package com.example.playlistmaker.ui.editplaylist

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.editplaylist.EditPlaylistViewModel
import com.example.playlistmaker.ui.makeplaylist.MakePlaylistFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment : MakePlaylistFragment() {
    override val viewModel: EditPlaylistViewModel by viewModel()

    private var initialPlaylistName: String? = ""
    private var initialPlaylistDescription: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialPlaylistName = requireArguments().getString(ARGS_PLAYLIST_NAME)
        initialPlaylistDescription = requireArguments().getString(ARGS_PLAYLIST_DESCRIPTION)

        binding.apply {
            newPlaylistToolbar.setTitle(R.string.edit)
            makePlaylistButton.setText(R.string.save)
            playlistName.setText(initialPlaylistName)
            playlistDescription.setText(initialPlaylistDescription)
        }

    }

    override fun navigateUpWithConfirmation() {
        if ((binding.playlistName.text.toString() != initialPlaylistName) or (binding.playlistDescription.text.toString() != initialPlaylistDescription)) {

        }
    }

    companion object {
        private const val ARGS_PLAYLIST_ID = "playlist_id"
        private const val ARGS_PLAYLIST_NAME = "playlist_name"
        private const val ARGS_PLAYLIST_DESCRIPTION = "playlist_description"
        private const val ARGS_COVER_FILE_PATH = "cover_file_path"

        fun createArgs(
            playlistId: Int,
            playlistName: String,
            playlistDescription: String?,
            coverFilePath: String?
        ): Bundle =
            bundleOf(
                ARGS_PLAYLIST_ID to playlistId,
                ARGS_PLAYLIST_NAME to playlistName,
                ARGS_PLAYLIST_DESCRIPTION to playlistDescription,
                ARGS_COVER_FILE_PATH to coverFilePath
            )
    }
}