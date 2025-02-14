package com.example.playlistmaker.search.domain.model
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.search.domain.usecase.SaveHistoryUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.LinkedList
import java.util.Queue
import java.util.function.Predicate

class HistoryQueue(private val queue: Queue<Track> = LinkedList()) : Queue<Track> by queue,
    KoinComponent {

    private val saveHistoryUseCase: SaveHistoryUseCase by inject()

    override fun offer(e: Track?): Boolean {
        val result = queue.offer(e)
        saveHistoryUseCase.execute(queue)
        return result
    }

    override fun add(element: Track?): Boolean {
        val result = queue.add(element)
        saveHistoryUseCase.execute(queue)
        return result
    }

    override fun remove(): Track {
        val result = queue.remove()
        saveHistoryUseCase.execute(queue)
        return result
    }

    override fun poll(): Track? {
        val result = queue.poll()
        saveHistoryUseCase.execute(queue)
        return result
    }

    override fun clear() {
        queue.clear()
        saveHistoryUseCase.execute(queue)

    }

    override fun removeIf(filter: Predicate<in Track>): Boolean {

        val result = queue.removeIf(filter)
        if (result) {saveHistoryUseCase.execute(queue)}
        return result
    }
}
