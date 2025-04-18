package com.example.playlistmaker.favorite_tracks.domain.interactors.impl

import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.favorite_tracks.domain.interactors.FavoriteTracksInteractor
import com.example.playlistmaker.favorite_tracks.domain.repository.FavoriteTracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.LinkedList

class FavoriteTracksInteractorImpl(private val favoriteTracksRepository: FavoriteTracksRepository) :
    FavoriteTracksInteractor {

   override fun loadTracksFlow(): Flow<Pair<LinkedList<Track>, Int>> {
        return favoriteTracksRepository.loadHistoryFlow().map { consData ->
            Pair(consData.result, consData.code)
        }
    }
}
