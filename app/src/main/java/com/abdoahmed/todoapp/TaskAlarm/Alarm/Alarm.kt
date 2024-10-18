package com.abdoahmed.todoapp.TaskAlarm.Alarm

import android.content.Context
import android.util.Log
import androidx.navigation.NavController
import com.abdoahmed.todoapp.Database.Dao_Implementation.implementation
import com.abdoahmed.todoapp.Database.__DatabaseBuilder__
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Alarm(private val context: Context, private val id: Int, private val navController: NavController, private val username: String) {

    private val dp = __DatabaseBuilder__.getDatabaseInstance(context)
    private val DaoImp = implementation(dp)


    fun createCalenderOp() {
        Log.d("Alarm", "Creating calendar operation for TODO ID: $id with username: $username")
        getData() // Fetch task data from the database
    }

    // Fetches task data and schedules alarms
    private fun getData() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val retrieved = DaoImp.__SelectTodoOnId(id)
                Log.d("Alarm", "Retrieved TODO Data: $retrieved")

                withContext(Dispatchers.Main) {
                    val startTime = TimeSlicer(retrieved.startTime)
                    val endTime = TimeSlicer(retrieved.EndTime)


                    Log.d("Alarm", "Start Time: $startTime, End Time: $endTime")


                    Log.d("Alarm         ddd", "Start Time: ${retrieved.startTime}, End Time: ${retrieved.EndTime}")
                    AssignAlarmDataToConvertedList_And_CreateCalender(startTime, endTime)
                }
            } catch (e: Exception) {
                Log.e("Alarm", "Error fetching data: ${e.message}", e)
            }
        }
    }

    // Helper function to slice the time string (HH:mm) into hour and minute
    private fun TimeSlicer(time: String): List<Int> {
        Log.d("Alarm", "Slicing time string: $time")
        if (time.isEmpty()) {
            throw IllegalArgumentException("Time string is empty.")
        }

        val parts = time.split(":")
        if (parts.size != 2) {
            throw IllegalArgumentException("Invalid time format. Time should be in the format HH:mm.")
        }

        val hour = parts[0].toIntOrNull() ?: throw IllegalArgumentException("Invalid hour format.")
        val minute = parts[1].toIntOrNull() ?: throw IllegalArgumentException("Invalid minute format.")

        return listOf(hour, minute)
    }


    private fun AssignAlarmDataToConvertedList_And_CreateCalender(startTimeConverted: List<Int>, endTimeConverted: List<Int>) {
        val startHour = startTimeConverted[0]
        val startMinute = startTimeConverted[1]
        val endHour = endTimeConverted[0]
        val endMinute = endTimeConverted[1]


        Log.d("Alarm", "Scheduling alarms with Start: $startHour:$startMinute, End: $endHour:$endMinute")


        AlarmScheduler.scheduleAlarm(context, startHour, startMinute, "Task Start Alarm", id, username)
        AlarmScheduler.scheduleAlarm(context, endHour, endMinute, "Task End Alarm", id, username)
    }


    fun stopSpecificAlarm() {
        Log.d("Alarm", "Stopping alarm for TODO ID: $id")
        updateAlarmStatus(id, 0) // Update the alarm status to inactive
        AlarmScheduler.stopAlarm(context, id,username) // Stop the alarm using AlarmScheduler
    }


    private fun updateAlarmStatus(todoId: Int, isActive: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            Log.d("Alarm", "Updating alarm status for TODO ID: $todoId to isActive: $isActive")
            DaoImp.updateAlarmStatus(todoId, isActive)
        }
    }
}
