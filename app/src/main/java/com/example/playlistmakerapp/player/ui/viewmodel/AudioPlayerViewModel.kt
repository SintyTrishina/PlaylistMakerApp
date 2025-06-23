package com.example.playlistmakerapp.player.ui.viewmodel

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel : ViewModel() {

    private var mediaPlayer = MediaPlayer()

    private var timerJob: Job? = null

    private val _playerState = MutableLiveData<AudioPlayerState>()
    val playerState: LiveData<AudioPlayerState> get() = _playerState

    private var previewUrl: String? = null

    init {
        _playerState.value = AudioPlayerState.Default()
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        _playerState.value = AudioPlayerState.Default()
    }

    fun setDataSource(previewUrl: String) {
        this.previewUrl = previewUrl
        preparePlayer()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            updateState(AudioPlayerState.Prepared())
        }
        mediaPlayer.setOnCompletionListener {
            timerJob?.cancel()
            updateState(AudioPlayerState.Prepared())
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(UPDATE_TIME)
                updateState(AudioPlayerState.Playing(true, getCurrentPlayerPosition()))
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        startTimer()
    }

    fun pausePlayer() {
        mediaPlayer.pause()
        timerJob?.cancel()
        updateState(AudioPlayerState.Paused(true, getCurrentPlayerPosition()))
    }

    fun playbackControl() {
        when (_playerState.value) {
            is AudioPlayerState.Playing -> pausePlayer()
            is AudioPlayerState.Prepared, is AudioPlayerState.Paused -> startPlayer()
            else -> Unit
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
            ?: "00:00"
    }

    private fun updateState(newState: AudioPlayerState) {
        _playerState.postValue(newState)
    }

    companion object {
        const val UPDATE_TIME = 300L
    }
}