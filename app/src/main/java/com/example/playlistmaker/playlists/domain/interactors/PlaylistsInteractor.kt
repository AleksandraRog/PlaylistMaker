package com.example.playlistmaker.playlists.domain.interactors

import com.example.playlistmaker.common.domain.model.Playlist
import kotlinx.coroutines.flow.Flow
import java.util.LinkedList

interface PlaylistsInteractor {
    fun loadPlaylistsFlow(): Flow<Pair<LinkedList<Playlist>, Int>>
    fun del(playlist: Playlist): Flow<Boolean>
}
