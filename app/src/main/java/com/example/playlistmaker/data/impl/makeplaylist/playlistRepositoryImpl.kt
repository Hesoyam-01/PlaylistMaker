package com.example.playlistmaker.data.impl.makeplaylist

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.converters.PlaylistDbConverter
import com.example.playlistmaker.domain.api.playlist.PlaylistRepository
import com.example.playlistmaker.domain.model.library.Playlist
import java.io.File
import java.io.FileOutputStream

class playlistRepositoryImpl(
    private val context: Context,
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter
) : PlaylistRepository {

    override suspend fun makePlaylist(name: String, description: String?, coverFilePath: String?) {
        val playlist = Playlist(name, description, coverFilePath)
        addToPlaylists(playlist)
    }

    private suspend fun addToPlaylists(playlist: Playlist) {
        appDatabase.playlistDao().insertPlaylist(playlistDbConverter.map(playlist))
    }

    override fun saveImageToPrivateStorage(uri: Uri) : String {
        val filePath =
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlistsCovers")

        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val timestamp = System.currentTimeMillis()
        val fileName = "${timestamp}_cover.jpg"
        val file = File(filePath, fileName)

        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)

        inputStream?.use { input ->
            outputStream.use { output ->
                val bitmap = BitmapFactory.decodeStream(input)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, output)
            }
        }

        return fileName
    }
}