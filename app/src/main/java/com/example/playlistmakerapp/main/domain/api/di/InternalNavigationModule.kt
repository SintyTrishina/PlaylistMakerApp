package com.example.playlistmakerapp.main.domain.api.di

import android.app.Activity
import com.example.playlistmakerapp.main.data.impl.InternalNavigationImpl
import com.example.playlistmakerapp.main.domain.api.InternalNavigation
import org.koin.dsl.module

val internalNavigationModule = module {

    factory<InternalNavigation> { (activity: Activity) ->
        InternalNavigationImpl(activity)
    }
}