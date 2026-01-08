package com.example.playlistmaker.domain.api.sharing

import com.example.playlistmaker.domain.model.sharing.EmailData

interface ExternalNavigator {
    fun shareApp()
    fun openEmail(emailData: EmailData)
    fun openTerms()
    fun getSupportEmailData() : EmailData
    fun sharePlaylist(shareMessage: String)
}