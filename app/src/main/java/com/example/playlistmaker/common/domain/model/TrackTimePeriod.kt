package com.example.playlistmaker.common.domain.model

import java.time.Duration
import java.time.Duration.ofMillis
import java.util.Locale

data class TrackTimePeriod(val duration: Duration?) {

    override fun toString(): String {
        return String.format(
            Locale.getDefault(),
            "%02d:%02d",
            duration?.toMinutes(),
            duration?.seconds!!.rmd(60)
        )
    }

    private fun Long.rmd(value: Long): Long = this % value

    fun toMillis(): Long? {
        return duration?.toMillis()
    }

    companion object {
        fun fromMillis(millis: Long?): TrackTimePeriod? {
            return if (millis == null) null else TrackTimePeriod(ofMillis(millis))
        }
    }
}
