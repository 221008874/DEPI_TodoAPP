package com.abdoahmed.todoapp.completedTodoOP

import CompleedTodosRecycularViewAdapter
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.abdoahmed.todoapp.Database.Dao_Implementation.implementation
import com.abdoahmed.todoapp.Database.TodoDataEntity
import com.abdoahmed.todoapp.Database.__DatabaseBuilder__
import com.abdoahmed.todoapp.LiveDataViewModel.CommonViewModelFactory
import com.abdoahmed.todoapp.MainBageDirections
import com.abdoahmed.todoapp.MainBageOP.AddNewTodoDirections
import com.abdoahmed.todoapp.R


import com.abdoahmed.todoapp.databinding.FragmentCompletedTodosBinding

import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CompletedTodos : Fragment() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var toggle: ActionBarDrawerToggle
    private  var deleteActionState:Boolean = false
    private  var updateActionState:Boolean = false
    var savedData:ArrayList<TodoDataEntity>?=null


    private val username by lazy {
        arguments?.getString("username")
    }
    private lateinit var bind :FragmentCompletedTodosBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind= DataBindingUtil.inflate(inflater,R.layout.fragment_completed_todos, container, false)




        return bind.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        bind.Return.setOnClickListener(){
            val action = CompletedTodosDirections.actionCompletedTodosToMainBage(username)
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


        var context: Context = requireContext()
        val dp= __DatabaseBuilder__.getDatabaseInstance(context)
        val DaoImp= implementation(dp)

        val viewModelFactory = CommonViewModelFactory(DaoImp)





        applySettingsOnMainPageCreated(DaoImp)








        /*
                // Observe LiveData
                commonViewModel.username.observe(viewLifecycleOwner, Observer { currentUsername ->
                    Log.d("ExampleFragment", "Observed Username: $currentUsername")
                    username = currentUsername
                    // Update UI with the username
                    view.findViewById<TextView>(R.id.title).text = currentUsername
                })


        */

        retrieveAllDataLoadDataOnRecView(view,username.toString(),DaoImp)















//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$  drawer layout section



        drawerLayout = view.findViewById(R.id.drawer_layout)
        navView = view.findViewById(R.id.nav_view)
        toolbar = view.findViewById(R.id.toolbar)


        (activity as AppCompatActivity).setSupportActionBar(toolbar)


        toggle = ActionBarDrawerToggle(
            activity,
            drawerLayout,
            toolbar,
            R.string.nav_close,
            R.string.nav_open,


            )
        drawerLayout.addDrawerListener(toggle)





        toggle.syncState()

//*********************** Start of navigation header section



        //view.findViewById<TextView>(R.id.usernameView).setText(username)
        //navView.findViewById<TextView>(R.id.usernameView).setText(username)
        //drawerLayout.findViewById<TextView>(R.id.usernameView).setText(username)
        var headerView=navView.getHeaderView(0)

        headerView.findViewById<TextView>(R.id.usernameView).setText("user ID: $username")

        setupDrawerContent(navView,view)

//*************************** end of navigation header section
    }


    private fun setupDrawerContent(navigationView: NavigationView,view: View) {

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.logOut->{
                    Toast.makeText(activity, "Log out", Toast.LENGTH_SHORT).show()
                    val action=CompletedTodosDirections.actionCompletedTodosToAuthantication(username)
                    findNavController().navigate(action,NavOptions.Builder()
                        .setPopUpTo(R.id.mainBage, true)
                        .setEnterAnim(R.anim.__to_top)  // Animation for entering the new fragment
                        .setExitAnim(R.anim.__to_bottom)   // Animation for exiting the current fragment
                        .setPopEnterAnim(R.anim.__from_bottom)  // Animation when popping back to the fragment
                        .setPopExitAnim(R.anim.__from_top)// This removes the current fragment from the back stack
                        .build())

                }
                R.id.settings->{
                    val action=CompletedTodosDirections.actionCompletedTodosToSettings2(username.toString(),2)
                    findNavController().navigate(action,NavOptions.Builder()
                        .setPopUpTo(R.id.mainBage, true)
                        .setEnterAnim(R.anim.to_right)  // Animation for entering the new fragment
                        .setExitAnim(R.anim.to_left)   // Animation for exiting the current fragment
                        .setPopEnterAnim(R.anim.from_left)  // Animation when popping back to the fragment
                        .setPopExitAnim(R.anim.from_right)// This removes the current fragment from the back stack
                        .build())
                }//Done
            }
            drawerLayout.closeDrawers()
            true
        }

    }



    private fun retrieveAllDataLoadDataOnRecView(view: View,username:String,Dao:implementation){

        // Virtual Data for Fallback
        val virtualData: ArrayList<TodoDataEntity> = arrayListOf(
            TodoDataEntity(Title = "Buy", Content = "Get milk and eggs", username = "00", startTime = "0", EndTime = "0", CompleteState = 0, isActivated = 0)
        )
        Log.i("test virtual Data", virtualData.toString())

        // Use Dispatchers.IO for Database Operations
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val actualDataFromDb: ArrayList<TodoDataEntity> =ArrayList(Dao.__retrieveAllTodoS__(username))
                savedData=actualDataFromDb
                Log.i("test real Data", actualDataFromDb.toString())

                // Switch to Main Dispatcher to Update UI
                withContext(Dispatchers.Main) {
                    val recyclerView = bind.ResyView
                    recyclerView.layoutManager = LinearLayoutManager(view.context)

                    if (actualDataFromDb.isNullOrEmpty()) {
                        val adapter = CompleedTodosRecycularViewAdapter(virtualData, true,findNavController(),false,false)//New User
                        recyclerView.adapter = adapter
                    } else {

                        // Sort and use the actual data from the database
                        val sortedData = ArrayList(actualDataFromDb.sortedByDescending { it.id })

                        val adapter = CompleedTodosRecycularViewAdapter( checkCompleteStateBeforeLoadingOnMain(sortedData), false,findNavController(), isDeleteEnabled = deleteActionState, isUpdateEnabled = updateActionState)
                        recyclerView.adapter = adapter
                    }
                }
            } catch (e: Exception) {
                Log.e("Data Load Error", e.message ?: "Unknown error occurred")
            }
        }
    }







    private fun checkCompleteStateBeforeLoadingOnMain(Data:ArrayList<TodoDataEntity>):ArrayList<TodoDataEntity>{
        var completedArray:ArrayList<TodoDataEntity> = arrayListOf(
            TodoDataEntity(Title = "Buy", Content = "Get milk and eggs", username = "00", startTime = "0", EndTime = "0", CompleteState = 0, isActivated = 0)
        )
        var NotCompletedArray:ArrayList<TodoDataEntity> = arrayListOf(
            TodoDataEntity(Title = "Buy", Content = "Get milk and eggs", username = "00", startTime = "0", EndTime = "0", CompleteState = 0, isActivated = 0)
        )
        completedArray.clear()
        NotCompletedArray.clear()

        for (todo in Data){
            if (todo.CompleteState==1){

                completedArray.add(todo)
            }
            else{
                NotCompletedArray.add(todo)
            }
        }


        return completedArray

    }
















    private fun applySettingsOnMainPageCreated(Dao: implementation){

        viewLifecycleOwner.lifecycleScope.launch (Dispatchers.IO){
            val settings=Dao.__retrieveSettings(username.toString())
            deleteActionState=settings.deleteState
            updateActionState=settings.updateState
            Log.i("setting in main","${settings.toString()}")
        }




    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Add a back press callback when the fragment is attached
        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing to disable the back button

                Toast.makeText(activity, "Log out to exit", Toast.LENGTH_SHORT).show()

            }
        })
    }



}




