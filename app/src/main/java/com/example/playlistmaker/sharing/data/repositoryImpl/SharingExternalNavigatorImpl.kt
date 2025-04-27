package com.example.playlistmaker.sharing.data.repositoryImpl

import com.example.playlistmaker.common.domain.repsitory.ExternalNavigator
import com.example.playlistmaker.sharing.domain.model.EmailData
import com.example.playlistmaker.sharing.domain.repsitory.SharingExternalNavigator
import kotlinx.coroutines.flow.Flow

class SharingExternalNavigatorImpl(private val externalNavigator: ExternalNavigator) : SharingExternalNavigator {

    override fun openLinkFlow(termsLink: String): Flow<Any?> {
        return  externalNavigator.openLinkFlow(termsLink)
    }

    override fun shareLinkFlow(shareAppLink: String): Flow<Any?> {
        return externalNavigator.shareLinkFlow(shareAppLink)
    }

    override fun openEmailFlow(supportEmailData: EmailData): Flow<Any?> {
        return externalNavigator.openEmailFlow(supportEmailData)
    }

    override fun defaultFlow(): Flow<Any?> {
        return externalNavigator.defaultFlow()
    }
}
