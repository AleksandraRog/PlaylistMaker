package com.example.playlistmaker

import com.google.gson.TypeAdapter
import com.google.gson.annotations.SerializedName
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.text.SimpleDateFormat
import java.time.Duration.ofMillis
import java.util.Locale
import java.time.Duration
import java.util.Date

data class Track(
    val trackName: String?,
    val artistName: String?,
    val artworkUrl100: String?,
    val previewUrl: String?,
    val releaseDate: Date?,
    val collectionName: String?,
    val primaryGenreName: String?,
    val country: String?,
    val trackId: Int?,
    @SerializedName("trackTimeMillis")
    val trackTime: TrackTimePeriod?
)

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

class CustomTimeTypeAdapter : TypeAdapter<TrackTimePeriod>() {

    override fun write(out: JsonWriter?, value: TrackTimePeriod?) {
        if (value != null) {
            out?.value(value.toMillis())
        } else { out?.nullValue() }

    }

    override fun read(`in`: JsonReader?): TrackTimePeriod? {
        return if (`in` != null && `in`.peek() == JsonToken.NUMBER)
            TrackTimePeriod.fromMillis(`in`.nextLong()) else null
    }
}

class CustomDateTypeAdapter : TypeAdapter<Date?>() {

    companion object {
        const val FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    }

    private val formatter = SimpleDateFormat(FORMAT_PATTERN, Locale.getDefault())
    override fun write(out: JsonWriter?, value: Date?) {
        if (value != null) {
            out?.value(formatter.format(value))
        } else { out?.nullValue() }

    }

    override fun read(`in`: JsonReader?): Date? {
        return if (`in` != null && `in`.peek() == JsonToken.STRING)
            formatter.parse(`in`.nextString()) else null
    }
}
