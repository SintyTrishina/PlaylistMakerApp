package com.example.playlistmakerapp.settings.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmakerapp.databinding.ActivitySettingsBinding
import com.example.playlistmakerapp.settings.ui.viewmodel.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private lateinit var settingsViewModel: SettingsViewModel

//    override fun onPause() {
//        super.onPause()
//        (applicationContext as App).switchTheme(binding.switcher.isChecked)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        settingsViewModel = ViewModelProvider(
            this,
            SettingsViewModel.getViewModelFactory(this)
        )[SettingsViewModel::class.java]

        settingsViewModel.darkThemeEnabled.observe(this) {
            binding.switcher.isChecked = settingsViewModel.isDarkThemeEnabled()
        }

        binding.switcher.setOnCheckedChangeListener { _, checked ->
            settingsViewModel.switchTheme(checked)
        }

        binding.back.setOnClickListener {
            finish()
        }

        binding.buttonShare.setOnClickListener {
            settingsViewModel.shareApp()
        }

        binding.buttonSupport.setOnClickListener {
            settingsViewModel.openSupport()
        }

        binding.buttonAgreement.setOnClickListener {
            settingsViewModel.openTerms()
        }
    }
}