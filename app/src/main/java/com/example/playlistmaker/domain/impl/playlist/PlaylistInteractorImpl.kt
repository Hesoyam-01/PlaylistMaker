package com.example.playlistmaker.domain.impl.playlist

import android.net.Uri
import com.example.playlistmaker.domain.api.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.api.playlist.PlaylistRepository
import com.example.playlistmaker.domain.model.library.Playlist
import com.example.playlistmaker.domain.model.search.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val repository: PlaylistRepository) : PlaylistInteractor {

    override suspend fun makePlaylist(name: String, description: String?, coverFilePath: String?) {
        repository.makePlaylist(name, description, coverFilePath)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override fun getPlaylistById(playlistId: Int): Flow<Playlist> {
        return repository.getPlaylistById(playlistId)
    }

    override suspend fun addTrackToPlaylistById(playlistId: Int, newTrackId: Int) {
        repository.addTrackToPlaylistById(playlistId, newTrackId)
    }

    override suspend fun saveImageAndGetPath(uri: Uri) : String {
        return repository.saveImageAndGetPath(uri)
    }

    override suspend fun saveTrackFromPlaylist(track: Track) {
        repository.saveTrackFromPlaylist(track)
    }

    override suspend fun getTracksByIds(trackIdList: List<Int>): List<Track> {
        return repository.getTracksByIds(trackIdList)
    }

    override suspend fun deleteTracksFromPlaylistByIds(playlistId: Int, trackIdList: List<Int>) {
        repository.deleteTracksFromPlaylistByIds(playlistId, trackIdList)
    }

    override suspend fun deletePlaylistById(playlistId: Int) {
        repository.deletePlaylistById(playlistId)
    }

}