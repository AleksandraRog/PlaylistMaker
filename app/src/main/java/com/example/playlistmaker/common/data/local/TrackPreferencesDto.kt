package com.example.playlistmaker.common.data.local

import java.util.Date

data class TrackPreferencesDto (
    val trackName: String,
    val artistName: String,
    val artworkUrl100: String,
    val previewUrl: String,
    val releaseDate: Date?,
    val collectionName: String,
    val primaryGenreName: String,
    val country: String,
    val trackId: Int,
    val isFavorite: Boolean,
    val trackTimeMillis: Long)
