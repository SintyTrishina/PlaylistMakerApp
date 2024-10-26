package com.example.playlistmakerapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val button1 = findViewById<Button>(R.id.button1)

        button1.setOnClickListener {
            val intent1 = Intent(this, Search::class.java)
            startActivity(intent1)
        }

        val button2 = findViewById<Button>(R.id.button2)

        button2.setOnClickListener {
            val intent2 = Intent(this, Media::class.java)
            startActivity(intent2)
        }

        val button3 = findViewById<Button>(R.id.button3)

        button3.setOnClickListener {
            val intent3 = Intent(this, SettingsActivity::class.java)
            startActivity(intent3)
        }


    }
}