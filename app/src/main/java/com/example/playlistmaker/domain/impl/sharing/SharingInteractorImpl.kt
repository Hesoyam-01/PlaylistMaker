package com.example.playlistmaker.domain.impl.sharing

import com.example.playlistmaker.domain.api.sharing.ExternalNavigator
import com.example.playlistmaker.domain.api.sharing.SharingInteractor
import com.example.playlistmaker.domain.models.sharing.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareApp()
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    override fun openTerms() {
        externalNavigator.openTerms()
    }

    private fun getSupportEmailData() : EmailData {
        return externalNavigator.getSupportEmailData()
    }
}