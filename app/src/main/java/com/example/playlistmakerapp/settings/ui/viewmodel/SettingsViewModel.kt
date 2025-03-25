package com.example.playlistmakerapp.settings.ui.viewmodel

import android.app.Activity
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
        _darkThemeEnabled.value = settingsInteractor.isDarkThemeEnabled()
    }

    fun switchTheme(isDarkTheme: Boolean) {
        settingsInteractor.switchTheme(isDarkTheme)
        _darkThemeEnabled.value = isDarkTheme
    }

    fun isDarkThemeEnabled(): Boolean {
        return settingsInteractor.isDarkThemeEnabled()
    }

    fun shareApp(activity: Activity) {
        sharingInteractor.shareApp(activity)
    }

    fun openTerms(activity: Activity) {
        sharingInteractor.openTerms(activity)
    }

    fun openSupport(activity: Activity) {
        sharingInteractor.openSupport(activity)
    }

}