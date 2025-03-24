package com.example.playlistmaker.player.domain.interactors.impl

import android.os.Bundle
import com.example.playlistmaker.common.domain.model.ExtraActionBundleKey
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.domain.repsitory.DbRepository
import com.example.playlistmaker.player.domain.interactors.TrackInteractor
import com.example.playlistmaker.search.domain.reposirory.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrackInteractorImpl(private val repositoryDb: DbRepository,
                          private val repositoryHisrory: HistoryRepository
    ) : TrackInteractor {

    override fun favoriteControl(track: Track, isFavorite: Boolean): Flow<Boolean> {
        return if (isFavorite) repositoryDb.setTrack(track,) else repositoryDb.delTrack(track,)
    }

    override fun loadTrackFlow(trackIdBundle: Bundle): Flow<Pair<Track, Int>> {

       when {
            trackIdBundle.containsKey(ExtraActionBundleKey.TRACK_EXTRA_HISTORY.value) -> {
                val trackId = trackIdBundle.getInt(ExtraActionBundleKey.TRACK_EXTRA_HISTORY.value, -1)
                return repositoryHisrory.getTrackByIdFlow(trackId).map { consData ->
                    Pair(consData.result, consData.code)
                }
            }
            trackIdBundle.containsKey(ExtraActionBundleKey.TRACK_EXTRA_FAVORITE.value) -> {

                val trackId = trackIdBundle.getInt(ExtraActionBundleKey.TRACK_EXTRA_FAVORITE.value, -1)
                return repositoryDb.getTrackByIdFlow(trackId).map { consData ->
                    Pair(consData.result, consData.code)
                }
            }
            else -> {
                val trackId = trackIdBundle.getInt(ExtraActionBundleKey.TRACK_EXTRA_HISTORY.value, -1)
                return repositoryHisrory.getTrackByIdFlow(trackId).map { consData ->
                    Pair(consData.result, consData.code)
                }
            }
        }
    }
}
