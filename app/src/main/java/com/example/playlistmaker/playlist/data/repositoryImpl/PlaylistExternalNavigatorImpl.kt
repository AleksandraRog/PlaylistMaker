package com.example.playlistmaker.playlist.data.repositoryImpl

import com.example.playlistmaker.common.domain.repsitory.ExternalNavigator
import com.example.playlistmaker.playlist.domain.repository.PlaylistExternalNavigator
import kotlinx.coroutines.flow.Flow

class PlaylistExternalNavigatorImpl(private val externalNavigator: ExternalNavigator) :
    PlaylistExternalNavigator {

    override fun shareLinkFlow(shareAppLink: String): Flow<Any?> {
        return externalNavigator.shareLinkFlow(shareAppLink)
    }
}
