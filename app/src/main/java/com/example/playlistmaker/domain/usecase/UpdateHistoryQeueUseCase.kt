package com.example.playlistmaker.domain.usecases

import com.example.playlistmaker.domain.consumer.Consumer
import com.example.playlistmaker.domain.consumer.ConsumerData
import com.example.playlistmaker.domain.model.HistoryQueue
import com.example.playlistmaker.domain.model.Track
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
