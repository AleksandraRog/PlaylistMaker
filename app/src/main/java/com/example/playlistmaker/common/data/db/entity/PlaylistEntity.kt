package com.example.playlistmaker.common.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
class PlaylistEntity(
    val playlistName: String,
    val fileUrl: String,
    val description: String,
    @PrimaryKey(autoGenerate = true)
    val playlistId: Int = 0,
    val systemTime: Long,
    var playlistSize: Int,
    )
