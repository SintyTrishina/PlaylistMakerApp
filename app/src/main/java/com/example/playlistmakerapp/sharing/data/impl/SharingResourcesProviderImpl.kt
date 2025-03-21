package com.example.playlistmakerapp.sharing.data.impl

import android.content.Context
import com.example.playlistmakerapp.R
import com.example.playlistmakerapp.sharing.domain.SharingResourcesProvider
import com.example.playlistmakerapp.sharing.domain.models.EmailData

class SharingResourcesProviderImpl(private val context: Context) : SharingResourcesProvider {
    override fun getShareAppLink(): String {
        return context.getString(R.string.shareText)
    }

    override fun getTermsLink(): String {
        return context.getString(R.string.agreementText)
    }

    override fun getSupportEmailData(): EmailData {
        return EmailData(
            extraSubject = context.getString(R.string.extraSubject),
            extraText = context.getString(R.string.extraText),
            email = context.getString(R.string.email),
            sendTitle = context.getString(R.string.sendTitle),
        )
    }
}