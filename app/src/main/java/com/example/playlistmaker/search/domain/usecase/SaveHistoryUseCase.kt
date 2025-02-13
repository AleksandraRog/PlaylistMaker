package com.example.playlistmaker.search.domain.usecase

import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.search.domain.reposirory.HistoryRepository
import java.util.LinkedList
import java.util.Queue

class SaveHistoryUseCase(private val repository: HistoryRepository) {

    fun execute(queue: Queue<Track>) {
        repository.registrationDiff(LinkedList(queue))
    }
}
