package com.abdoahmed.todoapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.abdoahmed.todoapp.Database.Dao_Implementation.implementation
import com.abdoahmed.todoapp.Database.__DatabaseBuilder__
import com.abdoahmed.todoapp.TaskAlarm.Alarm.AlarmScheduler
import com.abdoahmed.todoapp.TaskAlarm.Alarm.RingtoneManagerSingleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AlarmTriggered : Fragment() {

    private var currentTodoId: Int = 0
    private var username_current: String? = null
    private var alarmType: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_alarm_tirigered, container, false)


        val args = AlarmTriggeredArgs.fromBundle(requireArguments())
        username_current = args.username
        currentTodoId = args.todoID
        alarmType = args.alarmType

        Toast.makeText(requireContext(), "Todo ID: $currentTodoId", Toast.LENGTH_SHORT).show()

        val stopAlarmButton: Button = view.findViewById(R.id.StopAlarm)

        stopAlarmButton.setOnClickListener {
            Toast.makeText(requireContext(), "Alarm stopped for Todo ID: $currentTodoId", Toast.LENGTH_SHORT).show()
            stopCurrentAlarm()
        }

        return view
    }

    private fun stopCurrentAlarm() {
        if (currentTodoId > 0) {
            if (alarmType=="Task End Alarm"){
                // Use AlarmScheduler to stop the alarm
                AlarmScheduler.stopAlarm(requireContext(), currentTodoId,username_current.toString())

                updateTodoAlarmStateToStop(todoID = currentTodoId, alarmType.toString())

                RingtoneManagerSingleton.ringtone?.stop()
                RingtoneManagerSingleton.ringtone = null
                // Navigate back or close the fragment after stopping the alarm
                val action = AlarmTriggeredDirections.actionAlarmTirigeredToMainBage(username_current)
                findNavController().navigate(action, NavOptions.Builder()
                    .setPopUpTo(R.id.viewTodoOrUpdate, true)
                    .setEnterAnim(R.anim.to_right)  // Animation for entering the new fragment
                    .setExitAnim(R.anim.to_left)   // Animation for exiting the current fragment
                    .setPopEnterAnim(R.anim.from_left)  // Animation when popping back to the fragment
                    .setPopExitAnim(R.anim.from_right)// This removes the current fragment from the back stack// This removes the current fragment from the back stack
                    .build())
            }
            else{

                RingtoneManagerSingleton.ringtone?.stop()
                RingtoneManagerSingleton.ringtone = null
                val action = AlarmTriggeredDirections.actionAlarmTirigeredToMainBage(username_current)
                findNavController().navigate(action, NavOptions.Builder()
                    .setPopUpTo(R.id.viewTodoOrUpdate, true)
                    .setEnterAnim(R.anim.to_right)  // Animation for entering the new fragment
                    .setExitAnim(R.anim.to_left)   // Animation for exiting the current fragment
                    .setPopEnterAnim(R.anim.from_left)  // Animation when popping back to the fragment
                    .setPopExitAnim(R.anim.from_right)// This removes the current fragment from the back stack// This removes the current fragment from the back stack
                    .build())

            }


        } else {
            Toast.makeText(requireContext(), "Error: Invalid Todo ID", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateTodoAlarmStateToStop(todoID: Int, AlarmType: String) {
        if (AlarmType == "Task End Alarm") {
            val dp = __DatabaseBuilder__.getDatabaseInstance(requireContext())
            val DaoImp = implementation(dp)
            GlobalScope.launch(Dispatchers.IO) {
                DaoImp.updateAlarmStatus(todoID, 0)
            }


        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Add a back press callback when the fragment is attached
        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing to disable the back button
            }
        })
    }
}
