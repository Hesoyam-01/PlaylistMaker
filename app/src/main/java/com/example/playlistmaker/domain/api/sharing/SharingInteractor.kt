package com.example.playlistmaker.domain.api.sharing

interface SharingInteractor {
    fun shareApp()
    fun openSupport()
    fun openTerms()
    fun sharePlaylist(shareMessage: String)
}