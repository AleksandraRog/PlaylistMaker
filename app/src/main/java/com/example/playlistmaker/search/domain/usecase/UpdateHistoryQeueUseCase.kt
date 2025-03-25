package com.example.playlistmaker.search.domain.usecase

import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.domain.repsitory.DbRepository
import com.example.playlistmaker.common.presentation.UiState
import com.example.playlistmaker.search.domain.model.HistoryQueue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UpdateHistoryQueueUseCase(private val repositoryDb: DbRepository,) {

    fun executeFlow(track: Track, queue: HistoryQueue): Flow<UiState> = flow {
        track.apply { isFavorite = repositoryDb.getFavoriteId(this.trackId) }
        queue.removeIf { it.trackId == track.trackId }
        if (queue.size == MAX_HISTORYLIST_SIZE) {
            queue.poll()
        }
        queue.offer(track)
        emit(UiState.AnyTrack(track.trackId))

    }

}

private const val MAX_HISTORYLIST_SIZE = 10
