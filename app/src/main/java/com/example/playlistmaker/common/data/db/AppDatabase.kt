package com.example.playlistmaker.common.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.playlistmaker.common.data.db.dao.TrackDao
import com.example.playlistmaker.common.data.db.entity.TrackEntity
import com.example.playlistmaker.common.data.mapper.DateConverter

@Database(version = 2, entities = [TrackEntity::class])
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase(){

    abstract fun trackDao(): TrackDao

}
