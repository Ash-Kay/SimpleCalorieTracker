package com.example.simplecalorietracker.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.simplecalorietracker.R
import com.example.simplecalorietracker.databinding.ActivityMainBinding
import com.example.simplecalorietracker.utils.AuthUtils
import com.example.simplecalorietracker.utils.SharedPreferences
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val content: View = findViewById(android.R.id.content)
        viewModel.getAuthToken()

        viewModel.authToken.observe(this) {
            if (it == null) {
                Toast.makeText(this, "Error Logging In!", Toast.LENGTH_LONG).show()
            } else {
                AuthUtils.AUTH_TOKEN = it
                SharedPreferences.saveData(this, AuthUtils.AUTH_TOKEN_KEY, it)
            }
        }

        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.navHostFragment
        ) as NavHostFragment
        setSupportActionBar(binding.toolBar)
        navController = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
}