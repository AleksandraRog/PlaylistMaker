package com.example.playlistmaker.sharing.data.repositoryImpl

import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.sharing.domain.model.EmailData
import com.example.playlistmaker.sharing.domain.repsitory.ExternalNavigator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ExternalNavigatorImpl() : ExternalNavigator {

    override fun openLinkFlow(termsLink: String): Flow<Any?> = flow {

        emit(Intent(Intent.ACTION_VIEW, Uri.parse(termsLink)))
    }

    override fun shareLinkFlow(shareAppLink: String): Flow<Any?> = flow {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareAppLink)
        }
        emit(shareIntent)
    }

    override fun openEmailFlow(supportEmailData: EmailData): Flow<Any?> = flow {
        val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmailData.email))
            putExtra(Intent.EXTRA_SUBJECT, supportEmailData.themeMessage)
            putExtra(Intent.EXTRA_TEXT, supportEmailData.textMessage)
        }
        emit(supportIntent)
    }

    override fun defaultFlow(): Flow<Any?> = flow{
        emit(null)
    }
}