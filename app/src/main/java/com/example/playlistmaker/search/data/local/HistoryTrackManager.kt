package com.example.playlistmaker.search.data.local

import android.content.SharedPreferences
import com.example.playlistmaker.common.data.local.LocalStorageManager
import com.example.playlistmaker.common.data.local.TrackPreferencesDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.LinkedList

class HistoryTrackManager(private val sharedPreferences : SharedPreferences,
                          private val gson: Gson,) : LocalStorageManager<LinkedList<TrackPreferencesDto>> {


    override suspend fun saveDataSuspend(data: LinkedList<TrackPreferencesDto>) : Boolean {
        return withContext(Dispatchers.IO) {
            val json = gson.toJson(data)
            sharedPreferences.edit().putString(HISTORY_LIST_KEY, json).apply()

        true}
    }

    override suspend fun getDataSuspend(): LinkedList<TrackPreferencesDto> {

        return withContext(Dispatchers.IO) {
            try {
                val json = sharedPreferences.getString(HISTORY_LIST_KEY, null)
                val gg = if (json != null) gson.fromJson(json, object : TypeToken<LinkedList<TrackPreferencesDto>>() {}.type) else LinkedList<TrackPreferencesDto>()
                gg
            } catch (e: Throwable) {
                LinkedList<TrackPreferencesDto>()
            }
        }
    }

    private val HISTORY_LIST_KEY = "HISTORY_LIST_KEY"
}
