package com.example.playlistmakerapp.main.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmakerapp.databinding.ActivityMainBinding
import com.example.playlistmakerapp.media.ui.MediaActivity
import com.example.playlistmakerapp.search.ui.activity.SearchActivity
import com.example.playlistmakerapp.settings.ui.activity.SettingsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button1.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        binding.button2.setOnClickListener {
            startActivity(Intent(this, MediaActivity::class.java))
        }

        binding.button3.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}