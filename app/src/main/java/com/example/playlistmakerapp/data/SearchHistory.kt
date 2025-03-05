package com.example.playlistmakerapp.data

import android.content.SharedPreferences
import com.example.playlistmakerapp.domain.models.Track
import com.google.gson.Gson

const val maxSize = 10

class SearchHistory(private val sharedPrefs: SharedPreferences) {
    private val searchList = ArrayList<Track>()

    fun addTrack(track: Track) {
        if (searchList.contains(track)) {
            searchList.remove(track)
        }

        searchList.add(0, track)

        if (searchList.size > maxSize) {
            val removedTrack = searchList.removeAt(searchList.size - 1)
            sharedPrefs.edit()
                .remove(removedTrack.trackId.toString())
                .apply()
        }

        saveTrackToPrefs(track, System.currentTimeMillis())
    }

    private fun saveTrackToPrefs(track: Track, timestamp: Long) {
        val json = Gson().toJson(track)
        sharedPrefs.edit()
            .putString(track.trackId.toString(), "$timestamp|$json")
            .apply()
    }

    fun getHistory(): ArrayList<Track> {
        return searchList
    }

    fun cleanHistory() {
        sharedPrefs.edit()
            .clear()
            .apply()

        searchList.clear()
    }

    fun loadHistoryFromPrefs() {
        searchList.clear()

        searchList.addAll(
            sharedPrefs.all.values.mapNotNull { value ->
                try {
                    val (timestamp, json) = value.toString().split("|")
                    val track = Gson().fromJson(json, Track::class.java)
                    Pair(timestamp.toLong(), track)
                } catch (e: Exception) {
                    null
                }
            }.sortedByDescending { it.first }
                .map { it.second }
        )

        while (searchList.size > maxSize) {
            val removedTrack = searchList.removeAt(searchList.size - 1)
            sharedPrefs.edit()
                .remove(removedTrack.trackId.toString())
                .apply()
        }
    }
}