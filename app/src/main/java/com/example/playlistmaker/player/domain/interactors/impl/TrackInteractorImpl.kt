package com.example.playlistmaker.player.domain.interactors.impl

import com.example.playlistmaker.common.domain.model.Playlist
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.presentation.model.ExtraActionBundleKey
import com.example.playlistmaker.common.presentation.model.ItemPlaylistWrapper
import com.example.playlistmaker.player.domain.GetValueToastString
import com.example.playlistmaker.player.domain.interactors.TrackInteractor
import com.example.playlistmaker.player.domain.reposirory.TrackRepository
import com.example.playlistmaker.search.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.LinkedList

class TrackInteractorImpl(private val trackRepository: TrackRepository,
                          private val repositoryHistory : HistoryRepository,
                          private val getValueToastString: GetValueToastString,
    ) : TrackInteractor {

    override suspend fun favoriteControl(track: Track, isFavorite: Boolean): Flow<Boolean> {
        track.apply { this@apply.isFavorite = isFavorite }
        val resalt = if (isFavorite){
            trackRepository.insertTrack(track,)
        } else if (trackRepository.getTrackByIdInPlaylist(track.trackId,)) {
            trackRepository.insertTrack(track,)
        } else {
            trackRepository.delTrack(track,)
        }
        return resalt
    }

    override fun loadTrackFlow(extraActionBundleKey: ExtraActionBundleKey?): Flow<Pair<Track?, Int>> {

        when (extraActionBundleKey) {
            null -> {
                return  flow { emit(Pair(null, -1)) }
            }

            else -> {
                if (extraActionBundleKey.id < 0) {
                    return   flow { emit(Pair(null, extraActionBundleKey.id)) }
                } else {
                    return when (extraActionBundleKey) {
                        ExtraActionBundleKey.TRACK_EXTRA_HISTORY -> {
                            repositoryHistory.getTrackByIdFlow(extraActionBundleKey.id)
                                .map { consData ->
                                    Pair(consData.result, consData.code)
                                }
                        }

                        ExtraActionBundleKey.TRACK_EXTRA_FAVORITE, ExtraActionBundleKey.TRACK_EXTRA_PLAYLIST -> {
                            trackRepository.getTrackByIdFlow(extraActionBundleKey.id)
                                .map { consData ->
                                    Pair(consData.result, consData.code)
                                }
                        }
                        else ->{ flow { emit(Pair(null, -1)) }}
                    }
                }
            }
        }
    }

    override fun loadPlaylists(track: Track): Flow<LinkedList<Pair<Playlist,Boolean>>> {
        val trueFlow = trackRepository
            .loadPlaylistsContainingTrack(track.trackId)
            .map { consData ->
                consData.result.mapTo(LinkedList()) { playlist ->
                    Pair(playlist, true)
                }
            }

        val falseFlow = trackRepository
            .loadPlaylistsNotContainingTrack(track.trackId)
            .map { consData ->
                consData.result.mapTo(LinkedList()) { playlist ->
                    Pair(playlist, false)
                }
            }

        return combine(trueFlow, falseFlow) { listTrue, listFalse ->
            val combinedList = LinkedList<Pair<Playlist, Boolean>>()
            combinedList.addAll(listFalse)
            combinedList.addAll(listTrue)

            combinedList
        }
    }

    override  fun insertTrackToPlaylist(track: Track, playlist: ItemPlaylistWrapper.PlaylistPair): Flow<String> {
        return if (playlist.isOnPlaylist)
            flow { emit(String.format(getValueToastString.negativeMessage,playlist.playlist.playlistName)) }
        else
            trackRepository.insertTrackToPlaylist(track, playlist.playlist).map {
                String.format(getValueToastString.positiveMessage,playlist.playlist.playlistName)
            }
    }
}
