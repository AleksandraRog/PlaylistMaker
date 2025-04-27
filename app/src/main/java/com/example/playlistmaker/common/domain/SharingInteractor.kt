package com.example.playlistmaker.common.domain

import com.example.playlistmaker.common.presentation.model.SharingObjects
import kotlinx.coroutines.flow.Flow

interface SharingInteractor {

    fun getIntentProperty(sharingObjects: SharingObjects): Flow<SharingObjects>

    var getShareAppLink: () -> String
}
