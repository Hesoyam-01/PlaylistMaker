package com.example.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.data.db.dao.PlaylistDao
import com.example.playlistmaker.data.db.dao.TrackDao
import com.example.playlistmaker.data.db.dao.TrackFromPlaylistDao
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.data.db.entity.TrackFromPlaylistEntity

@Database(version = 1, entities = [TrackEntity::class, PlaylistEntity::class, TrackFromPlaylistEntity::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao() : TrackDao

    abstract fun playlistDao() : PlaylistDao

    abstract fun trackFromPlaylistDao() : TrackFromPlaylistDao
}