package com.example.playlistmakerapp.ui.main_screen

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmakerapp.R
import com.example.playlistmakerapp.ui.media.MediaActivity
import com.example.playlistmakerapp.ui.search.SearchActivity
import com.example.playlistmakerapp.ui.settings.SettingsActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button1 = findViewById<Button>(R.id.button1)

        button1.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        val button2 = findViewById<Button>(R.id.button2)

        button2.setOnClickListener {
            startActivity(Intent(this, MediaActivity::class.java))
        }

        val button3 = findViewById<Button>(R.id.button3)

        button3.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}