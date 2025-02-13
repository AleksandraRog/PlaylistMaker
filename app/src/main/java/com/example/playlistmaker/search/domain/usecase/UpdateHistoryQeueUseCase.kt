package com.example.playlistmaker.search.domain.usecase

import com.example.playlistmaker.common.domain.consumer.Consumer
import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.search.domain.model.HistoryQueue
import org.koin.java.KoinJavaComponent.getKoin
import java.util.concurrent.ExecutorService

class UpdateHistoryQueueUseCase() {

    private val executor: ExecutorService by lazy { getKoin().get<ExecutorService>() }

    fun execute (track: Track, queue: HistoryQueue, consumer: Consumer<Track>) {
        executor.execute {
            queue.removeIf { it.trackId == track.trackId }
            if (queue.size == MAX_HISTORYLIST_SIZE) {
                queue.poll()
            }
            queue.offer(track)

            consumer.consume(ConsumerData(track))
        }

    }
}

private const val MAX_HISTORYLIST_SIZE = 10
