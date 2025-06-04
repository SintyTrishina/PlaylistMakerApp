package com.example.playlistmakerapp.sharing.domain.api

import android.content.Context

interface SharingInteractor {
    fun shareApp(context: Context)
    fun openTerms(context: Context)
    fun openSupport(context: Context)
}