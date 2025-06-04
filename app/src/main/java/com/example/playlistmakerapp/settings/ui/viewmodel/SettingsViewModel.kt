package com.example.playlistmakerapp.settings.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmakerapp.settings.domain.api.SettingsInteractor
import com.example.playlistmakerapp.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {


    private val _darkThemeEnabled = MutableLiveData<Boolean>()
    val darkThemeEnabled: LiveData<Boolean> get() = _darkThemeEnabled

    init {
        // Загружаем текущее состояние темы при инициализации ViewModel
        _darkThemeEnabled.value = settingsInteractor.getTheme()
    }

    fun switchTheme(isDarkTheme: Boolean) {
        settingsInteractor.switchTheme(isDarkTheme)
        _darkThemeEnabled.value = isDarkTheme
    }

    fun isDarkThemeEnabled(): Boolean {
        return settingsInteractor.getTheme()
    }

    fun shareApp(context: Context) {
        sharingInteractor.shareApp(context)
    }

    fun openTerms(context: Context) {
        sharingInteractor.openTerms(context)
    }

    fun openSupport(context: Context) {
        sharingInteractor.openSupport(context)
    }

}