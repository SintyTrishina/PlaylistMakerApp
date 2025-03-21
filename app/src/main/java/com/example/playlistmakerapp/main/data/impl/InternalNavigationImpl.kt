package com.example.playlistmakerapp.main.data.impl

import android.content.Context
import android.content.Intent
import com.example.playlistmakerapp.main.domain.api.InternalNavigation
import com.example.playlistmakerapp.media.ui.MediaActivity
import com.example.playlistmakerapp.search.ui.activity.SearchActivity
import com.example.playlistmakerapp.settings.ui.activity.SettingsActivity

class InternalNavigationImpl(private val context: Context): InternalNavigation {
    override fun startSearch() {
        val intent = Intent(context, SearchActivity::class.java)
        context.startActivity(intent)
    }

    override fun startMedia() {
        val intent = Intent(context, MediaActivity::class.java)
        context.startActivity(intent)
    }

    override fun startSettings() {
        val intent = Intent(context, SettingsActivity::class.java)
        context.startActivity(intent)
    }
}