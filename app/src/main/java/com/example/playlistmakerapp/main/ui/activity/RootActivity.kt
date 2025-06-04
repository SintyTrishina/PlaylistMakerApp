package com.example.playlistmakerapp.main.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmakerapp.R
import com.example.playlistmakerapp.databinding.ActivityRootBinding

class RootActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRootBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController =
            (supportFragmentManager.findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment).navController

        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destinantion, _ ->
            when (destinantion.id) {
                R.id.audioPlayerFragment -> {
                    binding.bottomNavigationView.visibility = View.GONE
                    binding.viewMenu.visibility = View.GONE
                }
                else -> {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                    binding.viewMenu.visibility = View.VISIBLE
                }
            }
        }
    }
}