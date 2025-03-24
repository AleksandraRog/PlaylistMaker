package com.example.playlistmaker.common.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.common.data.db.entity.TrackEntity

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Query("SELECT * FROM track_table ORDER BY systemTime DESC")
    suspend fun getTracks(): List<TrackEntity>

    @Query("SELECT trackId FROM track_table WHERE trackId = :trackIdf")
    suspend fun getTrackId(trackIdf : Int): List<Int>

    @Query("SELECT * FROM track_table WHERE trackId = :trackIdf")
    suspend fun getTrack(trackIdf : Int): TrackEntity

    @Delete(entity = TrackEntity::class)
    suspend fun deleteTrack(track: TrackEntity)
}
