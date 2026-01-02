package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.playlistmaker.data.db.entity.TrackFromPlaylistEntity

@Dao
interface TrackFromPlaylistDao {

    @Insert(entity = TrackFromPlaylistEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: TrackFromPlaylistEntity)

}