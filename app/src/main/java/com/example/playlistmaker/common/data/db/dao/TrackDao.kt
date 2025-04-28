package com.example.playlistmaker.common.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.playlistmaker.common.data.db.entity.ContentPlaylistEntity
import com.example.playlistmaker.common.data.db.entity.PlaylistEntity
import com.example.playlistmaker.common.data.db.entity.TrackEntity

@Dao
interface TrackDao {

    // track_table
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Delete(entity = TrackEntity::class)
    suspend fun deleteTrack(track: TrackEntity)

    @Query("SELECT * FROM track_table WHERE trackId = :trackId")
    suspend fun getTrack(trackId: Int): TrackEntity

    @Query("SELECT * FROM track_table WHERE isFavorite = 1 ORDER BY systemTime DESC")
    suspend fun getFavoriteTracks(): List<TrackEntity>

    @Query("SELECT COUNT(*) FROM track_table WHERE trackId = :trackId and isFavorite = 1")
    suspend fun getCountFavoriteTrackById(trackId: Int): Int

    @Query("DELETE FROM track_table WHERE trackId IN (:ids) AND isFavorite = 0")
    suspend fun deleteTracksIfNotFavorite(ids: List<Int>)

     // playlist_table
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Delete(entity = PlaylistEntity::class)
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlist_table ORDER BY systemTime DESC")
    suspend fun getPlaylists(): List<PlaylistEntity>

    @Query("SELECT * FROM playlist_table WHERE playlistId = :id")
    suspend fun getPlaylist(id: Int): PlaylistEntity

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    //content_playlist_table
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPairTrackPlaylist(pairTrackPlaylist: ContentPlaylistEntity)

    @Query("DELETE FROM content_playlist_table")
    suspend fun deleteContent()

    @Query("SELECT COUNT(*) FROM content_playlist_table WHERE playlistId = :playlistId")
    suspend fun getCountTracksInPlaylist (playlistId: Int): Int

    @Query("SELECT trackId FROM content_playlist_table WHERE playlistId = :playlistId")
    suspend fun getTracksIdInPlaylist (playlistId: Int): List<Int>

    @Query("SELECT COUNT(*) FROM content_playlist_table WHERE trackId = :trackId")
    suspend fun getCountTrackByIdInPlaylists(trackId: Int) : Int

    @Query("DELETE FROM content_playlist_table WHERE keyId = :keyId")
    suspend fun deleteContentKey(keyId: String)


    //complex query
    @Query("SELECT * FROM playlist_table WHERE playlistId NOT IN (SELECT playlistId FROM content_playlist_table WHERE trackId = :trackId)")
    suspend fun getPlaylistsNotContainingTrack(trackId: Int): List<PlaylistEntity>

    @Query("SELECT * FROM playlist_table WHERE playlistId IN (SELECT playlistId FROM content_playlist_table WHERE trackId = :trackId)")
    suspend fun getPlaylistsContainingTrack(trackId: Int): List<PlaylistEntity>

    @Query("""
    SELECT track_table.* FROM track_table
    INNER JOIN content_playlist_table ON track_table.trackId = content_playlist_table.trackId
    WHERE content_playlist_table.playlistId = :playlistId
    ORDER BY content_playlist_table.systemTime DESC """)
    suspend fun getTrackListInPlaylist(playlistId: Int): List<TrackEntity>

    //Transaction
    @Transaction
    suspend fun insertTrackAndPlaylist(pairTrackPlaylist: ContentPlaylistEntity,
        track: TrackEntity, playlist: PlaylistEntity,
    ) {
        insertTrack(track)
        insertPairTrackPlaylist(pairTrackPlaylist)
        playlist.apply { playlistSize = getCountTracksInPlaylist(pairTrackPlaylist.playlistId) }
        updatePlaylist(playlist)
    }

    @Transaction
    suspend fun deletePlaylistAndTracks(playlist: PlaylistEntity,) {
        deleteTracksIfNotFavorite(
            getTracksIdInPlaylist(playlist.playlistId)
        )
        deletePlaylist(playlist)
    }

    @Transaction
    suspend fun deleteTrackFromPlaylist(trackEntity: TrackEntity, playlist: PlaylistEntity) {

        if( (getCountTrackByIdInPlaylists(trackEntity.trackId) == 1)
            && (trackEntity.isFavorite != true)) {
            deleteTrack(trackEntity)
        }
        deleteContentKey("${playlist.playlistId}#${trackEntity.trackId}")
        playlist.apply { playlistSize = getCountTracksInPlaylist(playlist.playlistId) }
        updatePlaylist(playlist)
    }
}
