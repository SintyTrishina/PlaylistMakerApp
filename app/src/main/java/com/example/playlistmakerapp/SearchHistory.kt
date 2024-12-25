package com.example.playlistmakerapp

import android.content.SharedPreferences
import com.google.gson.Gson

const val maxSize = 10

class SearchHistory(private val sharedPrefs: SharedPreferences) {
    private val searchList = ArrayList<Track>() // Локальное хранилище

    // Добавляем трек на самый верх
    fun addTrack(track: Track) {
        // Удаляем дубликаты из локального списка
        searchList.removeIf { it.trackId == track.trackId }

        // Добавляем новый трек в начало списка
        searchList.add(0, track)

        // Если список превышает максимальный размер, удаляем последний элемент
        if (searchList.size > maxSize) {
            val removedTrack = searchList.removeAt(searchList.size - 1)
            // Удаляем трек из SharedPreferences
            sharedPrefs.edit()
                .remove(removedTrack.trackId.toString())
                .apply()
        }

        // Сохраняем новый трек в SharedPreferences
        saveTrackToPrefs(track, System.currentTimeMillis())
    }

    // Сохраняем трек в SharedPreferences
    private fun saveTrackToPrefs(track: Track, timestamp: Long) {
        val json = Gson().toJson(track)
        sharedPrefs.edit()
            .putString(track.trackId.toString(), "$timestamp|$json")
            .apply()
    }

    // Получаем историю поиска
    fun getHistory(): ArrayList<Track> {
        return searchList
    }

    // Очищаем локальное хранилище и SharedPreferences
    fun cleanHistory() {
        sharedPrefs.edit()
            .clear()
            .apply()

        searchList.clear()
    }

    // Загружаем данные из SharedPreferences
    fun loadHistoryFromPrefs() {
        searchList.clear() // Очищаем текущий список

        searchList.addAll(
            sharedPrefs.all.values.mapNotNull { value ->
                try {
                    val (timestamp, json) = value.toString().split("|")
                    val track = Gson().fromJson(json, Track::class.java)
                    Pair(timestamp.toLong(), track)
                } catch (e: Exception) {
                    null
                }
            }.sortedByDescending { it.first } // Сортируем по метке времени
                .map { it.second } // Преобразуем в список треков
        )

        // Если список превышает maxSize, удаляем лишние треки
        while (searchList.size > maxSize) {
            val removedTrack = searchList.removeAt(searchList.size - 1)
            sharedPrefs.edit()
                .remove(removedTrack.trackId.toString())
                .apply()
        }
    }
}