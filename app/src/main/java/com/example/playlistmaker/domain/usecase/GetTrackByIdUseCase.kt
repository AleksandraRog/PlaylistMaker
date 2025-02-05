package com.example.playlistmaker.domain.usecases

import com.example.playlistmaker.domain.consumer.Consumer
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.HistoryRepository
import org.koin.java.KoinJavaComponent.getKoin
import java.util.concurrent.ExecutorService

class GetTrackByIdUseCase(private val repository: HistoryRepository) {

    private val executor: ExecutorService by lazy { getKoin().get<ExecutorService>() }

    fun execute (trackId: Int, consumer: Consumer<Track>) {
        executor.execute {
            consumer.consume(repository.getTrackById(trackId))
        }
    }
}
