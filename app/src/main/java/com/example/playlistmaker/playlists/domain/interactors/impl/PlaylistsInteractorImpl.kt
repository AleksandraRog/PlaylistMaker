package com.example.playlistmaker.playlists.domain.interactors.impl

import com.example.playlistmaker.common.domain.model.Playlist
import com.example.playlistmaker.playlists.domain.interactors.PlaylistsInteractor
import com.example.playlistmaker.playlists.domain.repository.PlaylistsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.LinkedList

class PlaylistsInteractorImpl(private val playlistsRepository: PlaylistsRepository) :
    PlaylistsInteractor {

    override fun loadPlaylistsFlow(): Flow<Pair<LinkedList<Playlist>, Int>> {

        return playlistsRepository.loadPlaylistsFlow().map { consData ->
            Pair(consData.result, consData.code)
        }
    }

    override fun del(playlist: Playlist): Flow<Boolean> {
        return playlistsRepository.delPlaylist(playlist)
    }
}
