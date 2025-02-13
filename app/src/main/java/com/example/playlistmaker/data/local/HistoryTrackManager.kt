package com.example.playlistmaker.data.local

import android.content.SharedPreferences
import com.example.playlistmaker.data.dto.TrackDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.koin.java.KoinJavaComponent.getKoin
import java.util.LinkedList

class HistoryTrackManager : LocalStorageManager<LinkedList<TrackDto>> {

    private val sharedPreferences: SharedPreferences by lazy {
        getKoin().get<SharedPreferences>()
    }
    private val gson: Gson by lazy { getKoin().get<Gson>() }

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
