package com.example.transactionsapiintegration

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.transactionsapiintegration.utils.SharedPref
import com.example.transactionsapiintegration.databinding.ActivityMainBinding
import com.example.transactionsapiintegration.utils.Constants
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private val pref : SharedPref by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding = ActivityMainBinding.inflate(layoutInflater)
       setContentView(binding.root)
       initUi()
    }

    private fun initUi() {
        setNavigationController()
    }

    private fun setNavigationController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_welcome) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.app_navigation)

        val navToScreen = if (pref.getString(Constants.TOKEN).isNullOrEmpty()) {
            R.id.loginFragment
        } else {
            R.id.transactionsFragment
        }

        graph.setStartDestination(navToScreen)
        navController = navHostFragment.navController
        navController.setGraph(graph, intent.extras)

        navController.navigate(navToScreen)

    }
}