package com.example.playlistmakerapp.sharing.domain.impl

import com.example.playlistmakerapp.sharing.domain.SharingResourcesProvider
import com.example.playlistmakerapp.sharing.domain.api.ExternalNavigator
import com.example.playlistmakerapp.sharing.domain.api.SharingInteractor

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val sharingResourcesProvider: SharingResourcesProvider
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(sharingResourcesProvider.getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(sharingResourcesProvider.getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(sharingResourcesProvider.getSupportEmailData())
    }

}