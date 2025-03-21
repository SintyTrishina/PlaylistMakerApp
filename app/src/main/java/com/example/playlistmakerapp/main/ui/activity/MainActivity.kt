package com.example.playlistmakerapp.main.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmakerapp.databinding.ActivityMainBinding
import com.example.playlistmakerapp.main.ui.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            MainViewModel.getViewModelFactory(this)
        )[MainViewModel::class.java]

        binding.button1.setOnClickListener {
            viewModel.startSearch()
        }

        binding.button2.setOnClickListener {
            viewModel.startMedia()
        }

        binding.button3.setOnClickListener {
            viewModel.startSettings()
        }
    }
}