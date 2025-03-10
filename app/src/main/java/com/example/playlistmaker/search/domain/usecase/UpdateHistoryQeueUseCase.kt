package com.example.playlistmaker.search.domain.usecase

import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.search.domain.model.HistoryQueue
import com.example.playlistmaker.search.presentation.TracksState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UpdateHistoryQueueUseCase() {

    fun executeFlow(track: Track, queue: HistoryQueue): Flow<TracksState> = flow {

        queue.removeIf { it.trackId == track.trackId }
        if (queue.size == MAX_HISTORYLIST_SIZE) {
            queue.poll()
        }
        queue.offer(track)
        emit(TracksState.AnyTrack(track.trackId))

    }

}

private const val MAX_HISTORYLIST_SIZE = 10
