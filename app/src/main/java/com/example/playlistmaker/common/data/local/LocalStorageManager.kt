package com.example.playlistmaker.common.data.local

interface LocalStorageManager<T> {
    
    fun getData(): T

    suspend fun saveDataSuspend(data: T)

    suspend fun getDataSuspend(): T
}

const val PLAYLISTMAKER_PREFERENCES = "PLAYLISTMAKER_PREFERENCES"
