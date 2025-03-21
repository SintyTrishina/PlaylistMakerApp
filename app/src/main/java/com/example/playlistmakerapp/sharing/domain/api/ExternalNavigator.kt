package com.example.playlistmakerapp.sharing.domain.api

import com.example.playlistmakerapp.sharing.domain.models.EmailData

interface ExternalNavigator {
    fun shareLink(link: String)
    fun openLink(link: String)
    fun openEmail(emailData: EmailData)

}