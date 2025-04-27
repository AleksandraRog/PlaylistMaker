package com.example.playlistmaker.common.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "content_playlist_table",
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["playlistId"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE
        )
     ],
    indices = [
        Index(value = ["playlistId"])
    ]
)
class ContentPlaylistEntity(
    val playlistId: Int,
    val trackId: Int,
    @PrimaryKey
    val keyId: String
) {
    @Ignore
    constructor(playlistId: Int, trackId: Int) : this(
        playlistId = playlistId,
        trackId = trackId,
        keyId = "${playlistId}#${trackId}"
    )
}