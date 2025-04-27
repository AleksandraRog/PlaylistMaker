package com.example.playlistmaker.sharing.domain.interactors

import com.example.playlistmaker.common.domain.SharingInteractor
import com.example.playlistmaker.sharing.domain.model.EmailData

interface CompleteSharingInteractor : SharingInteractor {

    var getSupportEmailData: () -> EmailData
    var getTermsLink: () -> String
}
