package com.abdoahmed.todoapp

import android.content.Context
import android.graphics.Color
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
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.abdoahmed.todoapp.Database.Dao_Implementation.implementation
import com.abdoahmed.todoapp.Database.TodoDataEntity
import com.abdoahmed.todoapp.Database.__DatabaseBuilder__
import com.abdoahmed.todoapp.LiveDataViewModel.CommonViewModel
import com.abdoahmed.todoapp.LiveDataViewModel.CommonViewModelFactory
import com.abdoahmed.todoapp.recycular_view_Adapter.RecycularViewAdapter
import com.abdoahmed.todoapp.databinding.FragmentMainBageBinding
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.abdoahmed.todoapp.Database.Settings

class MainBage : Fragment() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var toggle: ActionBarDrawerToggle
    private  var deleteActionState:Boolean = false
    private  var updateActionState:Boolean = false


    private lateinit var commonViewModel: CommonViewModel
    private lateinit var completedTodos:ArrayList<TodoDataEntity>
private lateinit var bind:FragmentMainBageBinding

 var savedData:ArrayList<TodoDataEntity>?=null


    private val username by lazy {
        arguments?.getString("username")
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind= DataBindingUtil.inflate(inflater,R.layout.fragment_main_bage, container, false)




        return bind.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        var context: Context = requireContext()
        val dp= __DatabaseBuilder__.getDatabaseInstance(context)
        val DaoImp= implementation(dp)

        val viewModelFactory = CommonViewModelFactory(DaoImp)
        commonViewModel = ViewModelProvider(this, viewModelFactory).get(CommonViewModel::class.java)



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



        bind.ViewCompletedTodos.setOnClickListener(){
            val action=MainBageDirections.actionMainBageToCompletedTodos(username.toString())
            findNavController().navigate(action, NavOptions.Builder()
                .setPopUpTo(R.id.mainBage, true)
                .setEnterAnim(R.anim.to_right)  // Animation for entering the new fragment
                .setExitAnim(R.anim.to_left)   // Animation for exiting the current fragment
                .setPopEnterAnim(R.anim.from_left)  // Animation when popping back to the fragment
                .setPopExitAnim(R.anim.from_right)// This removes the current fragment from the back stack
                .build())
        }

        bind.addNewTodo.setOnClickListener {
           val action=MainBageDirections.actionMainBageToAddNewTodo3(username)

            findNavController().navigate(action, NavOptions.Builder()
                .setPopUpTo(R.id.mainBage, true)
                    .setEnterAnim(R.anim.to_right)  // Animation for entering the new fragment
                    .setExitAnim(R.anim.to_left)   // Animation for exiting the current fragment
                    .setPopEnterAnim(R.anim.from_left)  // Animation when popping back to the fragment
                    .setPopExitAnim(R.anim.from_right)// This removes the current fragment from the back stack
                    .build())


        }









//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$  drawer layout section



        drawerLayout = view.findViewById(R.id.drawer_layout)
        navView = view.findViewById(R.id.nav_view)
        toolbar = view.findViewById(R.id.toolbar)

        toolbar.setTitleTextColor(Color.LTGRAY)
        toolbar.setTitle("TodoMaster")





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
                    val action=MainBageDirections.actionMainBageToAuthantication(username)
                    findNavController().navigate(action,NavOptions.Builder()
                        .setPopUpTo(R.id.mainBage, true)
                        .setEnterAnim(R.anim.__to_top)  // Animation for entering the new fragment
                        .setExitAnim(R.anim.__to_bottom)   // Animation for exiting the current fragment
                        .setPopEnterAnim(R.anim.__from_bottom)  // Animation when popping back to the fragment
                        .setPopExitAnim(R.anim.__from_top)// This removes the current fragment from the back stack
                        .build())

                }
                R.id.settings->{
                    val action=MainBageDirections.actionMainBageToSettings2(username.toString(),1)
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
                        val adapter = RecycularViewAdapter(virtualData, true,findNavController(),false,false)//New User
                        recyclerView.adapter = adapter
                    } else {

                        // Sort and use the actual data from the database
                        val sortedData = ArrayList(actualDataFromDb.sortedByDescending { it.id })

                        val adapter = RecycularViewAdapter( checkCompleteStateBeforeLoadingOnMain(sortedData), false,findNavController(), isDeleteEnabled = deleteActionState, isUpdateEnabled = updateActionState)
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
        completedTodos=completedArray

        return NotCompletedArray

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















