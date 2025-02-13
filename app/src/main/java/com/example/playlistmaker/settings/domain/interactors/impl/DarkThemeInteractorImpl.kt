package com.example.playlistmaker.settings.domain.interactors.impl

import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.common.domain.consumer.ListenerConsumer
import com.example.playlistmaker.settings.domain.interactors.DarkThemeInteractor
import com.example.playlistmaker.settings.domain.repository.DarkThemeRepository
import com.example.playlistmaker.settings.presentation.ThreadFlag
import org.koin.java.KoinJavaComponent.getKoin
import java.util.concurrent.ExecutorService

class DarkThemeInteractorImpl (private val darkThemeRepository : DarkThemeRepository) :
    DarkThemeInteractor {

    private val executor: ExecutorService by lazy { getKoin().get<ExecutorService>() }

    override fun getDarkTheme(
        threadFlag: ThreadFlag,
        consumer: DarkThemeInteractor.DarkThemeConsumer
    ) {
        if(threadFlag == ThreadFlag.NOT_MAIN_THREAD) {
            executor.execute {
                consumer.consume(darkThemeRepository.getDarkTheme())
            }
        } else {
            consumer.consume(darkThemeRepository.getDarkTheme())
        }
    }

    override fun saveDarkTheme(darkTheme: Boolean) {
        executor.execute {darkThemeRepository.saveDarkTheme(darkTheme) }
    }

    override fun observeThemeChanges(consumer: DarkThemeInteractor.DarkThemeConsumer) {
        executor.execute {
            darkThemeRepository.observeThemeChanges(lisenerConsumer = object :
                ListenerConsumer<Boolean> {
                override fun consume(data: ConsumerData<Boolean>) {
                    consumer.consume(data)
                }
            })
        }
    }
}

