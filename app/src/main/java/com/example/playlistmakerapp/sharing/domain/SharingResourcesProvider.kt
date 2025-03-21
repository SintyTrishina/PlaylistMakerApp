package com.example.playlistmakerapp.sharing.domain

import com.example.playlistmakerapp.sharing.domain.models.EmailData

interface SharingResourcesProvider {

    fun getShareAppLink(): String
    fun getTermsLink(): String
    fun getSupportEmailData(): EmailData

}