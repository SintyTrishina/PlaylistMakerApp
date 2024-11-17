package com.example.playlistmakerapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet.Layout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val buttonBack = findViewById<ImageView>(R.id.back)

        buttonBack.setOnClickListener {
            finish()
        }

        val buttonShare = findViewById<FrameLayout>(R.id.buttonShare)

        buttonShare.setOnClickListener {
            val message = getString(R.string.shareText)
            val shareTitle = getString(R.string.shareTitle)
            val intent = Intent(Intent.ACTION_SEND)

            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, message)

            startActivity(Intent.createChooser(intent, shareTitle))
        }

        val buttonSupport = findViewById<FrameLayout>(R.id.buttonSupport)

        buttonSupport.setOnClickListener {
            val extraSubject = getString(R.string.extraSubject)
            val extraText = getString(R.string.extraText)
            val sendTitle = getString(R.string.sendTitle)
            val email = getString(R.string.email)

            intent.type = "text/plain"
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            intent.putExtra(Intent.EXTRA_SUBJECT, extraSubject)
            intent.putExtra(Intent.EXTRA_TEXT, extraText)
            startActivity(Intent.createChooser(intent, sendTitle))
        }

        val buttonAgreement = findViewById<FrameLayout>(R.id.buttonAgreement)

        buttonAgreement.setOnClickListener {
            val agreementText = getString(R.string.agreementText)
            val intent =
                Intent(Intent.ACTION_VIEW, Uri.parse(agreementText))
            startActivity(intent)
        }
    }
}