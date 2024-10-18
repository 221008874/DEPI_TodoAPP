package com.abdoahmed.todoapp.MainBageOP

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.abdoahmed.todoapp.Database.Dao_Implementation.implementation
import com.abdoahmed.todoapp.Database.TodoDataEntity
import com.abdoahmed.todoapp.Database.__DatabaseBuilder__
import com.abdoahmed.todoapp.R
import com.abdoahmed.todoapp.databinding.FragmentAddNewTodoBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class AddNewTodo : Fragment() {
lateinit var bind:FragmentAddNewTodoBinding
    private val username by lazy {
        arguments?.getString("username")
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bind= DataBindingUtil.inflate(inflater,R.layout.fragment_add_new_todo, container, false)



        return bind.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var context: Context = requireContext()
        val dp = __DatabaseBuilder__.getDatabaseInstance(context)
        val DaoImp = implementation(dp)

        bind.Add.setOnClickListener() {
            //collectDataAndValidate(DaoImp)
            //val action=AddNewTodoDirections.actionAddNewTodo3ToAlarmStart(username.toString())
            val title = bind.TitleInput.text.toString()
            val content = bind.ContentInput.text.toString()
            if (title.isNotBlank()) {
                if (content.isNotBlank()) {
                    val action = AddNewTodoDirections.actionAddNewTodo3ToAlarmStart(
                        username.toString(),
                        title,
                        content
                    )
                    Log.d("add new todo args test","$username ,$title ,$content")

                    findNavController().navigate(action)
                } else {
                    Toast.makeText(context, "Please enter todo content", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, "Please enter todo title", Toast.LENGTH_LONG).show()
            }

        }



        bind.returnBack.setOnClickListener() {//Done
            val action = AddNewTodoDirections.actionAddNewTodo3ToMainBage(username)
            findNavController().navigate(
                action, NavOptions.Builder()
                    .setPopUpTo(R.id.addNewTodo, false)
                    .setEnterAnim(R.anim.to_right)  // Animation for entering the new fragment
                    .setExitAnim(R.anim.to_left)   // Animation for exiting the current fragment
                    .setPopEnterAnim(R.anim.from_left)  // Animation when popping back to the fragment
                    .setPopExitAnim(R.anim.from_right)// This removes the current fragment from the back stack
                    .build()
            )

        }
    }
override fun onAttach(context: Context) {
            super.onAttach(context)

            // Add a back press callback when the fragment is attached
            requireActivity().onBackPressedDispatcher.addCallback(
                this,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        // Do nothing to disable the back button
                    }
                })
        }

    }
