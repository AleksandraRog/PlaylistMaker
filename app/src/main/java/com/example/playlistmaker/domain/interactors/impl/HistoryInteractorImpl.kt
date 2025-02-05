package com.example.playlistmaker.domain.interactors.impl

import com.example.playlistmaker.domain.interactors.HistoryInteractor
import com.example.playlistmaker.domain.repository.HistoryRepository
import org.koin.java.KoinJavaComponent.getKoin
import java.util.concurrent.ExecutorService

class HistoryInteractorImpl(private val repository: HistoryRepository) : HistoryInteractor {

    private val executor: ExecutorService by lazy { getKoin().get<ExecutorService>() }

     override fun loadTracks(consumer: HistoryInteractor.HistoryTracksConsumer) {
         executor.execute {
            consumer.consume(repository.loadHistory())
        }
    }
}
