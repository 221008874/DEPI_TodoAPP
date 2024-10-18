package com.abdoahmed.todoapp.completedTodoOP

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
import com.abdoahmed.todoapp.databinding.FragmentViewCompletedTodosBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ViewCompletedTodos : Fragment() {


    private lateinit var  bind : FragmentViewCompletedTodosBinding
    private lateinit var list: List<String>
    lateinit var actualDataFromDb: TodoDataEntity
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
            R.layout.fragment_view_completed_todos, container, false)

        return bind.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var context: Context = requireContext()
        val dp= __DatabaseBuilder__.getDatabaseInstance(context)
        val DaoImp= implementation(dp)




        val list=retrieveTodoOnID(DaoImp)


        bind.returnBack.setOnClickListener(){
            val action= ViewCompletedTodosDirections.actionViewCompletedTodosToCompletedTodos(username =actualDataFromDb.username.toString())
            findNavController().navigate(action, NavOptions.Builder()
                .setPopUpTo(R.id.viewTodoOrUpdate, true)
                .setEnterAnim(R.anim.to_right)  // Animation for entering the new fragment
                .setExitAnim(R.anim.to_left)   // Animation for exiting the current fragment
                .setPopEnterAnim(R.anim.from_left)  // Animation when popping back to the fragment
                .setPopExitAnim(R.anim.from_right)// This removes the current fragment from the back stack// This removes the current fragment from the back stack
                .build())
        }



    }

    private fun retrieveTodoOnID(Dao: implementation){


        viewLifecycleOwner.lifecycleScope.launch (Dispatchers.IO){
            actualDataFromDb=Dao.__SelectTodoOnId(todoId)
            Log.i("test real Data in view todo", actualDataFromDb.toString())


            withContext(Dispatchers.Main){
                val TitleFirstDisplay = bind.TitleInput.setText(actualDataFromDb.Title).toString()
                val ContentFirstDisplay= bind.ContentInput.setText(actualDataFromDb.Content).toString()
                bind.preivousStartTime.setText("Start time: ${actualDataFromDb.startTime}")
                bind.previousEndTime.setText("End time: ${actualDataFromDb.EndTime}")

                list= listOf(actualDataFromDb.Title,actualDataFromDb.Content)
            }

        }

    }







    private fun isEnabledUpdateButton(isEnabled:Boolean?,view: View):Boolean{

        var enabled:Boolean
        if (isEnabled==true){
            view.findViewById<Button>(R.id.Update).isEnabled=true
            Toast.makeText(view.context, "Warning Update enabled", Toast.LENGTH_LONG).show()
            enabled=true
            return enabled
        }
        else{
            view.findViewById<Button>(R.id.Update).isEnabled=false
            view.findViewById<Button>(R.id.Update).isEnabled=true
            Toast.makeText(view.context, "Update Disabled from app settings", Toast.LENGTH_LONG).show()
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


}