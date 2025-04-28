package com.example.playlistmaker.common.data.repositoryImpl

import com.example.playlistmaker.common.data.db.AppDatabase
import com.example.playlistmaker.common.data.mapper.TrackMapper
import com.example.playlistmaker.common.data.mapper.TrackMapper.toDomainModel
import com.example.playlistmaker.common.data.mapper.TrackMapper.toTrackEntity
import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.domain.repsitory.DbTrackTableRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import java.util.LinkedList

class DbTrackTableRepositoryImpl(
    private val appDatabase: AppDatabase,
) : DbTrackTableRepository {

    private val _stateFlow = MutableSharedFlow<Unit>(replay = 1)
    private val stateFlow = _stateFlow.asSharedFlow()

    override fun insertTrack(track: Track): Flow<Boolean> = flow {
        withContext(Dispatchers.IO) {
            appDatabase.trackDao().insertTrack(track.toTrackEntity())
        }
        notifyDatabaseChanged()
        emit(true)
    }

    override fun delTrack(track: Track): Flow<Boolean> = flow {
        withContext(Dispatchers.IO) {
            appDatabase.trackDao().deleteTrack(track.toTrackEntity())
        }
        notifyDatabaseChanged()
        emit(false)
    }

    override suspend fun notifyDatabaseChanged() {
        _stateFlow.tryEmit(Unit)
    }

    override fun loadHistoryFlow(): Flow<ConsumerData<LinkedList<Track>>> = stateFlow
        .onStart { emit(Unit) }
        .transform {
            val resultList = appDatabase.trackDao().getFavoriteTracks()
            val result = TrackMapper.mapEntityToDomainModel(LinkedList(resultList))
            val resultCode = 200
            emit(ConsumerData(result, resultCode))
        }
        .flowOn(Dispatchers.IO)

    override suspend fun getFavoriteId(trackId: Int): Boolean {

        val favoritCount = withContext(Dispatchers.IO) {
            appDatabase.trackDao().getCountFavoriteTrackById(trackId)
        }
        return (favoritCount != 0)
    }

    override fun getTrackByIdFlow(trackId: Int): Flow<ConsumerData<Track>> = flow {

        val favoritTrack = withContext(Dispatchers.IO) {
            appDatabase.trackDao().getTrack(trackId).toDomainModel()
        }
        emit(ConsumerData(favoritTrack))
    }

    override fun getTrackListInPlaylist(playlistId: Int): Flow<ConsumerData<LinkedList<Track>>> = stateFlow
        .onStart { emit(Unit) }
        .transform {
            val resultList = appDatabase.trackDao().getTrackListInPlaylist(playlistId)
            val result = TrackMapper.mapEntityToDomainModel(LinkedList(resultList))
            val resultCode = 200
            emit(ConsumerData(result, resultCode))
        }
        .flowOn(Dispatchers.IO)
}
