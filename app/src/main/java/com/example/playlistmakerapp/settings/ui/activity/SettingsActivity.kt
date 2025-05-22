package com.example.playlistmakerapp.settings.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmakerapp.databinding.ActivitySettingsBinding
import com.example.playlistmakerapp.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private val settingsViewModel by viewModel<SettingsViewModel>() { parametersOf(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Инициализация свитчера текущим значением
        binding.switcher.isChecked = settingsViewModel.isDarkThemeEnabled()

        settingsViewModel.darkThemeEnabled.observe(this) { isDark ->
            if (binding.switcher.isChecked != isDark) {
                binding.switcher.isChecked = isDark
            }
        }

        binding.switcher.setOnCheckedChangeListener { _, checked ->
            settingsViewModel.switchTheme(checked)
        }

        binding.back.setOnClickListener {
            finish()
        }

        binding.buttonShare.setOnClickListener {
            settingsViewModel.shareApp(this)
        }

        binding.buttonSupport.setOnClickListener {
            settingsViewModel.openSupport(this)
        }

        binding.buttonAgreement.setOnClickListener {
            settingsViewModel.openTerms(this)
        }
    }
}