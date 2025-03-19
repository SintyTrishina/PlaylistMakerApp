package com.example.playlistmakerapp.sharing.domain.impl

import android.content.Context
import com.example.playlistmakerapp.R
import com.example.playlistmakerapp.sharing.domain.api.ExternalNavigator
import com.example.playlistmakerapp.sharing.domain.api.SharingInteractor
import com.example.playlistmakerapp.sharing.domain.models.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val context: Context
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        return context.getString(R.string.shareText)
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            extraSubject = context.getString(R.string.extraSubject),
            extraText = context.getString(R.string.extraText),
            email = context.getString(R.string.email),
            sendTitle = context.getString(R.string.sendTitle),
        )
    }

    private fun getTermsLink(): String {
        return context.getString(R.string.agreementText)
    }
}