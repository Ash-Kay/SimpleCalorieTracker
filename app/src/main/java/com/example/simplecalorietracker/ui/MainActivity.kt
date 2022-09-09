package com.example.simplecalorietracker.ui

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavGraph
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
    private lateinit var navHostFragment: NavHostFragment

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment = supportFragmentManager.findFragmentById(
            R.id.navHostFragment
        ) as NavHostFragment
        setSupportActionBar(binding.toolBar)
        navController = navHostFragment.navController

        viewModel.viewState.observe(this) {
            renderViewState(it)
        }
    }

    private fun renderViewState(state: MainViewState) {
        when (state) {
            MainViewState.Loading -> {
                //TODO: show loader
                Timber.d("loading! token")
            }
            MainViewState.AuthCheck -> {
                viewModel.getAuthToken()
            }
            is MainViewState.AuthCheckSuccess -> {
                viewModel.updateUiBasedOnUserRole(state.userDetails.role)
            }
            is MainViewState.Error -> {
                //TODO: show retry
                Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
            }
            MainViewState.ShowAdminHome -> {
                val graph = getInflatedGraph()
                graph.setStartDestination(R.id.adminHomeFragment)
                updateGraphInHost(graph)
            }
            MainViewState.ShowUserHome -> {
                val graph = getInflatedGraph()
                graph.setStartDestination(R.id.userHomeFragment)
                updateGraphInHost(graph)
            }
        }
    }

    private fun getInflatedGraph(): NavGraph {
        val inflater = navHostFragment.navController.navInflater
        return inflater.inflate(R.navigation.main_navigation)
    }

    private fun updateGraphInHost(graph: NavGraph) {
        navHostFragment.navController.graph = graph
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
}