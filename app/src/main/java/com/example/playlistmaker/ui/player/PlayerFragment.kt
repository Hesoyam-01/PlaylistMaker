package com.example.playlistmaker.ui.player

import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.domain.model.library.Playlist
import com.example.playlistmaker.domain.model.player.MediaState
import com.example.playlistmaker.domain.model.search.Track
import com.example.playlistmaker.presentation.player.PlayerState
import com.example.playlistmaker.presentation.player.PlayerViewModel
import com.example.playlistmaker.util.debounce
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment() {

    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(track)
    }

    private lateinit var binding: FragmentPlayerBinding

    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit

    private lateinit var addToPlaylistAdapter: AddToPlaylistAdapter

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private lateinit var track: Track

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

        track = getTrackFromArgs()

        viewModel.observePlayerState().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.fillData()

        viewLifecycleOwner.lifecycleScope.launch {
            delay(MINIMAL_DELAY)
            viewModel.getBottomSheetState()
        }

        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_makePlaylistFragment)
        }

        onPlaylistClickDebounce =
            debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) {
                viewModel.addTrackToPlaylist(it)
            }

        addToPlaylistAdapter = AddToPlaylistAdapter {
            onPlaylistClickDebounce(it)
        }
        binding.playlistsRecyclerView.adapter = addToPlaylistAdapter

        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        binding.overlay.alpha = INITIAL_OVERLAY_ALPHA

        binding.playerToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.playStopButton.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.isFavoriteButton.setOnClickListener {
            viewModel.onFavoriteClicked(track)
        }

        binding.addToPlaylistButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        val displayMetrics = Resources.getSystem().displayMetrics
        val screenHeightPx = displayMetrics.heightPixels

        val topMarginPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            0f,
            resources.displayMetrics
        ).toInt()

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                val params = bottomSheet.layoutParams
                params.height = screenHeightPx - topMarginPx
                bottomSheet.layoutParams = params
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val alpha = (slideOffset + 1) / 2
                binding.overlay.alpha = alpha
            }
        })

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
            if (track.isFavorite) isFavoriteButton.setImageResource(R.drawable.ic_favorite_51)
            else isFavoriteButton.setImageResource(R.drawable.ic_not_favorite_51)
        }

        val coverUrl = track.artworkUrl100
        Glide.with(this)
            .load(coverUrl)
            .transform(RoundedCorners(dpToPx(8)))
            .placeholder(R.drawable.ic_cover_placeholder_312)
            .into(binding.playerTrackCover)

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun render(state: PlayerState) {
        when (state) {
            is PlayerState.Playlists -> showPlaylists(state.list)
            is PlayerState.IsFavorite -> changeIsFavoriteButton(state.isFavorite)
            is PlayerState.Media -> showMedia(state.mediaState, state.elapsedTime)
            is PlayerState.TrackInPlaylistStatus -> showPlaylistCheckFeedback(
                state.isPresent,
                state.playlistName
            )

            is PlayerState.BottomSheet -> bottomSheetBehavior.state = state.bottomSheetState
        }
    }

    private fun showPlaylistCheckFeedback(isPresent: Boolean, playlistName: String) {
        binding.apply {
            if (isPresent) Toast.makeText(
                requireContext(),
                getString(R.string.track_is_present, playlistName),
                Toast.LENGTH_SHORT
            ).show()
            else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                Toast.makeText(
                    requireContext(),
                    getString(R.string.track_added_to_playlist, playlistName),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showPlaylists(playlists: List<Playlist>) {
        addToPlaylistAdapter.updateList(playlists)
    }

    private fun showMedia(mediaState: MediaState, elapsedTime: String) {
        binding.apply {
            if (mediaState == MediaState.PLAYING) playStopButton.setImageResource(R.drawable.ic_stop_84)
            else playStopButton.setImageResource(R.drawable.ic_play_84)
        }
        binding.elapsedTime.text = elapsedTime
    }

    private fun changeIsFavoriteButton(isFavorite: Boolean) {
        binding.apply {
            if (isFavorite) isFavoriteButton.setImageResource(R.drawable.ic_favorite_51)
            else isFavoriteButton.setImageResource(R.drawable.ic_not_favorite_51)
        }
        track.isFavorite = isFavorite
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
        viewModel.saveBottomSheetState(bottomSheetBehavior.state)
    }

    private fun getTrackFromArgs() =
        Track(
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

    companion object {
        private const val INITIAL_OVERLAY_ALPHA: Float = 0F
        private const val CLICK_DEBOUNCE_DELAY = 300L
        private const val MINIMAL_DELAY = 200L

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

}