package com.example.playlistmaker.player.data.repositoryImpl

import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.common.domain.model.Playlist
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.domain.repsitory.DbElementKeyRepository
import com.example.playlistmaker.common.domain.repsitory.DbPlaylistTableRepository
import com.example.playlistmaker.common.domain.repsitory.DbTrackTableRepository
import com.example.playlistmaker.player.domain.reposirory.TrackRepository
import kotlinx.coroutines.flow.Flow
import java.util.LinkedList

class TrackRepositoryImpl(private val repositoryDb : DbElementKeyRepository,
                          private val repositoryTrackTable : DbTrackTableRepository,
                          private val repositoryPlaylistTable : DbPlaylistTableRepository,
    ): TrackRepository {
    override fun insertTrack(track: Track): Flow<Boolean> {
        return repositoryTrackTable.insertTrack(track,)
    }

    override suspend fun getTrackByIdInPlaylist(trackId: Int): Boolean {
        return repositoryDb.getTrackByIdInPlaylist(trackId)
    }

    override fun delTrack(track: Track): Flow<Boolean> {
        return repositoryTrackTable.delTrack(track,)
    }

    override fun getTrackByIdFlow(trackId: Int): Flow<ConsumerData<Track>> {
        return repositoryTrackTable.getTrackByIdFlow(trackId)
    }

    override fun insertTrackToPlaylist(track: Track, playlist: Playlist): Flow<Boolean> {
        return repositoryDb.insertTrackToPlaylist(track, playlist)
    }

    override fun loadPlaylistsContainingTrack(trackId: Int): Flow<ConsumerData<LinkedList<Playlist>>> {
        return repositoryPlaylistTable
            .loadPlaylistsContainingTrack(trackId)
    }

    override fun loadPlaylistsNotContainingTrack(trackId: Int): Flow<ConsumerData<LinkedList<Playlist>>> {
        return repositoryPlaylistTable
            .loadPlaylistsNotContainingTrack(trackId)
    }
}
