package com.example.playlistmaker.common.domain.repsitory

import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.common.domain.model.Track
import kotlinx.coroutines.flow.Flow
import java.util.LinkedList

interface DbTrackTableRepository {

  fun notifyDatabaseChanged()
  fun insertTrack(track: Track): Flow<Boolean>
  fun delTrack(track: Track): Flow<Boolean>
  fun loadHistoryFlow(): Flow<ConsumerData<LinkedList<Track>>>
  suspend fun getFavoriteId(trackId: Int): Boolean
  fun getTrackByIdFlow(trackId: Int): Flow<ConsumerData<Track>>

}
