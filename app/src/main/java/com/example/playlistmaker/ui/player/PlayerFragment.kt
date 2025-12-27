package com.example.playlistmaker.ui.player

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.domain.models.player.MediaState
import com.example.playlistmaker.domain.models.search.Track
import com.example.playlistmaker.presentation.player.PlayerState
import com.example.playlistmaker.presentation.player.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment() {

    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(requireArguments().getString(ARGS_PREVIEW_URL),
            requireArguments().getInt(ARGS_TRACK_ID))
    }

    private lateinit var binding: FragmentPlayerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val track = getTrackFromArgs()

        viewModel.observePlayerState().observe(viewLifecycleOwner) {
            when (it) {
                is PlayerState.Media -> {
                    changePlayStopButton(it.mediaState == MediaState.PLAYING)
                    binding.elapsedTime.text = it.elapsedTime
                }

                is PlayerState.IsFavorite -> {
                    changeIsFavoriteButton(it.isFavorite)
                    Log.d("state", "${track.isFavorite}")
                    track.isFavorite = it.isFavorite
                }
            }
        }

        binding.playerToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.playStopButton.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.isFavoriteButton.setOnClickListener {
            viewModel.onFavoriteClicked(track)
        }

        if (track.releaseDate == null) binding.yearView.visibility =
            View.GONE
        if (track.collectionName == null) binding.albumView.visibility =
            View.GONE

        binding.apply {
            playerTrackName.text = track.trackName
            playerArtistName.text = track.artistName
            timeInfo.text = track.trackTime
            albumInfo.text = track.collectionName
            genreInfo.text = track.primaryGenreName
            yearInfo.text = track.releaseDate
            countryInfo.text = track.country
            Log.d("creating", "${track.isFavorite}")
            if (track.isFavorite) isFavoriteButton.setImageResource(R.drawable.ic_favorite_51)
            else isFavoriteButton.setImageResource(R.drawable.ic_not_favorite_51)
        }

        val coverUrl = track.artworkUrl100
        Glide.with(this)
            .load(coverUrl)
            .transform(RoundedCorners(dpToPx(8)))
            .placeholder(R.drawable.ic_album_placeholder_45)
            .into(binding.playerTrackCover)

    }

    private fun changePlayStopButton(isPlaying: Boolean) {
        binding.apply {
            if (isPlaying) playStopButton.setImageResource(R.drawable.ic_stop_84)
            else playStopButton.setImageResource(R.drawable.ic_play_84)
        }
    }

    private fun changeIsFavoriteButton(isFavorite: Boolean) {
        binding.apply {
            if (isFavorite) isFavoriteButton.setImageResource(R.drawable.ic_favorite_51)
            else isFavoriteButton.setImageResource(R.drawable.ic_not_favorite_51)
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    companion object {
        private const val ARGS_TRACK_ID = "track_id"
        private const val ARGS_PREVIEW_URL = "preview_url"
        private const val ARGS_TRACK_COVER = "track_cover"
        private const val ARGS_TRACK_NAME = "track_name"
        private const val ARGS_ARTIST_NAME = "artist_name"
        private const val ARGS_TRACK_TIME = "track_time"
        private const val ARGS_ALBUM_NAME = "album_name"
        private const val ARGS_GENRE_NAME = "genre_name"
        private const val ARGS_RELEASE_DATE = "release_date"
        private const val ARGS_COUNTRY = "country"
        private const val ARGS_IS_FAVORITE = "favorite"

        fun createArgs(
            trackId: Int,
            previewUrl: String,
            trackCover: String,
            trackName: String,
            artistName: String,
            trackTime: String,
            albumName: String?,
            genreName: String,
            releaseDate: String?,
            country: String,
            isFavorite: Boolean,
        ): Bundle =
            bundleOf(
                ARGS_TRACK_ID to trackId,
                ARGS_PREVIEW_URL to previewUrl,
                ARGS_TRACK_COVER to trackCover,
                ARGS_TRACK_NAME to trackName,
                ARGS_ARTIST_NAME to artistName,
                ARGS_TRACK_TIME to trackTime,
                ARGS_ALBUM_NAME to albumName,
                ARGS_GENRE_NAME to genreName,
                ARGS_RELEASE_DATE to releaseDate,
                ARGS_COUNTRY to country,
                ARGS_IS_FAVORITE to isFavorite,
            )
    }

    private fun getTrackFromArgs(): Track {
        return Track(
            trackId = requireArguments().getInt(ARGS_TRACK_ID),
            trackName = requireArguments().getString(ARGS_TRACK_NAME) ?: "",
            artistName = requireArguments().getString(ARGS_ARTIST_NAME) ?: "",
            trackTime = requireArguments().getString(ARGS_TRACK_TIME) ?: "",
            artworkUrl100 = requireArguments().getString(ARGS_TRACK_COVER) ?: "",
            collectionName = requireArguments().getString(ARGS_ALBUM_NAME),
            releaseDate = requireArguments().getString(ARGS_RELEASE_DATE),
            primaryGenreName = requireArguments().getString(ARGS_GENRE_NAME) ?: "",
            country = requireArguments().getString(ARGS_COUNTRY) ?: "",
            previewUrl = requireArguments().getString(ARGS_PREVIEW_URL) ?: "",
            isFavorite = requireArguments().getBoolean(ARGS_IS_FAVORITE)
        )
    }

}