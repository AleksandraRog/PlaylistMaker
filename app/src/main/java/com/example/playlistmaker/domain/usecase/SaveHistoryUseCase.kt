package com.example.playlistmaker.domain.usecases

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.HistoryRepository
import java.util.LinkedList
import java.util.Queue

class SaveHistoryUseCase(private val repository: HistoryRepository) {

    fun execute(queue: Queue<Track>) {
        repository.registrationDiff(LinkedList(queue))
    }
}
