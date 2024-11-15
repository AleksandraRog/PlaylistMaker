package com.example.playlistmaker

import com.google.gson.TypeAdapter
import com.google.gson.annotations.SerializedName
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.time.Duration.ofMillis
import java.util.Locale
import java.time.Duration

data class Track(
    val trackName: String?,
    val artistName: String?,
    val artworkUrl100: String?,
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
        out?.value(value?.toMillis())
    }

    override fun read(`in`: JsonReader?): TrackTimePeriod? {
        return if (`in` != null && `in`.peek() == JsonToken.NUMBER)
            TrackTimePeriod.fromMillis(`in`.nextLong()) else null
    }
}
