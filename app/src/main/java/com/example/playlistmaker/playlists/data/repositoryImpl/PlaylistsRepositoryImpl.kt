package com.example.playlistmaker.playlists.data.repositoryImpl

import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.common.domain.model.Playlist
import com.example.playlistmaker.common.domain.repsitory.DbPlaylistTableRepository
import com.example.playlistmaker.playlists.domain.repository.PlaylistsRepository
import kotlinx.coroutines.flow.Flow
import java.util.LinkedList

class PlaylistsRepositoryImpl(private val repositoryDb: DbPlaylistTableRepository) : PlaylistsRepository {

    // db
    override fun loadPlaylistsFlow(): Flow<ConsumerData<LinkedList<Playlist>>> {
        return repositoryDb.loadPlaylistsFlow()
    }

    override fun delPlaylist(playlist: Playlist): Flow<Boolean> {
        return repositoryDb.delPlaylist(playlist)
    }
}
