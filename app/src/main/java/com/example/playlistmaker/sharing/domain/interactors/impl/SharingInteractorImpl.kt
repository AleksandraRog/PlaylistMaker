package com.example.playlistmaker.sharing.domain.interactors.impl

import com.example.playlistmaker.common.domain.consumer.Consumer
import com.example.playlistmaker.sharing.domain.interactors.SharingInteractor
import com.example.playlistmaker.sharing.domain.model.EmailData
import com.example.playlistmaker.sharing.domain.repsitory.ExternalNavigator
import org.koin.java.KoinJavaComponent.getKoin
import java.util.concurrent.ExecutorService

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {

    private val executor: ExecutorService by lazy { getKoin().get<ExecutorService>() }

    override fun shareApp(consumer: Consumer<Any?>) {
        consumer.consume(externalNavigator.shareLink(getShareAppLink()))

    }

    override fun openTerms(consumer: Consumer<Any?>) {
        consumer.consume(externalNavigator.openLink(getTermsLink()))

    }

    override fun openSupport(consumer: Consumer<Any?>) {
        consumer.consume(externalNavigator.openEmail(getSupportEmailData()))

    }

    override var getShareAppLink: () -> String = { "" }
    override var getSupportEmailData: () -> EmailData = { EmailData("", "", "") }
    override var getTermsLink: () -> String = { "" }

}