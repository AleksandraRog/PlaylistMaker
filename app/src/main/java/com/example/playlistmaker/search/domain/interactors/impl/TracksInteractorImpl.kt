package com.example.playlistmaker.search.domain.interactors.impl

import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.search.domain.interactors.TracksInteractor
import com.example.playlistmaker.search.domain.reposirory.ApiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TracksInteractorImpl(private val repository: ApiRepository) : TracksInteractor {

    override fun searchTracksFlow(expression: String): Flow<Pair<List<Track>, Int>> {

        return repository.searchTracksFlow(expression).map { consData ->
            Pair(consData.result, consData.code)
        }
    }
}
