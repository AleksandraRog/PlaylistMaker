package com.example.playlistmaker.domain.interactors.impl

import com.example.playlistmaker.domain.consumer.ConsumerData
import com.example.playlistmaker.domain.consumer.ListenerConsumer
import com.example.playlistmaker.domain.interactors.DarkThemeInteractor
import com.example.playlistmaker.domain.repository.DarkThemeRepository
import org.koin.java.KoinJavaComponent.getKoin
import java.util.concurrent.ExecutorService

class DarkThemeInteractorImpl (private val darkThemeRepository : DarkThemeRepository) : DarkThemeInteractor {

    private val executor: ExecutorService by lazy { getKoin().get<ExecutorService>() }

    override fun getDarkTheme(consumer: DarkThemeInteractor.DarkThemeConsumer) {
        executor.execute {
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
