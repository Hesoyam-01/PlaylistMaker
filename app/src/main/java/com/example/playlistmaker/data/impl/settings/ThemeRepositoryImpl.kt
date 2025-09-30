package com.example.playlistmaker.data.impl.settings

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.data.client.StorageClient
import com.example.playlistmaker.domain.api.settings.ThemeRepository

class ThemeRepositoryImpl(
    private val storage: StorageClient<Boolean>,
    private val context: Context
) : ThemeRepository {

    override fun getThemeMode(): Int {
        val darkThemeEnabled = storage.getData() ?: isSystemInDarkTheme()
        return if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
        else AppCompatDelegate.MODE_NIGHT_NO
    }

    override fun putThemeIntoSharedPrefs(darkThemeEnabled: Boolean) {
        storage.storeData(darkThemeEnabled)
    }

    override fun isSystemInDarkTheme(): Boolean {
        return when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }
}