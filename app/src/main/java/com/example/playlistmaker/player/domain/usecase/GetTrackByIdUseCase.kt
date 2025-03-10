package com.example.playlistmaker.domain.usecases

import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.search.domain.reposirory.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetTrackByIdUseCase(private val repository: HistoryRepository) {

    fun loadTrackFlow(trackId: Int): Flow<Pair<Track, Int>> {
        return repository.getTrackByIdFlow(trackId).map { consData ->
            Pair(consData.result, consData.code)
        }
    }
}
