package com.abdoahmed.todoapp

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.abdoahmed.todoapp.databinding.ActivityMainBinding
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    lateinit var binding: ActivityMainBinding
    private var alarmType: String? = null // Store alarmType
    private var currentTodoId: Int = 0 // Variable to store current Todo ID
    private var username: String? = null // Variable to store username

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        installSplashScreen()


        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)


        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION  // Hide navigation bar
                        or View.SYSTEM_UI_FLAG_FULLSCREEN       // Hide status bar
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // Keep immersive mode
                )


        val lottieBackground: LottieAnimationView = binding.lottieBackground
        lottieBackground.setAnimation(R.raw.bg)
        lottieBackground.playAnimation()
        lottieBackground.repeatCount = LottieDrawable.INFINITE
        lottieBackground.scaleType = ImageView.ScaleType.CENTER_CROP


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController


        handleIntent(intent)


        if (alarmType == null) {
            navController.navigate(R.id.authantication)
        }

        setLocale(this, "en")
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }


    private fun handleIntent(intent: Intent) {
        alarmType = intent.getStringExtra("ALARM_TYPE")
        currentTodoId = intent.getIntExtra("todoID", 0)
        username = intent.getStringExtra("username")


        alarmType?.let { navigateToFragment(it) }
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent) // Handle the new intent
    }

    private fun navigateToFragment(alarmType: String) {
        when (alarmType) {
            "Task Start Alarm" -> {
                // Log the values before navigating
                Log.d("NavigationTest", "Navigating to Task Start Alarm with TODO_ID: $currentTodoId and username: $username")


                navController.navigate(R.id.action_global_alarmTriggered, Bundle().apply {
                    putInt("todoID", currentTodoId) // Pass the current TODO ID
                    putString("username", username) // Pass the username
                    putString("alarmType", alarmType)
                })
            }
            "Task End Alarm" -> {
                // Log the values before navigating
                Log.d("NavigationTest", "Navigating to Task End Alarm with TODO_ID: $currentTodoId and username: $username")


                navController.navigate(R.id.action_global_alarmTriggered, Bundle().apply {
                    putInt("todoID", currentTodoId)
                    putString("username", username)
                    putString("alarmType", alarmType)
                })
            }
            else -> {
                Log.d("NavigationTest", "Unknown alarmType: $alarmType")
            }
        }
    }

    fun setLocale(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        context.createConfigurationContext(config)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(config)
        } else {
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }
    }
}
