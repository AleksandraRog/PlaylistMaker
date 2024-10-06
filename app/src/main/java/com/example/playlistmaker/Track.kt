package com.example.playlistmaker


data class Track(
    val trackName: String?,
    val artistName: String?,
    val trackTimeMillis: Long?,
    val artworkUrl100: String?,
    var trackTime: String?
) {

    fun trackTime() {
        trackTime = if (trackTimeMillis == null) trackTimeMillis else
            String.format("%02d:%02d", (trackTimeMillis / 1000) / 60, (trackTimeMillis / 1000) % 60)
    }
}
