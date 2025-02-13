package com.example.playlistmaker.search.domain.interactors.impl

import com.example.playlistmaker.search.domain.interactors.TracksInteractor
import com.example.playlistmaker.search.domain.reposirory.ApiRepository
import org.koin.java.KoinJavaComponent.getKoin
import java.util.concurrent.ExecutorService

class TracksInteractorImpl(private val repository: ApiRepository) : TracksInteractor {

    private val executor: ExecutorService by lazy { getKoin().get<ExecutorService>() }

    override fun searchTracks(expression: String, consumer: TracksInteractor.FindTracksConsumer) {
        executor.execute {
            consumer.consume(repository.searchTracks(expression))
        }
    }
}
