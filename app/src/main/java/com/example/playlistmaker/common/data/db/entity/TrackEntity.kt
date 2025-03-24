package com.example.playlistmaker.common.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "track_table")
class TrackEntity(
    val trackName: String,
    val artistName: String,
    val artworkUrl100: String,
    val previewUrl: String,
    val releaseDate: Date?,
    val collectionName: String,
    val primaryGenreName: String,
    val country: String,
    @PrimaryKey
    val trackId: Int,
    val systemTime: Long,
    val trackTimeMillis: Long,
    )