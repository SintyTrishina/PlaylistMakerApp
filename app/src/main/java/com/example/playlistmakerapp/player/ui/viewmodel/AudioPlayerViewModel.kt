package com.example.playlistmakerapp.player.ui.viewmodel

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel : ViewModel() {

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        const val UPDATE_TIME = 300L
    }

    private var mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())

    private val _playerState = MutableLiveData<Int>()
    val playerState: LiveData<Int> get() = _playerState

    private val _currentPosition = MutableLiveData<String>()
    val currentPosition: LiveData<String> get() = _currentPosition

    private val _isPlayButtonEnabled = MutableLiveData<Boolean>()
    val isPlayButtonEnabled: LiveData<Boolean> get() = _isPlayButtonEnabled

    private var previewUrl: String? = null

    init {
        _playerState.value = STATE_DEFAULT
        _isPlayButtonEnabled.value = false
    }

    fun setDataSource(previewUrl: String) {
        this.previewUrl = previewUrl
        preparePlayer()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            _isPlayButtonEnabled.value = true
            _playerState.value = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            _playerState.value = STATE_PREPARED
            handler.removeCallbacks(updatingTime)
        }
    }

    private val updatingTime = object : Runnable {
        override fun run() {
            if (_playerState.value == STATE_PLAYING) {
                _currentPosition.value = SimpleDateFormat("mm:ss", Locale.getDefault())
                    .format(mediaPlayer.currentPosition)
                handler.postDelayed(this, UPDATE_TIME)
            }
        }
    }

    fun startPlayer() {
        mediaPlayer.start()
        _playerState.value = STATE_PLAYING
        handler.post(updatingTime)
    }

    fun pausePlayer() {
        mediaPlayer.pause()
        _playerState.value = STATE_PAUSED
        handler.removeCallbacks(updatingTime)
    }

    fun playbackControl() {
        when (_playerState.value) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        handler.removeCallbacks(updatingTime)
    }


}