package com.example.playlistmaker.playlist.domain.repository

import kotlinx.coroutines.flow.Flow

interface PlaylistExternalNavigator {
    fun shareLinkFlow(shareAppLink: String): Flow<Any?>
}