package com.example.playlistmaker.common.domain.model

import java.util.Date

data class Track(
    val trackName: String,
    val artistName: String,
    val artworkUrl100: String,
    val previewUrl: String,
    val releaseDate: Date?,
    val collectionName: String,
    val primaryGenreName: String,
    val country: String,
    val trackId: Int,
    val trackTime: TrackTimePeriod?
)
