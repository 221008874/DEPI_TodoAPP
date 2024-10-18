package com.abdoahmed.todoapp

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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.abdoahmed.todoapp.Database.Dao_Implementation.implementation
import com.abdoahmed.todoapp.Database.Settings
import com.abdoahmed.todoapp.Database.__DatabaseBuilder__
import com.abdoahmed.todoapp.databinding.FragmentSettingsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Settings : Fragment() {
private lateinit var bind:FragmentSettingsBinding
    private val username by lazy {
        arguments?.getString("username")
    }
    private val Navflag by lazy {
        arguments?.getInt("navFlag")
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bind= DataBindingUtil.inflate(inflater,R.layout.fragment_settings, container, false)


        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {



        super.onViewCreated(view, savedInstanceState)
        var context: Context = requireContext()
        val dp= __DatabaseBuilder__.getDatabaseInstance(context)
        val DaoImp= implementation(dp)

        loadDefaultSettings(DaoImp,username.toString())
        if (Navflag==1){
            bind.returnBack.setOnClickListener(){
                val action=SettingsDirections.actionSettings2ToMainBage(username)
                findNavController().navigate(action, NavOptions.Builder()
                    .setPopUpTo(R.id.settings, true)
                    .setEnterAnim(R.anim.to_right)  // Animation for entering the new fragment
                    .setExitAnim(R.anim.to_left)   // Animation for exiting the current fragment
                    .setPopEnterAnim(R.anim.from_left)  // Animation when popping back to the fragment
                    .setPopExitAnim(R.anim.from_right)// This removes the current fragment from the back stack
                    .build())
            }
        }
        else{
            bind.returnBack.setOnClickListener(){
                val action=SettingsDirections.actionSettings2ToCompletedTodos2(username.toString())
                findNavController().navigate(action, NavOptions.Builder()
                    .setPopUpTo(R.id.settings, true)
                    .setEnterAnim(R.anim.to_right)  // Animation for entering the new fragment
                    .setExitAnim(R.anim.to_left)   // Animation for exiting the current fragment
                    .setPopEnterAnim(R.anim.from_left)  // Animation when popping back to the fragment
                    .setPopExitAnim(R.anim.from_right)// This removes the current fragment from the back stack
                    .build())
            }

        }






        bind.updateSwich.setOnCheckedChangeListener(){_,isChecked ->
            if (isChecked==true){
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO){
                    DaoImp.__changeUpdateState(updateState = true, username = username.toString())
                    withContext(Dispatchers.Main) {

                    }

                }
            }
            else{
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO){
                    DaoImp.__changeUpdateState(updateState = false, username = username.toString())
                    withContext(Dispatchers.Main){

                    }

                }
            }

        }

        bind.daleteSwich.setOnCheckedChangeListener(){_,isChecked ->
            if (isChecked==true){
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO){
                    DaoImp.__UpdateDeleteState(deleteState = true, username = username.toString())
                    withContext(Dispatchers.Main) {

                    }
                }
            }
            else{
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO){
                    DaoImp.__UpdateDeleteState(deleteState = false, username = username.toString())
                    withContext(Dispatchers.Main){

                    }
                }
            }

        }




    }


    private fun loadDefaultSettings(Dao:implementation,username:String){


        viewLifecycleOwner.lifecycleScope.launch (Dispatchers.IO){
            val setting=Dao.__retrieveSettings(username)
            withContext(Dispatchers.Main){
                bind.updateSwich.isChecked=setting.updateState
                bind.daleteSwich.isChecked=setting.deleteState
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