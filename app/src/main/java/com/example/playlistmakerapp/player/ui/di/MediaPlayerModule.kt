package com.example.playlistmakerapp.player.ui.di

import android.media.MediaPlayer
import org.koin.dsl.module

val mediaPlayerModule = module {
    factory<MediaPlayer> { MediaPlayer() }
}