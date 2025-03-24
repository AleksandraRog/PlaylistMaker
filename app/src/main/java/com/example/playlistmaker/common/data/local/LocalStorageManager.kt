package com.example.playlistmaker.common.data.local

interface LocalStorageManager<T> {

    suspend fun saveDataSuspend(data: T) : Boolean

    suspend fun getDataSuspend(): T
}

const val PLAYLISTMAKER_PREFERENCES = "PLAYLISTMAKER_PREFERENCES"
