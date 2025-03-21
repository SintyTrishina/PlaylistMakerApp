package com.example.playlistmakerapp.sharing.domain.models

data class EmailData(
    val extraSubject: String,
    val extraText: String,
    val email: String,
    val sendTitle: String
)
