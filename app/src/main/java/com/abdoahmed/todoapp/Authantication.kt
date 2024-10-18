package com.abdoahmed.todoapp

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.abdoahmed.todoapp.Database.Dao_Implementation.implementation
import com.abdoahmed.todoapp.Database.UserAuthanticationDataEntity
import com.abdoahmed.todoapp.Database.__DatabaseBuilder__
import com.abdoahmed.todoapp.LiveDataViewModel.CommonViewModel
import com.abdoahmed.todoapp.LiveDataViewModel.CommonViewModelFactory
import com.abdoahmed.todoapp.databinding.FragmentAuthanticationBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Authantication : Fragment() {
    private lateinit var bind:FragmentAuthanticationBinding
    private lateinit var username:String
    private lateinit var password:String
    private lateinit var commonViewModel: CommonViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment



        bind=DataBindingUtil.inflate(inflater,R.layout.fragment_authantication, container, false)

        var context: Context = requireContext()
        val dp= __DatabaseBuilder__.getDatabaseInstance(context)
        val DaoImp= implementation(dp)

        val viewModelFactory = CommonViewModelFactory(DaoImp)

        commonViewModel = ViewModelProvider(this, viewModelFactory).get(CommonViewModel::class.java)

        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var context: Context = requireContext()
        val dp= __DatabaseBuilder__.getDatabaseInstance(context)
        val DaoImp= implementation(dp)

        bind.LoginButton.setOnClickListener{
            OnLoginCilced(DaoImp)
        }

        bind.signinButton.setOnClickListener{
            findNavController().navigate(R.id.action_authantication_to_registration)
        }

    }

    private fun OnLoginCilced(daoImp:implementation){

        username=bind.usernameInput.text.toString()
        password=bind.passwordInput.text.toString()
        dataInsertionAfterLoginClicked(username,password,daoImp)

        Log.i("test OnLoginCilced Method","OnLoginCilced Executed")


    }

    private fun dataInsertionAfterLoginClicked(username:String,password:String,daoImp: implementation){
        try {

            viewLifecycleOwner.lifecycleScope.launch (Dispatchers.Main){

                val retrievedUserData=daoImp.__RetrieveSpecificUserData__(username)

                if (username.isNotBlank()){
                    if (retrievedUserData!=null){

                        CheckPasswordCorrectness(retrievedUserData)

                    }
                    else{
                        bind.MessageToUser.setText("Username is not valid")
                        bind.MessageToUser.setTextColor(Color.RED)
                        Toast.makeText(context, "Username is not valid ", Toast.LENGTH_LONG).show()

                    }
                }
                else{

                    bind.MessageToUser.setText("Enter Username firstly")
                    bind.MessageToUser.setTextColor(Color.RED)


                }


            }

        }
        catch (e:Exception){
            bind.MessageToUser.setText("ERORR")
            bind.MessageToUser.setTextColor(Color.RED)
        }

    }

    private fun CheckPasswordCorrectness(userData:UserAuthanticationDataEntity){
        if (password.isNotBlank()){

            if (password==userData.password){
                redirectToMAinPage(username = userData.username)
            }
            else{
                bind.MessageToUser.setText("Wrong password")
                bind.MessageToUser.setTextColor(Color.RED)
            }

        }
        else{
            bind.MessageToUser.setText("Enter password firstly")
            bind.MessageToUser.setTextColor(Color.RED)
        }
    }


    private fun redirectToMAinPage(username: String){
        Toast.makeText(context, "Login successfully", Toast.LENGTH_LONG).show()

        commonViewModel.updateUsername(username)
        val action=AuthanticationDirections.actionAuthanticationToMainBage(username)

        findNavController().navigate(action, NavOptions.Builder()
            .setPopUpTo(R.id.authantication, true)
            .setEnterAnim(R.anim.__to_top)  // Animation for entering the new fragment
            .setExitAnim(R.anim.__to_bottom)   // Animation for exiting the current fragment
            .setPopEnterAnim(R.anim.__from_bottom)  // Animation when popping back to the fragment
            .setPopExitAnim(R.anim.__from_top)// This removes the current fragment from the back stack// This removes the current fragment from the back stack
            .build())

    }//Done


    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Add a back press callback when the fragment is attached
        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })
    }


}