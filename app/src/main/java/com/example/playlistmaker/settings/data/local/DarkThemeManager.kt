package com.example.playlistmaker.settings.data.local

import android.content.SharedPreferences
import com.example.playlistmaker.common.data.local.LocalStorageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DarkThemeManager(val sharedPreferences: SharedPreferences) : LocalStorageManager<Boolean?> {


    override suspend fun saveDataSuspend(data: Boolean?) : Boolean {
        return if (data != null) {
            withContext(Dispatchers.IO) {
                sharedPreferences.edit().putBoolean(DARK_THEME_KEY, data).apply()
            true}
        } else false
    }

    override suspend fun getDataSuspend(): Boolean? {
        return if (sharedPreferences.contains(DARK_THEME_KEY)) {
            sharedPreferences.getBoolean(DARK_THEME_KEY, false)
        } else {
            null
        }
    }



    private val DARK_THEME_KEY = "DARK_THEME_KEY"
}
