package com.abdoahmed.todoapp.TaskAlarm.Alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.abdoahmed.todoapp.Database.Dao_Implementation.implementation
import com.abdoahmed.todoapp.Database.__DatabaseBuilder__
import com.abdoahmed.todoapp.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlarmReceiver : BroadcastReceiver() {
    private val handler = Handler(Looper.getMainLooper())

    override fun onReceive(context: Context, intent: Intent) {
        val dp = __DatabaseBuilder__.getDatabaseInstance(context)
        val DaoImp = implementation(dp)


        val alarmType = intent.getStringExtra("ALARM_TYPE")
        val requestCode = intent.getIntExtra("todoID", 0)
        val username = intent.getStringExtra("username")


        Log.d("AlarmReceiver", "Received $alarmType alarm for TODO ID: $requestCode")

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val todo = DaoImp.__SelectTodoOnId(requestCode)


                if (todo.isActivated == 1) {
                    withContext(Dispatchers.Main) {

                        Log.d("AlarmReceiver", "$alarmType triggered for TODO ID: $requestCode")

                        // Start the MainActivity when the alarm goes off
                        val mainActivityIntent = Intent(context, MainActivity::class.java).apply {
                            putExtra("ALARM_TYPE", alarmType)
                            putExtra("todoID", requestCode)
                            putExtra("username", username)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        }
                        context.startActivity(mainActivityIntent)

                        // Play the alarm sound using RingtoneManagerSingleton
                        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                        RingtoneManagerSingleton.ringtone = RingtoneManager.getRingtone(context, alarmSound)
                        RingtoneManagerSingleton.ringtone?.play()

                        handler.postDelayed({
                            stopRingtone()
                        }, 50000)
                    }
                } else {
                    Log.d("AlarmReceiver", "Alarm for TODO ID: $requestCode is deactivated.")
                }
            } catch (e: Exception) {
                Log.e("AlarmReceiver", "Error triggering alarm: ${e.message}", e)
            }
        }
    }


    private fun stopRingtone() {
        RingtoneManagerSingleton.ringtone?.stop()
        RingtoneManagerSingleton.ringtone = null
    }
}
