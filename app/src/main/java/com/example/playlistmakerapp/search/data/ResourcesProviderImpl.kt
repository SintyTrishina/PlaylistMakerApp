package com.example.playlistmakerapp.search.data

import android.content.Context
import com.example.playlistmakerapp.R
import com.example.playlistmakerapp.search.domain.ResourcesProvider

class ResourcesProviderImpl(private val context: Context) : ResourcesProvider {
    override fun getNothingFoundText(): String {
        return context.getString(R.string.nothing_found)
    }

    override fun getSomethingWentWrongText(): String {
        return context.getString(R.string.something_went_wrong)
    }
}
