package com.example.playlistmaker.sharing.domain.repsitory

import com.example.playlistmaker.sharing.domain.model.EmailData
import kotlinx.coroutines.flow.Flow

interface SharingExternalNavigator {

    fun openLinkFlow(termsLink: String) : Flow<Any?>

    fun shareLinkFlow(shareAppLink: String) : Flow<Any?>

    fun openEmailFlow(supportEmailData: EmailData) : Flow<Any?>

    fun defaultFlow() : Flow<Any?>
}