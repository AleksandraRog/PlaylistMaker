package com.example.playlistmaker.favorite_tracks.data.repositoryImpl

import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.domain.repsitory.DbTrackTableRepository
import com.example.playlistmaker.favorite_tracks.domain.repository.FavoriteTracksRepository
import kotlinx.coroutines.flow.Flow
import java.util.LinkedList

class FavoriteTracksRepositoryImpl(private val repositoryDb: DbTrackTableRepository) :
    FavoriteTracksRepository {

    override fun loadHistoryFlow(): Flow<ConsumerData<LinkedList<Track>>> {
        return repositoryDb.loadHistoryFlow()
    }
}
