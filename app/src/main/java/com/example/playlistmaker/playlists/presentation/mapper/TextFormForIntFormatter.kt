package com.example.playlistmaker.playlists.presentation.mapper

object TextFormForIntFormatter {

    fun pluralFormTrackCount (number: Int, ): String {
        val n = if (number < 14 ) number else number % 10
        val final = when (n) {
            1 -> ""
            in 2..4 -> "a"
            else -> "ов"
        }
        return number.toString() + BASE_WORLD + final
    }
    private const val BASE_WORLD = " трек"
}
