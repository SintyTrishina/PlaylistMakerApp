package com.example.playlistmakerapp.ui.main_screen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmakerapp.databinding.ActivityMainBinding
import com.example.playlistmakerapp.ui.media.MediaActivity
import com.example.playlistmakerapp.ui.search.SearchActivity
import com.example.playlistmakerapp.ui.settings.SettingsActivity

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