package com.example.playlistmakerapp.search.domain

interface ResourcesProvider {
    fun getNothingFoundText(): String
    fun getSomethingWentWrongText(): String
}