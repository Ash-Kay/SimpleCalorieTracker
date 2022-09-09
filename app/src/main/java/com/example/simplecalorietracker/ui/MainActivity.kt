package com.example.simplecalorietracker.ui

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.simplecalorietracker.R
import com.example.simplecalorietracker.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.navHostFragment
        ) as NavHostFragment
        setSupportActionBar(binding.toolBar)
        navController = navHostFragment.navController

        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.main_navigation)
//        graph.setStartDestination(R.id.addFoodEntryFragment)
//        navHostFragment.navController.graph = graph//this stat nav
//        NavigationUI.setupActionBarWithNavController(this, navController)

        viewModel.viewState.observe(this) {
            renderViewState(it)
        }
    }

    private fun renderViewState(state: MainViewState) {
        when (state) {
            MainViewState.Loading -> {
                Timber.d("loading! token")
            }
            MainViewState.AuthCheck -> {
                viewModel.getAuthToken()
            }
            is MainViewState.AuthCheckSuccess -> {

            }
            is MainViewState.Error -> {
                Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
            }
            MainViewState.ShowAdminHome -> TODO()
            MainViewState.ShowUserHome -> TODO()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
}