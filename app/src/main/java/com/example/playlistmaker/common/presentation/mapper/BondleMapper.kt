package com.example.playlistmaker.common.presentation.mapper

import android.os.Bundle
import com.example.playlistmaker.common.presentation.model.ExtraActionBundleKey

object BundleMapper {

    fun Bundle.toModel(): ExtraActionBundleKey? {
        return when {
            this.containsKey(ExtraActionBundleKey.TRACK_EXTRA_HISTORY.value) -> {
               ExtraActionBundleKey.valueOf("TRACK_EXTRA_HISTORY").apply { id = this@toModel.getInt(
                   ExtraActionBundleKey.TRACK_EXTRA_HISTORY.value, -1)}
            }
            this.containsKey(ExtraActionBundleKey.TRACK_EXTRA_FAVORITE.value) -> {
                ExtraActionBundleKey.valueOf("TRACK_EXTRA_FAVORITE").apply {id = this@toModel.getInt(
                    ExtraActionBundleKey.TRACK_EXTRA_FAVORITE.value, -1)}
            }
            this.containsKey(ExtraActionBundleKey.TRACK_EXTRA_PLAYLIST.value) -> {
                ExtraActionBundleKey.valueOf("TRACK_EXTRA_PLAYLIST").apply {id = this@toModel.getInt(
                    ExtraActionBundleKey.TRACK_EXTRA_PLAYLIST.value, -1)}
            }
            this.containsKey(ExtraActionBundleKey.PLAYLIST_EXTRA_PLAYLIST.value) -> {
                ExtraActionBundleKey.valueOf("PLAYLIST_EXTRA_PLAYLIST").apply {id = this@toModel.getInt(
                    ExtraActionBundleKey.PLAYLIST_EXTRA_PLAYLIST.value, -1)}
            }
           else -> null
        }
    }
}
