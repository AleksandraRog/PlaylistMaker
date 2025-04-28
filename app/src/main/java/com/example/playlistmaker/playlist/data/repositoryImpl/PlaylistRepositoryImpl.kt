package com.example.playlistmaker.playlist.data.repositoryImpl

import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.common.domain.model.Playlist
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.domain.repsitory.DbElementKeyRepository
import com.example.playlistmaker.common.domain.repsitory.DbPlaylistTableRepository
import com.example.playlistmaker.common.domain.repsitory.DbTrackTableRepository
import com.example.playlistmaker.playlist.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import java.util.LinkedList

class PlaylistRepositoryImpl(
    private val playlistTableRepository: DbPlaylistTableRepository,
    private val trackTableRepository: DbTrackTableRepository,
    private val elementKeyRepository: DbElementKeyRepository,
) : PlaylistRepository {

    override fun getPlaylistByIdFlow(id: Int): Flow<Playlist> {
        return playlistTableRepository.getPlaylist(id)
    }

    override fun getTrackListInPlaylist(id: Int): Flow<ConsumerData<LinkedList<Track>>> {
        return trackTableRepository.getTrackListInPlaylist(id)
    }

    override fun deletePlaylist(playlist: Playlist): Flow<Boolean> {
        return playlistTableRepository.delPlaylist(playlist)
    }

    override fun deleteTrackFromPlaylist(track: Track, playlist: Playlist): Flow<Boolean> {
        return elementKeyRepository.deleteTrackFromPlaylist(track, playlist)
    }
}
