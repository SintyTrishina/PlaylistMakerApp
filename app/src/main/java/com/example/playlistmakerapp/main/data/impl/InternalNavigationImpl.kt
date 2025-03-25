package com.example.playlistmakerapp.main.data.impl

import android.app.Activity
import android.content.Intent
import com.example.playlistmakerapp.main.domain.api.InternalNavigation
import com.example.playlistmakerapp.media.ui.MediaActivity
import com.example.playlistmakerapp.search.ui.activity.SearchActivity
import com.example.playlistmakerapp.settings.ui.activity.SettingsActivity

class InternalNavigationImpl(private val activity: Activity): InternalNavigation {
    override fun startSearch() {
        val intent = Intent(activity, SearchActivity::class.java)
        activity.startActivity(intent)
    }

    override fun startMedia() {
        val intent = Intent(activity, MediaActivity::class.java)
        activity.startActivity(intent)
    }

    override fun startSettings() {
        val intent = Intent(activity, SettingsActivity::class.java)
        activity.startActivity(intent)
    }
}