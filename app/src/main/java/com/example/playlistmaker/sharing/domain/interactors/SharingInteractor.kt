package com.example.playlistmaker.sharing.domain.interactors

import com.example.playlistmaker.common.domain.consumer.Consumer
import com.example.playlistmaker.sharing.domain.model.EmailData

interface SharingInteractor {

    fun shareApp(consumer: Consumer<Any?>)
    fun openTerms(consumer: Consumer<Any?>)
    fun openSupport(consumer: Consumer<Any?>)

    var getShareAppLink: () -> String
    var getSupportEmailData: () -> EmailData
    var getTermsLink: () -> String
}
