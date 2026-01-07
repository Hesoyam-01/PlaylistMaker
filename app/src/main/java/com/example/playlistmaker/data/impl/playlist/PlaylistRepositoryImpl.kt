package com.example.playlistmaker.data.impl.playlist

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.converters.PlaylistDbConverter
import com.example.playlistmaker.data.db.converters.TrackFromPlaylistDbConverter
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.TrackFromPlaylistEntity
import com.example.playlistmaker.domain.api.playlist.PlaylistRepository
import com.example.playlistmaker.domain.model.library.Playlist
import com.example.playlistmaker.domain.model.search.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class PlaylistRepositoryImpl(
    private val context: Context,
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
    private val trackFromPlaylistDbConverter: TrackFromPlaylistDbConverter
) : PlaylistRepository {

    override suspend fun makePlaylist(name: String, description: String?, coverFilePath: String?) {
        val playlist = Playlist(
            playlistName = name,
            playlistDescription = description,
            coverFilePath = coverFilePath
        )
        addToPlaylists(playlist)
    }

    private suspend fun addToPlaylists(playlist: Playlist) {
        appDatabase.playlistDao().insertPlaylist(playlistDbConverter.map(playlist))
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return appDatabase.playlistDao().getPlaylists().map { convertFromPlaylistEntity(it) }
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlistDbConverter.map(it) }
    }

    override suspend fun addTrackToPlaylist(playlistId: Int, newTrackId: Int) {
        val trackIdList = playlistDbConverter.mapIdList(getTrackIdList(playlistId))
        val newTrackIdList = playlistDbConverter.mapIdList(trackIdList + newTrackId)
        appDatabase.playlistDao().addTrackToPlaylist(playlistId, newTrackIdList)
    }

    private suspend fun getTrackIdList(playlistId: Int): String? {
        return appDatabase.playlistDao().getTrackIdList(playlistId)
    }

    override suspend fun saveImageAndGetPath(uri: Uri): String {
        val filePath =
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlistsCovers")

        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val timestamp = System.currentTimeMillis()
        val fileName = "${timestamp}_cover.jpg"
        val file = File(filePath, fileName)

        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = withContext(Dispatchers.IO) {
            FileOutputStream(file)
        }

        inputStream?.use { input ->
            outputStream.use { output ->
                val bitmap = BitmapFactory.decodeStream(input)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, output)
            }
        }

        return fileName
    }

    override suspend fun saveTrackFromPlaylist(track: Track) {
        appDatabase.trackFromPlaylistDao().insertTrack(trackFromPlaylistDbConverter.map(track))
    }

    override suspend fun getTracksByIds(ids: List<Int>): List<Track> {
        val tracks = appDatabase.trackFromPlaylistDao().getTracksByIds(ids)
        return convertFromTrackFromPlaylistEntity(tracks)
    }

    private fun convertFromTrackFromPlaylistEntity(tracks: List<TrackFromPlaylistEntity>): List<Track> {
        return tracks.map { trackFromPlaylistDbConverter.map(it) }
    }
}