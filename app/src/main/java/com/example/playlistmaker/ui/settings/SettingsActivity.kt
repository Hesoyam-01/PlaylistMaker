package com.example.playlistmaker.ui.settings

import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.content.Intent.ACTION_SENDTO
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val settingsToolbar = findViewById<MaterialToolbar>(R.id.settings_toolbar)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.theme_switcher)
        val shareAppButton = findViewById<MaterialTextView>(R.id.share_app_button)
        val supportButton = findViewById<MaterialTextView>(R.id.support_button)
        val userAgreementButton = findViewById<MaterialTextView>(R.id.user_agreement_button)

        settingsToolbar.setNavigationOnClickListener {
            finish()
        }

        themeSwitcher.isChecked = (application as App).darkTheme
        themeSwitcher.setOnCheckedChangeListener { switcher, checked -> (applicationContext as App).switchTheme(checked) }

        shareAppButton.setOnClickListener {
            val shareAppButtonIntent = Intent(ACTION_SEND)
            val shareAppLink = getString(R.string.share_app_link)
            shareAppButtonIntent.type = "text/plain"
            shareAppButtonIntent.putExtra(Intent.EXTRA_TEXT, shareAppLink)
            startActivity(shareAppButtonIntent)
        }

        supportButton.setOnClickListener {
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

        userAgreementButton.setOnClickListener {
            val userAgreementButtonIntent = Intent(ACTION_VIEW)
            val userAgreementLink = getString(R.string.user_agreement_link)
            userAgreementButtonIntent.data = Uri.parse(userAgreementLink)
            startActivity(userAgreementButtonIntent)
        }
    }
}