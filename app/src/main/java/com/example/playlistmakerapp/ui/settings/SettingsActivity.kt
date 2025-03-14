package com.example.playlistmakerapp.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmakerapp.R
import com.example.playlistmakerapp.databinding.ActivitySettingsBinding
import com.example.playlistmakerapp.ui.app.App

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onPause() {
        super.onPause()
        (applicationContext as App).switchTheme(binding.switcher.isChecked)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.switcher.isChecked = (applicationContext as App).darkTheme
        binding.switcher.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).switchTheme(checked)
        }


        binding.back.setOnClickListener {
            finish()
        }

        binding.buttonShare.setOnClickListener {
            val message = getString(R.string.shareText)
            val shareTitle = getString(R.string.shareTitle)
            val intent = Intent(Intent.ACTION_SEND)

            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, message)

            startActivity(Intent.createChooser(intent, shareTitle))
        }

        binding.buttonSupport.setOnClickListener {
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

        binding.buttonAgreement.setOnClickListener {
            val agreementText = getString(R.string.agreementText)
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(agreementText)))
        }
    }
}