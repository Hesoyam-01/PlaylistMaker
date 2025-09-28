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
import com.example.playlistmaker.presentation.settings.SettingsState
import com.example.playlistmaker.presentation.settings.SettingsViewModel

class SettingsActivity : AppCompatActivity() {
    private var viewModel: SettingsViewModel? = null
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
        viewModel = ViewModelProvider(
            this,
            SettingsViewModel.getFactory()
        )[SettingsViewModel::class.java]

        binding.settingsToolbar.setNavigationOnClickListener {
            finish()
        }

        viewModel?.observeSettingsState()?.observe(this) {
            render(it)
        }

        viewModel?.getThemeMode()

        binding.themeSwitcher.setOnCheckedChangeListener { switcher, checked -> viewModel?.switchTheme(checked) }

        binding.shareAppButton.setOnClickListener {
            viewModel?.shareApp()
        }

        binding.supportButton.setOnClickListener {
            viewModel?.openSupport()
        }

        binding.userAgreementButton.setOnClickListener {
            viewModel?.openTerms()
        }
    }

    private fun render(state: SettingsState) {
        when (state) {
            is SettingsState.LightTheme -> setLightTheme()
            is SettingsState.DarkTheme -> setDarkTheme()
        }
    }

    private fun setLightTheme() {
        AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_NO
        )
        binding.themeSwitcher.isChecked = false
    }

    private fun setDarkTheme() {
        AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_YES
        )
        binding.themeSwitcher.isChecked = true
    }
}