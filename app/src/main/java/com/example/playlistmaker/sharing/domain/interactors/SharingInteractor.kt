package com.example.playlistmaker.sharing.domain.interactors

import com.example.playlistmaker.sharing.domain.model.EmailData
import com.example.playlistmaker.sharing.presentation.SharingObjects
import kotlinx.coroutines.flow.Flow

interface SharingInteractor {

    fun getIntentProperty(sharingObjects: SharingObjects): Flow<SharingObjects>

    var getShareAppLink: () -> String
    var getSupportEmailData: () -> EmailData
    var getTermsLink: () -> String
}
