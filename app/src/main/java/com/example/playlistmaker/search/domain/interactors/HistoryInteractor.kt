package com.example.playlistmaker.search.domain.interactors

import com.example.playlistmaker.common.domain.model.Track
import kotlinx.coroutines.flow.Flow
import java.util.LinkedList

interface HistoryInteractor {
    fun loadTracksFlow(): Flow<Pair<LinkedList<Track>, Int>>
}
