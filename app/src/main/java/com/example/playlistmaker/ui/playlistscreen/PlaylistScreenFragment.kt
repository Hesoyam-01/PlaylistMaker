package com.example.playlistmaker.ui.playlistscreen

import android.content.res.Resources
import android.os.Bundle
import android.os.Environment
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistScreenBinding
import com.example.playlistmaker.domain.model.search.Track
import com.example.playlistmaker.presentation.playlistscreen.PlaylistScreenState
import com.example.playlistmaker.presentation.playlistscreen.PlaylistScreenViewModel
import com.example.playlistmaker.ui.player.PlayerFragment
import com.example.playlistmaker.ui.search.TrackAdapter
import com.example.playlistmaker.util.debounce
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class PlaylistScreenFragment : Fragment() {
    private val viewModel: PlaylistScreenViewModel by viewModel()

    private var _binding: FragmentPlaylistScreenBinding? = null
    private val binding get() = _binding!!

    private lateinit var trackBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var moreBottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private lateinit var deleteDialog: MaterialAlertDialogBuilder

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private lateinit var tracksInPlaylistAdapter: TrackAdapter

    private var selectedTrackId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlistId = requireArguments().getInt(ARGS_PLAYLIST_ID)

        viewModel.observePlaylistScreenState().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.fillData(playlistId)

        binding.overlay.alpha = INITIAL_OVERLAY_ALPHA

        binding.playlistToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        tracksInPlaylistAdapter = TrackAdapter(
            onTrackClick = {
                onTrackClickDebounce(it)
            },
            onTrackLongClick = {
                selectedTrackId = it
                deleteDialog.show()
            }
        )

        binding.tracksInPlaylistRecyclerView.adapter = tracksInPlaylistAdapter

        onTrackClickDebounce =
            debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) {
                navigateToPlayerFragment(it)
            }

        deleteDialog = MaterialAlertDialogBuilder(
            requireContext(),
            R.style.MyAlertDialogStyle
        )
            .setTitle(R.string.delete_track_question)
            .setNegativeButton(R.string.no) { _, _ -> }
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.deleteTrackFromPlaylistById(playlistId, selectedTrackId!!)
            }

        val displayMetrics = Resources.getSystem().displayMetrics
        val screenHeightPx = displayMetrics.heightPixels

        val topMarginPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            0f,
            resources.displayMetrics
        ).toInt()

        trackBottomSheetBehavior = BottomSheetBehavior.from(binding.tracksBottomSheet)

        trackBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                val params = bottomSheet.layoutParams
                params.height = screenHeightPx - topMarginPx
                bottomSheet.layoutParams = params
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        moreBottomSheetBehavior = BottomSheetBehavior.from(binding.moreBottomSheet)
        moreBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        binding.moreButton.setOnClickListener {
            moreBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        moreBottomSheetBehavior.addBottomSheetCallback(object :
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

        binding.bottomSheetDeletePlaylistButton.setOnClickListener {
            viewModel.deletePlaylistById(playlistId)
            findNavController().navigateUp()
        }

    }

    private fun render(state: PlaylistScreenState) {
        when (state) {
            is PlaylistScreenState.Content -> showContent(
                state.playlistName,
                state.playlistDescription,
                state.coverFilePath,
                state.trackCount,
                state.tracks,
                state.totalTime
            )

            PlaylistScreenState.Empty -> showEmpty()
        }
    }

    private fun showContent(
        playlistNameArg: String,
        playlistDescriptionArg: String?,
        coverFilePath: String?,
        trackCountArg: Int,
        tracks: List<Track>,
        totalTimeArg: Int
    ) {
        val filePath = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "playlistsCovers"
        )
        val file =
            if (coverFilePath != null) File(filePath, coverFilePath) else null

        binding.apply {
            tracksInPlaylistRecyclerView.visibility = View.VISIBLE
            emptyPlaylistPlaceholder.visibility = View.GONE

            Glide.with(root)
                .load(file)
                .placeholder(R.drawable.ic_cover_placeholder_312)
                .centerCrop()
                .into(playlistCover)
            playlistName.text = playlistNameArg
            playlistDescription.text = playlistDescriptionArg
            trackCount.text = root.resources.getQuantityString(
                R.plurals.track_count,
                trackCountArg,
                trackCountArg
            )
            totalTime.text = root.resources.getQuantityString(
                R.plurals.minutes,
                totalTimeArg,
                totalTimeArg
            )

            Glide.with(root)
                .load(file)
                .placeholder(R.drawable.ic_cover_placeholder_45)
                .centerCrop()
                .into(bottomSheetPlaylistCover)
            bottomSheetPlaylistName.text = playlistNameArg
            bottomSheetTrackCount.text = root.resources.getQuantityString(
                R.plurals.track_count,
                trackCountArg,
                trackCountArg
            )
        }

        tracksInPlaylistAdapter.updateList(tracks)
    }

    private fun showEmpty() {
        binding.apply {
            tracksInPlaylistRecyclerView.visibility = View.GONE
            emptyPlaylistPlaceholder.visibility = View.VISIBLE
        }
    }

    private fun navigateToPlayerFragment(track: Track) {
        findNavController().navigate(
            R.id.action_playlistScreenFragment_to_playerFragment,
            PlayerFragment.createArgs(
                trackId = track.trackId,
                previewUrl = track.previewUrl,
                trackCover = track.artworkUrl100,
                trackName = track.trackName,
                artistName = track.artistName,
                trackTime = track.trackTime,
                albumName = track.collectionName,
                genreName = track.primaryGenreName,
                releaseDate = track.releaseDate,
                country = track.country
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 300L
        private const val INITIAL_OVERLAY_ALPHA: Float = 0F

        private const val ARGS_PLAYLIST_ID = "playlist_id"

        fun createArgs(
            playlistId: Int,
        ): Bundle =
            bundleOf(
                ARGS_PLAYLIST_ID to playlistId,
            )

    }
}