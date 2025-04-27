package com.example.playlistmaker.playlists.domain.repository

import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.common.domain.model.Playlist
import kotlinx.coroutines.flow.Flow
import java.util.LinkedList

interface PlaylistsRepository {

   // db
    fun loadPlaylistsFlow(): Flow<ConsumerData<LinkedList<Playlist>>>
    fun delPlaylist(playlist: Playlist): Flow<Boolean>
}
