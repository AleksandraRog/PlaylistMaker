package com.example.playlistmaker.data.local

import com.example.playlistmaker.domain.model.TrackTimePeriod
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

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
