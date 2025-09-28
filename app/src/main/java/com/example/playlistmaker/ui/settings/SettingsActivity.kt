package com.example.playlistmaker.ui.settings

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.domain.api.settings.ThemeInteractor
import com.example.playlistmaker.domain.api.sharing.SharingInteractor
import com.example.playlistmaker.presentation.search.SearchViewModel
import com.example.playlistmaker.presentation.settings.SettingsViewModel
import com.example.playlistmaker.util.Creator

class SettingsActivity : AppCompatActivity() {
    private lateinit var themeInteractor: ThemeInteractor
    private lateinit var sharingInteractor: SharingInteractor

    private val viewModel: SettingsViewModel? = null

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        themeInteractor = Creator.provideThemeInteractor(this)
        sharingInteractor = Creator.provideSharingInteractor(this)

        viewModel = ViewModelProvider(
            this,
            SearchViewModel.getFactory()
        )[SearchViewModel::class.java]

        binding.settingsToolbar.setNavigationOnClickListener {
            finish()
        }

        themeInteractor.getThemeMode(object : ThemeInteractor.ThemeConsumer {
            override fun consume(themeMode: Int) {
                val isDarkThemeEnabled = themeMode == AppCompatDelegate.MODE_NIGHT_YES
                binding.themeSwitcher.isChecked = isDarkThemeEnabled
            }
        })
        binding.themeSwitcher.setOnCheckedChangeListener { switcher, checked -> switchTheme(checked) }

        binding.shareAppButton.setOnClickListener {
            sharingInteractor.shareApp()
        }

        binding.supportButton.setOnClickListener {
            sharingInteractor.openSupport()
        }

        binding.userAgreementButton.setOnClickListener {
            sharingInteractor.openTerms()
        }
    }

    private fun switchTheme(darkModeEnabled: Boolean) {
        if (darkModeEnabled) AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_YES
        ) else AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_NO
        )
        themeInteractor.saveTheme(darkModeEnabled)
    }
}