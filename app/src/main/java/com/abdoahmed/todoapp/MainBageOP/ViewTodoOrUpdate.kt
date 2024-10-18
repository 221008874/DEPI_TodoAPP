package com.abdoahmed.todoapp.MainBageOP

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.abdoahmed.todoapp.Database.Dao_Implementation.implementation
import com.abdoahmed.todoapp.Database.TodoDataEntity
import com.abdoahmed.todoapp.Database.__DatabaseBuilder__
import com.abdoahmed.todoapp.R
import com.abdoahmed.todoapp.databinding.FragmentViewTodoOrUpdateBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar


class ViewTodoOrUpdate : Fragment() {
    private lateinit var  bind :FragmentViewTodoOrUpdateBinding
    private lateinit var list_data: List<String>
    lateinit var actualDataFromDb:TodoDataEntity

    lateinit  var startTimeFromDialog:String// assign the start time value from Database
    lateinit var endTimeFromDialog:String   // assign the End time value from Database




    private val todoId by lazy {
        arguments?.getInt("TodoID")
    }
    private val updateActionState by lazy {
        arguments?.getBoolean("updateState")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bind= DataBindingUtil.inflate(inflater,
            R.layout.fragment_view_todo_or_update, container, false)

        return bind.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var context: Context = requireContext()
        val dp= __DatabaseBuilder__.getDatabaseInstance(context)
        val DaoImp= implementation(dp)




        retrieveTodoOnID(DaoImp)




        bind.returnBack.setOnClickListener(){
            val action=ViewTodoOrUpdateDirections.actionViewTodoOrUpdateToMainBage(username =actualDataFromDb.username.toString())
            findNavController().navigate(action, NavOptions.Builder()
                .setPopUpTo(R.id.viewTodoOrUpdate, true)
                .setEnterAnim(R.anim.to_right)  // Animation for entering the new fragment
                .setExitAnim(R.anim.to_left)   // Animation for exiting the current fragment
                .setPopEnterAnim(R.anim.from_left)  // Animation when popping back to the fragment
                .setPopExitAnim(R.anim.from_right)// This removes the current fragment from the back stack// This removes the current fragment from the back stack
                .build())
        }

        bind.Update.setOnClickListener(){
            if (isEnabledUpdateButton(view = view, isEnabled = updateActionState)==true){
                updateTodoData(DaoImp,actualDataFromDb)
            }
            else{
            }

        }



        bind.startButton.setOnClickListener(){

            setUpStartTimePicker()


            //val argList: Array<String> = time()
            //GotoStartDate(argList)
        }

        bind.EndButton.setOnClickListener(){
            setUpEndtTimePicker()
        }

    }

    private fun retrieveTodoOnID(Dao:implementation){


        viewLifecycleOwner.lifecycleScope.launch (Dispatchers.IO){
             actualDataFromDb=Dao.__SelectTodoOnId(todoId)
            Log.i("test real Data in view todo", actualDataFromDb.toString())


            withContext(Dispatchers.Main){
                bind.TitleInput.setText(actualDataFromDb.Title).toString()
                bind.ContentInput.setText(actualDataFromDb.Content).toString()
                bind.previousEndTime.setText("End time: ${actualDataFromDb.EndTime}").toString()
                bind.preivousStartTime.setText("Start time: ${actualDataFromDb.startTime}").toString()



                list_data= listOf(actualDataFromDb.Title,actualDataFromDb.Content,actualDataFromDb.startTime,actualDataFromDb.EndTime)

                startTimeFromDialog= list_data.getOrNull(2)?:""// assign the start time value from Database
                endTimeFromDialog=list_data.getOrNull(3)?:""
            }

        }

    }




    private fun updateTodoData(Dao: implementation, DataSet: TodoDataEntity) {
        val currentTitle: String = bind.TitleInput.text.toString()
        val currentContent: String = bind.ContentInput.text.toString()

        // Retrieve previous values from `list`
        val previousTitle: String = list_data.getOrNull(0) ?: ""
        val previousContent: String = list_data.getOrNull(1) ?: ""



        var startTimeDialog= startTimeFromDialog// assign the start time value from Database
        var endTimeDialog=endTimeFromDialog


        var realStartTime=list_data.getOrNull(2)?:""
        var realEndTime=list_data.getOrNull(3)?:""

        if (currentContent != previousContent || currentTitle != previousTitle||startTimeDialog!=realStartTime||endTimeDialog!=realEndTime) {
            // Log the values for debugging

            if (currentContent.isBlank()){
                Toast.makeText(context, "Todo content can not be empty", Toast.LENGTH_SHORT).show()
            }
            else if (currentTitle.isBlank()){
                Toast.makeText(context, "Todo title can not be empty", Toast.LENGTH_SHORT).show()
            }
            else{
                Log.d("UpdateTodoData", "Updating Todo ID: ${DataSet.id} with Content: $currentContent and Title: $currentTitle" )
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    try {


                        Dao.__UpdateTodo(
                            cont = currentContent,
                            Title = currentTitle,
                            id = DataSet.id,
                            startTime = startTimeDialog,
                            endTiem = endTimeDialog)




                        retrieveTodoOnID(Dao)

                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Todo Updated", Toast.LENGTH_LONG).show()

                        }
                    } catch (e: Exception) {
                        Log.e("UpdateError", "Error updating Todo: ${e.message}")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Update Failed", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }




        } else {
            Toast.makeText(context, "Nothing changed", Toast.LENGTH_LONG).show()
        }
    }






    private fun isEnabledUpdateButton(isEnabled:Boolean?,view: View):Boolean{

        var enabled:Boolean
        if (isEnabled==true){
            view.findViewById<Button>(R.id.Update).isEnabled=true
            Toast.makeText(view.context, "Warning Update enabled", Toast.LENGTH_SHORT).show()
            enabled=true
            return enabled
        }
        else{
            view.findViewById<Button>(R.id.Update).isEnabled=false
            view.findViewById<Button>(R.id.Update).isEnabled=true
            Toast.makeText(view.context, "Update Disabled from app settings", Toast.LENGTH_SHORT).show()
            enabled=false
            return enabled
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
        timePickerDialog.setOnCancelListener {
            // You can set the TextView to a default message or take any other action

            Toast.makeText(context, "Time selection was canceled", Toast.LENGTH_SHORT).show()
            startTimeFromDialog=list_data.getOrNull(2)?:""
            bind.startTimeTextView.setText("Selected Time: --:--")
        }
        timePickerDialog.show()
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
        timePickerDialog.setOnCancelListener {
            // You can set the TextView to a default message or take any other action

            Toast.makeText(context, "Time selection was canceled", Toast.LENGTH_SHORT).show()
            endTimeFromDialog=list_data.getOrNull(3)?:""
            bind.EndTimeTextView.setText("Selected Time: --:--")

        }

        timePickerDialog.show()

    }





}