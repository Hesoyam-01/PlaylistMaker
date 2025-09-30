package com.example.playlistmaker.data.impl.main

import android.content.Context
import android.content.Intent
import com.example.playlistmaker.domain.api.main.MainNavigator
import com.example.playlistmaker.ui.library.LibraryActivity
import com.example.playlistmaker.ui.search.SearchActivity
import com.example.playlistmaker.ui.settings.SettingsActivity

class MainNavigatorImpl(private val context: Context) : MainNavigator {
    override fun openSearch() {
        val searchButtonIntent = Intent(context, SearchActivity::class.java)
        searchButtonIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(searchButtonIntent)
    }

    override fun openLibrary() {
        val libraryButtonIntent = Intent(context, LibraryActivity::class.java)
        libraryButtonIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(libraryButtonIntent)
    }

    override fun openSettings() {
        val settingsButtonIntent = Intent(context, SettingsActivity::class.java)
        settingsButtonIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(settingsButtonIntent)
    }
}