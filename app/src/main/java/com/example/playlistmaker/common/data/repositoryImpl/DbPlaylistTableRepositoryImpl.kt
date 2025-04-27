package com.example.playlistmaker.common.data.repositoryImpl

import com.example.playlistmaker.common.data.db.AppDatabase
import com.example.playlistmaker.common.data.mapper.PlaylistDbConvertor
import com.example.playlistmaker.common.data.mapper.PlaylistDbConvertor.toDomainModel
import com.example.playlistmaker.common.data.mapper.PlaylistDbConvertor.toEntity
import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.common.domain.model.Playlist
import com.example.playlistmaker.common.domain.repsitory.DbPlaylistTableRepository
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

class DbPlaylistTableRepositoryImpl(private val appDatabase: AppDatabase,) : DbPlaylistTableRepository {

    private val _stateFlow = MutableSharedFlow<Unit>(replay = 1)
    private val stateFlow = _stateFlow.asSharedFlow()

    override fun notifyDatabaseChanged() {
        _stateFlow.tryEmit(Unit)
    }

    override fun loadPlaylistsFlow(): Flow<ConsumerData<LinkedList<Playlist>>> = stateFlow
        .onStart { emit(Unit) }
        .transform {
            val playlists = PlaylistDbConvertor
                .mapEntityToDomainModel(
                    LinkedList(
                        appDatabase.trackDao().getPlaylists()
                    )
                )
            emit(ConsumerData(playlists))
        }
        .flowOn(Dispatchers.IO)

    override fun loadPlaylistsNotContainingTrack(trackId: Int): Flow<ConsumerData<LinkedList<Playlist>>> =
        stateFlow
            .onStart { emit(Unit) }
            .transform {
                val playlists = PlaylistDbConvertor.mapEntityToDomainModel(
                    LinkedList(
                        appDatabase.trackDao().getPlaylistsNotContainingTrack(trackId)
                    )
                )
                emit(ConsumerData(playlists))
            }
            .flowOn(Dispatchers.IO)

    override fun loadPlaylistsContainingTrack(trackId: Int): Flow<ConsumerData<LinkedList<Playlist>>> = stateFlow
        .onStart { emit(Unit) }
            .transform {
                val playlists = PlaylistDbConvertor
                    .mapEntityToDomainModel(
                        LinkedList(
                            appDatabase.trackDao().getPlaylistsContainingTrack(trackId)
                        )
                    )
                emit(ConsumerData(playlists, 1))
            }
            .flowOn(Dispatchers.IO)

    override fun insertPlaylist(playlist: Playlist): Flow<Playlist> = flow {
        val insertedPlaylistEntity = withContext(Dispatchers.IO) {
            val playlistEntity = playlist.toEntity()
            appDatabase.trackDao().insertPlaylist(playlistEntity)
            playlistEntity
        }
        notifyDatabaseChanged()
        emit(insertedPlaylistEntity.toDomainModel())
    }

    override fun delPlaylist(playlist: Playlist): Flow<Boolean> = flow {
        withContext(Dispatchers.IO) {
            appDatabase.trackDao().deletePlaylistAndTracks(playlist.toEntity())
        }
        notifyDatabaseChanged()
        emit(false)
    }
}
