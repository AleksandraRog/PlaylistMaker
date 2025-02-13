package com.example.playlistmaker.common.data.local

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
