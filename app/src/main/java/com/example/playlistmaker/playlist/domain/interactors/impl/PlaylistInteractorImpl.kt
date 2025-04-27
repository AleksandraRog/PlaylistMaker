package com.example.playlistmaker.playlist.domain.interactors.impl

import com.example.playlistmaker.common.domain.model.Playlist
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.domain.model.TrackTimePeriod
import com.example.playlistmaker.common.presentation.mapper.TextFormForIntFormatter
import com.example.playlistmaker.common.presentation.model.SharingObjects
import com.example.playlistmaker.playlist.domain.interactors.PlaylistInteractor
import com.example.playlistmaker.playlist.domain.repository.PlaylistExternalNavigator
import com.example.playlistmaker.playlist.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import java.util.LinkedList

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository,
    private val externalNavigator: PlaylistExternalNavigator,
    ) : PlaylistInteractor {

    override fun loadPlaylistFlow(playlistId: Int): Flow<Pair<Playlist?, List<Track>?>> {

        return when {
            playlistId < 0 -> {
                flow { emit(null to null) }
            }
            else -> {
                val playlistFlow = playlistRepository.getPlaylistByIdFlow(playlistId)

                val tracklistFlow =
                    playlistRepository.getTrackListInPlaylist(playlistId)
                        .map { cons ->
                            if (cons.code == 200) {
                                cons.result
                            } else
                                LinkedList<Track>()
                        }
                combine(playlistFlow, tracklistFlow) { playlist, tracksList ->
                    playlist.apply {
                        playlistTime = TrackTimePeriod.fromMillis( tracksList.sumOf{
                            it.trackTime?.toMillis() ?: 0L })
                        } to tracksList
                    }
            }
        }
    }

    override fun getIntentProperty(
        sharingObjects: SharingObjects,
        playlist: Playlist
    ): Flow<SharingObjects> {
        return getSharePlaylistLink(playlist).transform { link ->
            val intents = externalNavigator.shareLinkFlow(link).first()
            emit(sharingObjects.apply { intent = intents })
        }
    }

    override fun getSharePlaylistLink(playlist: Playlist): Flow<String> {
        return playlistRepository.getTrackListInPlaylist(id = playlist.playlistId).map { cons ->
            val shareString = buildString {
                appendLine(playlist.playlistName)
                if (playlist.description.isNotEmpty()) {
                    appendLine(playlist.description)
                }
                appendLine(TextFormForIntFormatter.pluralFormTrackCount(playlist.playlistSize))
                if (cons.code == 200) {
                    cons.result.forEachIndexed { index, track -> appendLine("${index + 1}. ${track.artistName} - ${track.trackName} (${track.trackTime.toString()}) ") }
                }
            }
            shareString
        }
    }

    override fun deletePlaylist(playlist: Playlist): Flow<Boolean> {
        return playlistRepository.deletePlaylist(playlist)
    }

    override fun deleteTrackFromPlaylist(track: Track, playlist: Playlist): Flow<Boolean> {
        return playlistRepository.deleteTrackFromPlaylist(track, playlist)
    }
}
