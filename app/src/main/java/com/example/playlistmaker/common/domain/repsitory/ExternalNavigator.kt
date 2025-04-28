package com.example.playlistmaker.common.domain.repsitory

import com.example.playlistmaker.sharing.domain.model.EmailData
import kotlinx.coroutines.flow.Flow

interface ExternalNavigator {

    fun openLinkFlow(termsLink: String) : Flow<Any?>

    fun shareLinkFlow(shareAppLink: String) : Flow<Any?>

    fun openEmailFlow(supportEmailData: EmailData) : Flow<Any?>

    fun defaultFlow() : Flow<Any?>
}