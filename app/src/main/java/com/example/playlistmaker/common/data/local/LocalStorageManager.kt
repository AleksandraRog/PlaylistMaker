package com.example.playlistmaker.common.data.local

interface LocalStorageManager<T> {
    
    fun getData(): T

    fun saveData(data: T)
}

const val PLAYLISTMAKER_PREFERENCES = "PLAYLISTMAKER_PREFERENCES"
