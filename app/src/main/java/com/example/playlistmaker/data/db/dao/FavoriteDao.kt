package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.FavoriteEntity

@Dao
interface FavoriteDao {

    @Insert(entity = FavoriteEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: FavoriteEntity)

    @Delete(entity = FavoriteEntity::class)
    suspend fun deleteTrack(track: FavoriteEntity)

    @Query("SELECT * FROM track_table ORDER BY addedAt DESC")
    suspend fun getTracks() : List<FavoriteEntity>

    @Query("SELECT trackId FROM track_table")
    suspend fun getTrackIds() : List<Int>

}