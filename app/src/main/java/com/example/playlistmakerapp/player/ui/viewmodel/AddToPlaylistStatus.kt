package com.example.playlistmakerapp.player.ui.viewmodel

sealed class AddToPlaylistStatus {
    data class Success(val playlistName: String) : AddToPlaylistStatus()
    data class AlreadyExists(val playlistName: String) : AddToPlaylistStatus()
}