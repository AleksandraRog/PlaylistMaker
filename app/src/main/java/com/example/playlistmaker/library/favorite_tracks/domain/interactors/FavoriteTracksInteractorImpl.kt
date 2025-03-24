package com.example.playlistmaker.library.favorite_tracks.domain.interactors

import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.domain.repsitory.DbRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.LinkedList

class FavoriteTracksInteractorImpl(private val repositoryDb: DbRepository) :
    FavoriteTracksInteractor {

   override fun loadTracksFlow(): Flow<Pair<LinkedList<Track>, Int>> {
        return repositoryDb.loadHistoryFlow().map { consData ->
            Pair(consData.result, consData.code)
        }
    }
}
