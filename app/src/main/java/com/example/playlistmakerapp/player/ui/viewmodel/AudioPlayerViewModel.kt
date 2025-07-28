package com.example.playlistmakerapp.player.ui.viewmodel

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmakerapp.media.domain.db.PlaylistInteractor
import com.example.playlistmakerapp.search.domain.db.FavouritesInteractor
import com.example.playlistmakerapp.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(
    private val mediaPlayer: MediaPlayer,
    private val favouritesInteractor: FavouritesInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private var timerJob: Job? = null

    private val _playerState = MutableLiveData<AudioPlayerState>()
    val playerState: LiveData<AudioPlayerState> get() = _playerState

    private val _isFavourite = MutableLiveData<Boolean>()
    val isFavourite: LiveData<Boolean> get() = _isFavourite

    private var previewUrl: String? = null

    init {
        _playerState.value = AudioPlayerState.Default()
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        timerJob?.cancel()
    }

    fun checkIsFavourite(trackId: Int) {
        viewModelScope.launch {
            _isFavourite.value = favouritesInteractor.isFavourite(trackId)
        }
    }

    fun setDataSource(previewUrl: String) {
        this.previewUrl = previewUrl
        preparePlayer()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            _playerState.postValue(AudioPlayerState.Prepared())
        }
        mediaPlayer.setOnCompletionListener {
            timerJob?.cancel()
            _playerState.postValue(AudioPlayerState.Prepared())
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(UPDATE_TIME)
                _playerState.postValue(AudioPlayerState.Playing(true, getCurrentPlayerPosition()))
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
        _playerState.postValue(AudioPlayerState.Paused(true, getCurrentPlayerPosition()))
    }

    fun playbackControl() {
        when (val currentState = _playerState.value) {
            is AudioPlayerState.Playing -> pausePlayer()
            is AudioPlayerState.Prepared, is AudioPlayerState.Paused -> startPlayer()
            else -> Unit
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
    }

    fun onFavouriteClicked(track: Track) {
        viewModelScope.launch {
            val isFavourite = favouritesInteractor.isFavourite(track.trackId)
            if (isFavourite) {
                favouritesInteractor.deleteFavouriteTrack(track.trackId)
                _isFavourite.postValue(false)
            } else {
                favouritesInteractor.addFavouriteTrack(track)
                _isFavourite.postValue(true)
            }
        }
    }

    companion object {
        const val UPDATE_TIME = 300L
    }
}