package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlist_table ORDER BY playlistId DESC")
    fun getPlaylists() : Flow<List<PlaylistEntity>>

    @Query("SELECT trackIdList FROM playlist_table WHERE playlistId = :playlistId")
    suspend fun getTrackIdList(playlistId: Int): String?

    @Query("UPDATE playlist_table SET trackIdList = :newTrackIdList, trackCount = trackCount + 1 WHERE playlistId = :playlistId")
    suspend fun addTrackToPlaylist(playlistId: Int, newTrackIdList: String)

}