package com.example.playlistmaker.data.impl.sharing

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.content.Intent.ACTION_SENDTO
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import androidx.core.content.ContextCompat.getString
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.sharing.ExternalNavigator
import com.example.playlistmaker.domain.models.sharing.EmailData

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {
    override fun shareApp() {
        val shareAppButtonIntent = Intent(ACTION_SEND)
        val shareAppLink = getString(context, R.string.share_app_link)
        shareAppButtonIntent.apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareAppLink)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(shareAppButtonIntent)
    }

    override fun openEmail(emailData: EmailData) {
        val supportButtonIntent = Intent(ACTION_SENDTO)
        val supportSubject = emailData.supportSubject
        val supportMessage = emailData.supportMessage
        val supportEmail = emailData.supportEmail
        supportButtonIntent.apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmail))
            putExtra(Intent.EXTRA_SUBJECT, supportSubject)
            putExtra(Intent.EXTRA_TEXT, supportMessage)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(supportButtonIntent)
    }

    override fun openTerms() {
        val userAgreementButtonIntent = Intent(ACTION_VIEW)
        val userAgreementLink = getString(context, R.string.user_agreement_link)
        userAgreementButtonIntent.apply {
            data = Uri.parse(userAgreementLink)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(userAgreementButtonIntent)
    }

    override fun getSupportEmailData(): EmailData {
        return EmailData(
            context.getString(R.string.support_subject),
            context.getString(R.string.support_message),
            context.getString(R.string.support_email)
        )
    }
}