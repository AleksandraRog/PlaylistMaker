package com.example.playlistmaker.sharing.domain.interactors.impl

import com.example.playlistmaker.common.presentation.model.SharingObjects
import com.example.playlistmaker.sharing.domain.interactors.CompleteSharingInteractor
import com.example.playlistmaker.sharing.domain.model.EmailData
import com.example.playlistmaker.sharing.domain.repsitory.SharingExternalNavigator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SharingInteractorImpl(
    private val externalNavigator: SharingExternalNavigator,
) : CompleteSharingInteractor {

    override fun getIntentProperty(sharingObjects: SharingObjects): Flow<SharingObjects> {
        when (sharingObjects) {
            SharingObjects.TERMS -> {
                return externalNavigator.openLinkFlow(getTermsLink()).map { intents ->
                    SharingObjects.TERMS.apply { intent = intents }
                }
            }

            SharingObjects.SUPPORT -> {
                return externalNavigator.openEmailFlow(getSupportEmailData()).map { intents ->
                    SharingObjects.SUPPORT.apply { intent = intents }
                }
            }

            SharingObjects.SHARE_APP -> {
                return externalNavigator.shareLinkFlow(getShareAppLink()).map { intents ->
                    SharingObjects.SHARE_APP.apply { intent = intents }
                }
            }

            else -> {
                return externalNavigator.defaultFlow().map { _ ->
                    SharingObjects.DEFAULT
                }
            }
        }

    }

    override var getShareAppLink: () -> String = { "" }
    override var getSupportEmailData: () -> EmailData = { EmailData("", "", "") }
    override var getTermsLink: () -> String = { "" }

}