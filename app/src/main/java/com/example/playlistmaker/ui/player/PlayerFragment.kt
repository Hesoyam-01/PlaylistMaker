package com.example.playlistmaker.ui.player

import android.os.Bundle
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
import com.example.playlistmaker.presentation.player.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment() {
    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(requireArguments().getString(ARGS_PREVIEW_URL))
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

        viewModel.observePlayerState().observe(viewLifecycleOwner) {
            changePlayStopButton(it.isPlaying)
            binding.elapsedTime.text = it.elapsedTime
        }

        binding.playerToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.playStopButton.setOnClickListener {
            viewModel.playbackControl()
        }

        if (requireArguments().getString(ARGS_RELEASE_DATE) == null) binding.yearView.visibility =
            View.GONE
        if (requireArguments().getString(ARGS_ALBUM_NAME) == null) binding.albumView.visibility =
            View.GONE

        binding.apply {
            playerTrackName.text = requireArguments().getString(ARGS_TRACK_NAME)
            playerArtistName.text = requireArguments().getString(ARGS_ARTIST_NAME)
            timeInfo.text = requireArguments().getString(ARGS_TRACK_TIME)
            albumInfo.text = requireArguments().getString(ARGS_ALBUM_NAME)
            genreInfo.text = requireArguments().getString(ARGS_GENRE_NAME)
            yearInfo.text = requireArguments().getString(ARGS_RELEASE_DATE)
            countryInfo.text = requireArguments().getString(ARGS_COUNTRY)
        }

        val coverUrl = requireArguments().getString(ARGS_TRACK_COVER)
        Glide.with(this)
            .load(coverUrl)
            .transform(RoundedCorners(dpToPx(8)))
            .placeholder(R.drawable.album_placeholder)
            .into(binding.playerTrackCover)

    }

    private fun changePlayStopButton(isPlaying: Boolean) {
        binding.apply {
            if (isPlaying) playStopButton.setImageResource(R.drawable.ic_stop_84)
            else playStopButton.setImageResource(R.drawable.ic_play_84)
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
        private const val ARGS_PREVIEW_URL = "preview_url"
        private const val ARGS_TRACK_COVER = "track_cover"
        private const val ARGS_TRACK_NAME = "track_name"
        private const val ARGS_ARTIST_NAME = "artist_name"
        private const val ARGS_TRACK_TIME = "track_time"
        private const val ARGS_ALBUM_NAME = "album_name"
        private const val ARGS_GENRE_NAME = "genre_name"
        private const val ARGS_RELEASE_DATE = "release_date"
        private const val ARGS_COUNTRY = "country"

        fun createArgs(
            previewUrl: String,
            trackCover: String,
            trackName: String,
            artistName: String,
            trackTime: String,
            albumName: String?,
            genreName: String,
            releaseDate: String?,
            country: String
        ): Bundle =
            bundleOf(
                ARGS_PREVIEW_URL to previewUrl,
                ARGS_TRACK_COVER to trackCover,
                ARGS_TRACK_NAME to trackName,
                ARGS_ARTIST_NAME to artistName,
                ARGS_TRACK_TIME to trackTime,
                ARGS_ALBUM_NAME to albumName,
                ARGS_GENRE_NAME to genreName,
                ARGS_RELEASE_DATE to releaseDate,
                ARGS_COUNTRY to country
            )
    }
}