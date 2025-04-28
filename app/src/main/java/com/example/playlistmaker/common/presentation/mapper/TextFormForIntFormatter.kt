package com.example.playlistmaker.common.presentation.mapper

object TextFormForIntFormatter {

    fun pluralFormTrackCount (number: Int, ): String {
        val n = if (number < 14 ) number else number % 10
        val final = when (n) {
            1 -> ""
            in 2..4 -> "a"
            else -> "ов"
        }
        return number.toString() + BASE_WORLD_TRACK + final
    }

    fun pluralFormMinuteCount (number: Long, ): String {
        val n = if (number < 14L ) number else number % 10
        val final = when (n) {
            1L -> "а"
            in 2L..4L -> "ы"
            else -> ""
        }
        return number.toString() + BASE_WORLD_MINUTE + final
    }

    private const val BASE_WORLD_TRACK = " трек"
    private const val BASE_WORLD_MINUTE = " минут"
}
