package com.example.playlistmaker.common.data.mapper

import com.example.playlistmaker.common.data.db.entity.TrackEntity
import com.example.playlistmaker.common.data.dto.TrackDto
import com.example.playlistmaker.common.data.local.TrackPreferencesDto
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.domain.model.TrackTimePeriod
import java.util.LinkedList

object TrackMapper {

    private fun Track.toPreferencesDto(): TrackPreferencesDto {
        return  TrackPreferencesDto(
            artistName = this.artistName,
            trackName = this.trackName,
            artworkUrl100 = this.artworkUrl100,
            previewUrl = this.previewUrl,
            releaseDate = this.releaseDate,
            collectionName = this.collectionName,
            primaryGenreName = this.primaryGenreName,
            country = this.country,
            trackId = this.trackId,
            isFavorite = this.isFavorite,
            trackTimeMillis = this.trackTime!!.toMillis()!!
        )
    }

    private const val placeholderString = "No Reply"

    private fun TrackDto.toTrackEntity(): TrackEntity {
        return  TrackEntity(
            artistName = this.artistName ?: placeholderString,
            trackName = this.trackName ?: placeholderString,
            artworkUrl100 = this.artworkUrl100 ?: "",
            previewUrl = this.previewUrl ?: "",
            releaseDate = this.releaseDate,
            collectionName = this.collectionName ?: placeholderString,
            primaryGenreName = this.primaryGenreName ?: placeholderString,
            country = this.country ?: placeholderString,
            trackId = this.trackId!!,
            trackTimeMillis = this.trackTimeMillis ?: 0L,
            systemTime = System.currentTimeMillis(),
            isFavorite = null,
        )
    }

   fun TrackEntity.toDomainModel(): Track {
        return Track(
            artistName = this.artistName,
            trackName = this.trackName,
            artworkUrl100 = this.artworkUrl100,
            previewUrl = this.previewUrl,
            releaseDate = this.releaseDate,
            collectionName = this.collectionName,
            primaryGenreName = this.primaryGenreName,
            country = this.country,
            trackId = this.trackId,
            isFavorite = this.isFavorite ?: false,
            trackTime = TrackTimePeriod.fromMillis(this.trackTimeMillis)
        )
    }

    fun Track.toTrackEntity(): TrackEntity {
        return TrackEntity(
            artistName = this.artistName,
            trackName = this.trackName,
            artworkUrl100 = this.artworkUrl100,
            previewUrl = this.previewUrl,
            releaseDate = this.releaseDate,
            collectionName = this.collectionName,
            primaryGenreName = this.primaryGenreName,
            country = this.country,
            trackId = this.trackId,
            trackTimeMillis = this.trackTime!!.toMillis()!!,
            systemTime = System.currentTimeMillis(),
            isFavorite = this.isFavorite,
        )
    }

    private fun TrackDto.toDomainModel(): Track {
        return Track(
            artistName = this.artistName ?: placeholderString,
            trackName = this.trackName ?: placeholderString,
            artworkUrl100 = this.artworkUrl100 ?: "",
            previewUrl = this.previewUrl ?: "",
            releaseDate = this.releaseDate,
            collectionName = this.collectionName ?: placeholderString,
            primaryGenreName = this.primaryGenreName ?: placeholderString,
            country = this.country ?: placeholderString,
            trackId = this.trackId!!,
            trackTime = TrackTimePeriod.fromMillis(this.trackTimeMillis),
        )
    }

    private fun TrackPreferencesDto.toDomainModel(): Track {
        return Track(
            artistName = this.artistName,
            trackName = this.trackName,
            artworkUrl100 = this.artworkUrl100,
            previewUrl = this.previewUrl,
            releaseDate = this.releaseDate,
            collectionName = this.collectionName,
            primaryGenreName = this.primaryGenreName,
            country = this.country,
            trackId = this.trackId,
            isFavorite = this.isFavorite,
            trackTime = TrackTimePeriod.fromMillis(this.trackTimeMillis)
        )
    }

    fun mapToPreferencesDto(tracks: LinkedList<Track>): LinkedList<TrackPreferencesDto> {
        return tracks.mapTo(LinkedList()) { it.toPreferencesDto() }
    }

    fun mapToDomainModel(tracksDto: List<TrackDto>): List<Track> {
        return tracksDto.map { it.toDomainModel() }
    }

    fun mapToDomainModel(tracksPreferencesDto: LinkedList<TrackPreferencesDto>): LinkedList<Track> {
        return tracksPreferencesDto.mapTo(LinkedList<Track>()) { it.toDomainModel() }
    }

    fun mapEntityToDomainModel(tracksEntity: LinkedList<TrackEntity>): LinkedList<Track> {
        return tracksEntity.mapTo(LinkedList<Track>()) { it.toDomainModel() }
    }
}
