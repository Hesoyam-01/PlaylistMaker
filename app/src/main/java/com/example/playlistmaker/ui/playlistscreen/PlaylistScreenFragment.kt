package com.example.playlistmaker.ui.playlistscreen

import android.content.res.Resources
import android.os.Bundle
import android.os.Environment
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistScreenBinding
import com.example.playlistmaker.domain.model.library.Playlist
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.io.File

class PlaylistScreenFragment : Fragment() {

    private lateinit var binding: FragmentPlaylistScreenBinding

    private lateinit var trackBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var moreBottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private lateinit var playlist: Playlist

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlist = getPlaylistFromArgs()

        binding.overlay.alpha = INITIAL_OVERLAY_ALPHA

        binding.playlistToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        val filePath = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "playlistsCovers"
        )
        val file = if (playlist.coverFilePath != null) File(filePath, playlist.coverFilePath!!) else null

        binding.apply {
            Glide.with(root)
                .load(file)
                .placeholder(R.drawable.ic_cover_placeholder_312)
                .centerCrop()
                .into(playlistCover)
            playlistName.text = playlist.playlistName
            playlistDescription.text = playlist.playlistDescription
            trackCount.text = root.resources.getQuantityString(R.plurals.track_count, playlist.trackCount, playlist.trackCount)

            Glide.with(root)
                .load(file)
                .placeholder(R.drawable.ic_cover_placeholder_45)
                .centerCrop()
                .into(bottomSheetPlaylistCover)
            bottomSheetPlaylistName.text = playlist.playlistName
            bottomSheetTrackCount.text = root.resources.getQuantityString(R.plurals.track_count, playlist.trackCount, playlist.trackCount)
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

    }

    private fun getPlaylistFromArgs() =
        Playlist(
            playlistId = requireArguments().getInt(ARGS_PLAYLIST_ID),
            playlistName = requireArguments().getString(ARGS_PLAYLIST_NAME) ?: "",
            playlistDescription = requireArguments().getString(ARGS_PLAYLIST_DESCRIPTION),
            coverFilePath = requireArguments().getString(ARGS_PLAYLIST_COVER_PATH),
            trackIdList = requireArguments().getIntegerArrayList(ARGS_TRACK_ID_LIST) ?: emptyList(),
            trackCount = requireArguments().getInt(ARGS_TRACK_COUNT)
        )


    companion object {
        private const val INITIAL_OVERLAY_ALPHA: Float = 0F

        private const val ARGS_PLAYLIST_ID = "playlist_id"
        private const val ARGS_PLAYLIST_NAME = "playlist_name"
        private const val ARGS_PLAYLIST_DESCRIPTION = "playlist_description"
        private const val ARGS_PLAYLIST_COVER_PATH = "playlist_cover_path"
        private const val ARGS_TRACK_ID_LIST = "track_id_list"
        private const val ARGS_TRACK_COUNT = "track_count"

        fun createArgs(
            playlistId: Int,
            playlistName: String,
            playlistDescription: String?,
            coverFilePath: String?,
            trackIdList: List<Int>,
            trackCount: Int

        ): Bundle =
            bundleOf(
                ARGS_PLAYLIST_ID to playlistId,
                ARGS_PLAYLIST_NAME to playlistName,
                ARGS_PLAYLIST_DESCRIPTION to playlistDescription,
                ARGS_PLAYLIST_COVER_PATH to coverFilePath,
                ARGS_TRACK_ID_LIST to trackIdList,
                ARGS_TRACK_COUNT to trackCount
            )

    }
}