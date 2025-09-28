package com.example.playlistmaker.ui.settings

import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.content.Intent.ACTION_SENDTO
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.domain.api.ThemeInteractor
import com.example.playlistmaker.util.Creator

class SettingsActivity : AppCompatActivity() {
    private lateinit var themeInteractor: ThemeInteractor

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
            val shareAppButtonIntent = Intent(ACTION_SEND)
            val shareAppLink = getString(R.string.share_app_link)
            shareAppButtonIntent.type = "text/plain"
            shareAppButtonIntent.putExtra(Intent.EXTRA_TEXT, shareAppLink)
            startActivity(shareAppButtonIntent)
        }

        binding.supportButton.setOnClickListener {
            val supportButtonIntent = Intent(ACTION_SENDTO)
            val supportSubject = getString(R.string.support_subject)
            val supportMessage = getString(R.string.support_message)
            val supportEmail = getString(R.string.support_email)
            supportButtonIntent.data = Uri.parse("mailto:")
            supportButtonIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmail))
            supportButtonIntent.putExtra(Intent.EXTRA_SUBJECT, supportSubject)
            supportButtonIntent.putExtra(Intent.EXTRA_TEXT, supportMessage)
            startActivity(supportButtonIntent)
        }

        binding.userAgreementButton.setOnClickListener {
            val userAgreementButtonIntent = Intent(ACTION_VIEW)
            val userAgreementLink = getString(R.string.user_agreement_link)
            userAgreementButtonIntent.data = Uri.parse(userAgreementLink)
            startActivity(userAgreementButtonIntent)
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