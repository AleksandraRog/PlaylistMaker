package com.example.playlistmaker.sharing.data.repositoryImpl

import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.sharing.domain.model.EmailData
import com.example.playlistmaker.sharing.domain.repsitory.ExternalNavigator

class ExternalNavigatorImpl() : ExternalNavigator {

    override fun shareLink(link: String): ConsumerData<Any?> {

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, link)
        }
        return ConsumerData(shareIntent, 0)
    }

    override fun openLink(link: String): ConsumerData<Any?> {

        val arrowIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        return ConsumerData(arrowIntent , 0)
    }

    override fun openEmail(emailData: EmailData): ConsumerData<Any?> {

        val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailData.email))
            putExtra(Intent.EXTRA_SUBJECT, emailData.themeMessage)
            putExtra(Intent.EXTRA_TEXT, emailData.textMessage)
        }

        return ConsumerData(supportIntent, 0)
    }
}