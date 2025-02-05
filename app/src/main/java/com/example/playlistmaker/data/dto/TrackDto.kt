package com.example.playlistmaker.data.dto

import java.util.Date

data class TrackDto (
    val trackName: String?,
    val artistName: String?,
    val artworkUrl100: String?,
    val previewUrl: String?,
    val releaseDate: Date?,
    val collectionName: String?,
    val primaryGenreName: String?,
    val country: String?,
    val trackId: Int?,
    val trackTimeMillis: Long?)
