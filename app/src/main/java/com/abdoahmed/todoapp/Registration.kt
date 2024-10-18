package com.abdoahmed.todoapp

import android.app.Application
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.abdoahmed.todoapp.Database.Dao_Implementation.implementation
import com.abdoahmed.todoapp.Database.Settings
import com.abdoahmed.todoapp.Database.UserAuthanticationDataEntity
import com.abdoahmed.todoapp.Database.__DatabaseBuilder__
import com.abdoahmed.todoapp.databinding.FragmentAuthanticationBinding
import com.abdoahmed.todoapp.databinding.FragmentRegistrationBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Registration : Fragment() {
    private lateinit var bind: FragmentRegistrationBinding
    //private lateinit var username:String
    private lateinit var password:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        bind= DataBindingUtil.inflate(inflater,R.layout.fragment_registration, container, false)
        return bind.root
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        var context: Context = requireContext()
        val dp=__DatabaseBuilder__.getDatabaseInstance(context)
        val DaoImp=implementation(dp)


        bind.SigninButton.setOnClickListener{
            OnSigninCilced(DaoImp)
        }

        bind.loginButton.setOnClickListener{
            findNavController().navigate(R.id.action_registration_to_authantication)
        }

    }


    private fun OnSigninCilced(DaoImp:implementation){

        var username:String
        var password:String
        username=bind.usernameInput.text.toString()
        password=bind.passwordInput.text.toString()

        dataInsertionAfterSigninClicked(username=username,password=password,DaoImp=DaoImp)

        Log.i("test OnSigninCilced Method","OnSigninCilced Executed")

    }


    private fun dataInsertionAfterSigninClicked(username:String,password:String,DaoImp:implementation){

    try {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main){

            if (username.isBlank()&&password.isBlank())
            {
                bind.MessageToUser.setText("Data fileds can not be empty")
                bind.MessageToUser.setTextColor(Color.RED)
            }
            else
            {
                if (password.length<=8)
                {
                    bind.MessageToUser.setText("Password length cannot be less than 8 character")
                    bind.MessageToUser.setTextColor(Color.RED)
                }
                else
                {
                    val insertionFlag=DaoImp.__insertNewUserData__(UserAuthanticationDataEntity(username=username, password = password))
                    if (insertionFlag==0L)
                    {
                        bind.MessageToUser.setText("Error username already exist")
                        bind.MessageToUser.setTextColor(Color.RED)
                    }
                    else
                    {
                        redirectToMAinPage(username,DaoImp)

                    }
                }
            }



        }
    }
    catch (e:Exception){
        Log.i("test dataInsertionAfterSigninClicked Method","Erorr")
    }

    }

    private fun redirectToMAinPage(username:String,DaoImp: implementation){
        initalizeDefaultSettings(DaoImp,username)
        val action=RegistrationDirections.actionRegistrationToMainBage(username)
        findNavController().navigate(action, NavOptions.Builder()
            .setPopUpTo(R.id.registration, true)
            .setEnterAnim(R.anim.__to_top)  // Animation for entering the new fragment
            .setExitAnim(R.anim.__to_bottom)   // Animation for exiting the current fragment
            .setPopEnterAnim(R.anim.__from_bottom)  // Animation when popping back to the fragment
            .setPopExitAnim(R.anim.__from_top)// This removes the current fragment from the back stack// This removes the current fragment from the back stack
            .build())
    }

    private fun initalizeDefaultSettings(DaoImp: implementation,username: String){
        viewLifecycleOwner.lifecycleScope.launch (Dispatchers.IO){
            DaoImp.__insertDafaultSettings__(settings = com.abdoahmed.todoapp.Database.Settings(deleteState = false, updateState = false, username = username))
        }
    }


}