package com.example.playlistmakerapp.sharing.domain.di

import android.app.Activity
import com.example.playlistmakerapp.sharing.data.impl.ExternalNavigatorImpl
import com.example.playlistmakerapp.sharing.domain.api.ExternalNavigator
import org.koin.dsl.module

val externalNavigatorModule = module {
    factory<ExternalNavigator> { (activity: Activity) ->
        ExternalNavigatorImpl(activity)
    }
}