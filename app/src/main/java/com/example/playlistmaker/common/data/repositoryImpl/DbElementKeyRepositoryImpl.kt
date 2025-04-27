package com.example.playlistmaker.common.data.repositoryImpl

import com.example.playlistmaker.common.data.db.AppDatabase
import com.example.playlistmaker.common.data.db.entity.ContentPlaylistEntity
import com.example.playlistmaker.common.data.mapper.PlaylistDbConvertor.toEntity
import com.example.playlistmaker.common.data.mapper.TrackMapper.toTrackEntity
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.domain.repsitory.DbElementKeyRepository
import com.example.playlistmaker.common.domain.repsitory.DbPlaylistTableRepository
import com.example.playlistmaker.common.domain.model.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class DbElementKeyRepositoryImpl(private val appDatabase: AppDatabase,
                                 private val dbPlaylistTableRepository : DbPlaylistTableRepository
) : DbElementKeyRepository {

    override fun insertTrackToPlaylist(track: Track, playlist: Playlist): Flow<Boolean> = flow {
        val trackEntity = track.toTrackEntity()
        val playlistEntity = playlist.toEntity()
        val result = withContext(Dispatchers.IO) {
            appDatabase.trackDao().insertTrackAndPlaylist(ContentPlaylistEntity(trackId = track.trackId, playlistId=playlistEntity.playlistId), trackEntity, playlistEntity,)
            true}
        dbPlaylistTableRepository.notifyDatabaseChanged()
        emit(result)
    }

    override suspend fun getTrackByIdInPlaylist(trackId: Int): Boolean {
        val countInPlaylist = withContext(Dispatchers.IO) {
            appDatabase.trackDao().getTrackByIdInPlaylist(trackId)
        }
        return (countInPlaylist != 0)
    }

    override fun del() :Flow<Boolean> = flow {
        withContext(Dispatchers.IO) {
            appDatabase.trackDao().deleteContent()
        }
        emit(true)
    }
}
