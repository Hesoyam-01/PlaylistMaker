package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.TrackFromPlaylistEntity

@Dao
interface TrackFromPlaylistDao {

    @Insert(entity = TrackFromPlaylistEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: TrackFromPlaylistEntity)

    @Query("SELECT * FROM track_from_playlist_table WHERE trackId IN (:ids)")
    suspend fun getTracksByIds(ids: List<Int>): List<TrackFromPlaylistEntity>

    @Query("DELETE FROM track_from_playlist_table WHERE trackId = :trackId")
    suspend fun deleteTrackById(trackId: Int)

}