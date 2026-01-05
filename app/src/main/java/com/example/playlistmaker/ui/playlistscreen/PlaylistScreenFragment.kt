package com.example.playlistmaker.ui.playlistscreen

import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentPlaylistScreenBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior

class PlaylistScreenFragment : Fragment() {

    private lateinit var binding: FragmentPlaylistScreenBinding

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

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

        bottomSheetBehavior = BottomSheetBehavior.from(binding.tracksBottomSheet)

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

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    companion object {
        private const val ARGS_PLAYLIST_ID = "playlist_id"
        private const val ARGS_PLAYLIST_NAME = "playlist_description"
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

        ) : Bundle =
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