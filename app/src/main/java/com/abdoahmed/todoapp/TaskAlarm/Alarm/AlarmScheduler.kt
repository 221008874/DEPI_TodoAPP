package com.abdoahmed.todoapp.TaskAlarm.Alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.Calendar


object AlarmScheduler {


    fun scheduleAlarm(context: Context, hour: Int, minute: Int, alarmType: String, todoId: Int, username: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerTimeMillis = calculateTriggerTimeMillis(hour, minute)


        Log.d("AlarmScheduler", "Scheduling $alarmType for todoId: $todoId at $hour:$minute")

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("ALARM_TYPE", alarmType)
            putExtra("todoID", todoId) // Pass the Todo ID to the AlarmReceiver
            putExtra("username", username)
        }


        val requestCode = getRequestCode(alarmType, todoId)
        Log.d("AlarmScheduler", "Request code for $alarmType: $requestCode") // Log request code

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pendingIntent)
    }

    // Stop both start and end alarms for the given todoId
    fun stopAlarm(context: Context, todoId: Int,username: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val startIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("ALARM_TYPE", "Task Start Alarm")
            putExtra("todoID", todoId)
            putExtra("username", username)
        }
        val startRequestCode = getRequestCode("Task Start Alarm", todoId)
        val startPendingIntent = PendingIntent.getBroadcast(context, startRequestCode, startIntent, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
        if (startPendingIntent != null) {
            alarmManager.cancel(startPendingIntent)
            startPendingIntent.cancel()
            Log.d("AlarmScheduler", "Stopped start alarm for todoId: $todoId with request code: $startRequestCode")
        }

        // Stop the end alarm
        val endIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("ALARM_TYPE", "Task End Alarm")
            putExtra("todoID", todoId)
            putExtra("username", username)
        }
        val endRequestCode = getRequestCode("Task End Alarm", todoId)
        val endPendingIntent = PendingIntent.getBroadcast(context, endRequestCode, endIntent, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
        if (endPendingIntent != null) {
            alarmManager.cancel(endPendingIntent)
            endPendingIntent.cancel()
            Log.d("AlarmScheduler", "Stopped end alarm for todoId: $todoId with request code: $endRequestCode")
        }
    }


    private fun getRequestCode(alarmType: String, todoId: Int): Int {
        return when (alarmType) {
            "Task Start Alarm" -> todoId * 2
            "Task End Alarm" -> todoId * 2 + 1
            else -> -1
        }
    }

    private fun calculateTriggerTimeMillis(hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            Log.d("AlarmScheduler", "Scheduled time is in the past. Adjusting to the next day.")
        }

        return calendar.timeInMillis
    }





}
