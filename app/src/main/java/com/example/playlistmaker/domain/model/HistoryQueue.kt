package com.example.playlistmaker.domain.model
import com.example.playlistmaker.Creator
import java.util.LinkedList
import java.util.Queue
import java.util.function.Predicate

class HistoryQueue(private val queue: Queue<Track> = LinkedList()) : Queue<Track> by queue {

    private val saveHistoryUseCase = Creator.provideSaveHistoryUseCase()

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
