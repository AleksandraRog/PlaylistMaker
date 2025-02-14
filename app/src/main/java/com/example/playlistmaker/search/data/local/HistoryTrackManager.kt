package com.example.playlistmaker.search.data.local

import android.content.SharedPreferences
import com.example.playlistmaker.common.data.dto.TrackDto
import com.example.playlistmaker.common.data.local.LocalStorageManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.LinkedList

class HistoryTrackManager(private val sharedPreferences : SharedPreferences,
                          private val gson: Gson,) : LocalStorageManager<LinkedList<TrackDto>> {

    override fun getData(): LinkedList<TrackDto> {
        val json = sharedPreferences.getString(HISTORY_LIST_KEY, null)
        return if (json != null) gson.fromJson(json, object : TypeToken<LinkedList<TrackDto>>() {}.type) else LinkedList<TrackDto>()
    }

    override fun saveData(data: LinkedList<TrackDto>) {
        val json = gson.toJson(data)
        sharedPreferences.edit().putString(HISTORY_LIST_KEY, json).apply()
    }

    private val HISTORY_LIST_KEY = "HISTORY_LIST_KEY"
}
