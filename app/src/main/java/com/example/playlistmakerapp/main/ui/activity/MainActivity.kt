package com.example.playlistmakerapp.main.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmakerapp.databinding.ActivityMainBinding
import com.example.playlistmakerapp.main.ui.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModel<MainViewModel>() { parametersOf(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


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