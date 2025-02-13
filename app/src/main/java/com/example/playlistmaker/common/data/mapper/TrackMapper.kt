package com.example.playlistmaker.common.data.mapper

import com.example.playlistmaker.common.data.dto.TrackDto
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.domain.model.TrackTimePeriod
import java.util.LinkedList

object TrackMapper {

    private fun Track.toDto(): TrackDto {
        return  TrackDto(
            artistName = this.artistName,
            trackName = this.trackName,
            artworkUrl100 = this.artworkUrl100,
            previewUrl = this.previewUrl,
            releaseDate = this.releaseDate,
            collectionName = this.collectionName,
            primaryGenreName = this.primaryGenreName,
            country = this.country,
            trackId = this.trackId,
            trackTimeMillis = this.trackTime!!.toMillis()
        )
    }

    private const val placeholderString = "No Reply"

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
            trackTime = TrackTimePeriod.fromMillis(this.trackTimeMillis)
        )
    }

    fun mapToDto(tracks: List<Track>): List<TrackDto> {
        return tracks.map { it.toDto() }
    }

    fun mapToDto(tracks: LinkedList<Track>): LinkedList<TrackDto> {
        return tracks.mapTo(LinkedList()) { it.toDto() }
    }

    fun mapToDomainModel(tracksDto: List<TrackDto>): List<Track> {
        return tracksDto.map { it.toDomainModel() }
    }

    fun mapToDomainModel(tracksDto: LinkedList<TrackDto>): LinkedList<Track> {
        return tracksDto.mapTo(LinkedList<Track>()) { it.toDomainModel() }
    }
}
