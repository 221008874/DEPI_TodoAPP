package com.abdoahmed.todoapp.TaskAlarm

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.abdoahmed.todoapp.R
import com.abdoahmed.todoapp.databinding.FragmentAlarmStartTimeBinding
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import com.abdoahmed.todoapp.Database.Dao_Implementation.implementation
import com.abdoahmed.todoapp.Database.TodoDataEntity
import com.abdoahmed.todoapp.Database.__DatabaseBuilder__
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar


class AlarmInputs : Fragment() {

    var startTimeFromDialog:String="x"
    var endTimeFromDialog:String="x"

lateinit var bind:FragmentAlarmStartTimeBinding
    private val username by lazy {
        arguments?.getString("username")
    }
    private val content by lazy {
        arguments?.getString("content")
    }
    private val title by lazy {
        arguments?.getString("Title")
    }
    val use: String
        get() = username ?: "0"

    val tit: String
        get() = title ?: "0"

    val cont: String
        get() = content ?: "0"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


         bind =DataBindingUtil.inflate(inflater,R.layout.fragment_alarm_start_time, container, false)

        Log.i("StartTime","$use       , $tit ,$cont")
        //timeBickerFormater()
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        bind.returnBack.setOnClickListener(){
            val action=AlarmInputsDirections.actionAlarmStartToAddNewTodo3(username)
            findNavController().navigate(action)
        }

        bind.startButton.setOnClickListener(){

            setUpStartTimePicker()


            //val argList: Array<String> = time()
           //GotoStartDate(argList)
        }

        bind.EndButton.setOnClickListener(){
            setUpEndtTimePicker()
        }


        bind.confirmButton.setOnClickListener(){
            var argsToBeSent= CollectDataToBeSent()
            if (argsToBeSent.get(0)=="null"){

            }
            else{
                SendDataToDatabase(argsToBeSent)
            }



        }

    }


    private fun setUpStartTimePicker() {
        // Get the current time as default values for the TimePickerDialog
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        var argsWithStartTime:Array<String>
        // Create and show the TimePickerDialog
        val timePickerDialog = TimePickerDialog(requireContext(),
            { _, hourOfDay, minute ->
                // Update the TextView with the selected time
                val formattedTime = String.format("%02d:%02d", hourOfDay, minute)
                startTimeFromDialog=formattedTime
                bind.startTimeTextView.text = "Selected Time: $formattedTime"
            }, currentHour, currentMinute, true
        )

        timePickerDialog.show()
    }
    private fun startTime(Time:String):Array<String>{
        return arrayOf(use,cont,tit,Time)
    }



    private fun setUpEndtTimePicker(){
        // Get the current time as default values for the TimePickerDialog
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        var argsWithStartAndEndTime:Array<String>

        val timePickerDialog = TimePickerDialog(requireContext(),
            { _, hourOfDay, minute ->

                val formattedTime = String.format("%02d:%02d", hourOfDay, minute)
                endTimeFromDialog=formattedTime
                bind.EndTimeTextView.text = "Selected Time: $formattedTime"
            }, currentHour, currentMinute, true
        )

        timePickerDialog.show()

    }






    private fun CollectDataToBeSent():Array<String>{

        if (startTimeFromDialog=="x"){
            Toast.makeText(activity, "Set Alarm start time", Toast.LENGTH_SHORT).show()
        }
        else if(endTimeFromDialog=="x"){
            Toast.makeText(activity, "Set Alarm end time", Toast.LENGTH_SHORT).show()
        }
        else{
            return arrayOf(use,cont,tit,startTimeFromDialog,endTimeFromDialog)
        }
        return arrayOf("null")
    }


    private fun SendDataToDatabase(args:Array<String>){
        var context: Context = requireContext()
        val dp = __DatabaseBuilder__.getDatabaseInstance(context)
        val DaoImp = implementation(dp)

        viewLifecycleOwner.lifecycleScope.launch (Dispatchers.IO){
            DaoImp.__insertNewTodo__(
                TodoDataEntity(
                username =args.get(0),
                Title = args.get(2),
                Content = args.get(1),
                startTime = args.get(3),
                EndTime = args.get(4),
                CompleteState = 0,
                    isActivated = 0)
            )
            withContext(Dispatchers.Main){
                val action=AlarmInputsDirections.actionAlarmStartToMainBage(args.get(0))
                findNavController().navigate(action, NavOptions.Builder()
                    .setPopUpTo(R.id.settings, true)
                    .setEnterAnim(R.anim.to_right)
                    .setExitAnim(R.anim.to_left)
                    .setPopEnterAnim(R.anim.from_left)
                    .setPopExitAnim(R.anim.from_right)
                    .build())
            }
        }


    }












/*


    fun timeBickerFormater(){
        //val is24Hour =(isUsing24HourFormat(requireContext()))
        //val clockType= if (is24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
        bind.timePicker.setIs24HourView(isUsing24HourFormat(requireContext()))
    }
    fun isUsing24HourFormat(context: Context): Boolean {
        return DateFormat.is24HourFormat(context)
    }



    private fun time(): Array<String> {
        var startHour = bind.timePicker.hour
        var startMinuts= bind.timePicker.minute
        Log.i("StartTime","$startMinuts       , $startHour")
        return arrayOf(use,cont,tit,startHour.toString()+":"+startMinuts.toString())
    }


    private fun GotoStartDate(argumentsList:Array<String>){

        val action=AlarmStartDirections.actionAlarmStartToAlarmEndTime(argumentsList)
        findNavController().navigate(action)

    }
*/

}