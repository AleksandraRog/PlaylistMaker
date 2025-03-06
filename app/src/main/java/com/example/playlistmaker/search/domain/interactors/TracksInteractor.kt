package com.example.playlistmaker.search.domain.interactors
import com.example.playlistmaker.common.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun searchTracksFlow(expression: String): Flow<Pair<List<Track>, Int>>
}
