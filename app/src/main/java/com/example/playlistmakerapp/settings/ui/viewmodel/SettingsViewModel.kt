package com.example.playlistmakerapp.settings.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmakerapp.settings.domain.api.SettingsRepository
import com.example.playlistmakerapp.sharing.domain.api.SharingInteractor
import com.example.playlistmakerapp.util.Creator

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {


    private val _darkThemeEnabled = MutableLiveData<Boolean>()
    val darkThemeEnabled: LiveData<Boolean> get() = _darkThemeEnabled

    init {
        // Загружаем текущее состояние темы при инициализации ViewModel
        _darkThemeEnabled.value = settingsRepository.isDarkThemeEnabled()
    }

    companion object {

        fun getViewModelFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {

                SettingsViewModel(
                    Creator.provideSettingsRepository(),
                    Creator.provideSharingInteractor(context)
                )
            }
        }
    }

    fun switchTheme(isDarkTheme: Boolean) {
        settingsRepository.switchTheme(isDarkTheme)
        _darkThemeEnabled.value = isDarkTheme
    }

    fun isDarkThemeEnabled(): Boolean {
        return settingsRepository.isDarkThemeEnabled()
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun openTerms() {
        sharingInteractor.openTerms()
    }

    fun openSupport() {
        sharingInteractor.openSupport()
    }


}