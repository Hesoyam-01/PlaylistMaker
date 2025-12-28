package com.example.playlistmaker.data.impl.makeplaylist

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.converters.PlaylistDbConverter
import com.example.playlistmaker.domain.api.makeplaylist.MakePlaylistRepository
import com.example.playlistmaker.domain.models.library.Playlist
import java.io.File
import java.io.FileOutputStream

class MakePlaylistRepositoryImpl(
    private val context: Context,
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter
) : MakePlaylistRepository {

    override suspend fun addToPlaylists(playlist: Playlist) {
        appDatabase.playlistDao().insertPlaylist(playlistDbConverter.map(playlist))
    }

    override fun saveImageToPrivateStorage(uri: Uri) {
        val filePath =
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlistsCovers")

        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val timestamp = System.currentTimeMillis()
        val file = File(filePath, "${timestamp}_cover.jpg")

        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)

        inputStream?.use { input ->
            outputStream.use { output ->
                val bitmap = BitmapFactory.decodeStream(input)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, output)
            }
        }
    }
}