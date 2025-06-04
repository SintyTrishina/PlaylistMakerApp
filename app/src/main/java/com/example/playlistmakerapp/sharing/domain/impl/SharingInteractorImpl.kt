package com.example.playlistmakerapp.sharing.domain.impl

import android.content.Context
import com.example.playlistmakerapp.sharing.domain.SharingResourcesProvider
import com.example.playlistmakerapp.sharing.domain.api.ExternalNavigator
import com.example.playlistmakerapp.sharing.domain.api.SharingInteractor

class SharingInteractorImpl(
    private val externalNavigator: (Context) -> ExternalNavigator,
    private val sharingResourcesProvider: SharingResourcesProvider
) : SharingInteractor {
    override fun shareApp(context: Context) {
        externalNavigator(context).shareLink(sharingResourcesProvider.getShareAppLink())
    }

    override fun openTerms(context: Context) {
        externalNavigator(context).openLink(sharingResourcesProvider.getTermsLink())
    }

    override fun openSupport(context: Context) {
        externalNavigator(context).openEmail(sharingResourcesProvider.getSupportEmailData())
    }

}