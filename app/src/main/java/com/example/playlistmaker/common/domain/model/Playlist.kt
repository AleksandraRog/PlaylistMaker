package com.example.playlistmaker.common.domain.model


class Playlist(
    var playlistName: String,
    val fileUrl: String = "",
    val description: String = "",
    val playlistId: Int = 0,
    val playlistSize: Int = 0,
    var playlistTime: TrackTimePeriod? = null
    )
