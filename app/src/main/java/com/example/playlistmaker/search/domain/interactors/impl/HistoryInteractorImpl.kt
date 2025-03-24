package com.example.playlistmaker.search.domain.interactors.impl

import android.util.Log
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.search.domain.interactors.HistoryInteractor
import com.example.playlistmaker.search.domain.reposirory.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.LinkedList

class HistoryInteractorImpl(private val repository: HistoryRepository) : HistoryInteractor {

    override fun loadTracksFlow(): Flow<Pair<LinkedList<Track>, Int>> {
        return repository.loadHistoryFlow().map { consData ->
            Pair(consData.result, consData.code)
        }
    }
}
