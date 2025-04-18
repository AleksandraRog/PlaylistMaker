package com.example.playlistmaker.common.data.mapper

import com.example.playlistmaker.common.data.db.entity.PlaylistEntity
import com.example.playlistmaker.common.domain.model.Playlist
import java.util.LinkedList

object PlaylistDbConvertor {

    fun Playlist.toEntity(): PlaylistEntity {
        return PlaylistEntity(
            playlistName = this.playlistName,
            fileUrl = this.fileUrl,
            description = this.description,
            playlistSize = this.playlistSize,
            playlistId = this.playlistId,
            systemTime = System.currentTimeMillis(),
        )
    }

    fun PlaylistEntity.toDomainModel(): Playlist {
        return Playlist(this.playlistName, this.fileUrl, this.description, this.playlistId, this.playlistSize)
    }

    fun mapEntityToDomainModel(playlistsEntity: LinkedList<PlaylistEntity>): LinkedList<Playlist> {
        return playlistsEntity.mapTo(LinkedList<Playlist>()) { it.toDomainModel() }
    }
}
