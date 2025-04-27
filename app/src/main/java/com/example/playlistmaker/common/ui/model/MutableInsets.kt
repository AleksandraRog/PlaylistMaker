package com.example.playlistmaker.common.ui.model

import androidx.core.graphics.Insets

data class MutableInsets(
    var left: Int,
    var top: Int,
    var right: Int,
    var bottom: Int
) {
    fun toInsets(): Insets {
        return Insets.of(left, top, right, bottom)
    }

    fun Insets.toMutableInsets(): MutableInsets {
        return MutableInsets(left, top, right, bottom)
    }
}