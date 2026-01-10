package com.example.playlistmaker.ui.editplaylist

import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.editplaylist.EditPlaylistViewModel
import com.example.playlistmaker.ui.makeplaylist.MakePlaylistFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class EditPlaylistFragment : MakePlaylistFragment() {
    override val viewModel: EditPlaylistViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newPlaylistToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        val filePath = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "playlistsCovers"
        )
        val coverFilePath = requireArguments().getString(ARGS_COVER_FILE_PATH)
        val file =
            if (coverFilePath != null) File(filePath, coverFilePath) else null

        binding.apply {
            newPlaylistToolbar.setTitle(R.string.edit)
            makePlaylistButton.setText(R.string.save)
            if (file != null) {
                Glide.with(root)
                    .load(file)
                    .centerCrop()
                    .into(setPlaylistCover)
            }
            playlistName.setText(requireArguments().getString(ARGS_PLAYLIST_NAME))
            playlistDescription.setText(requireArguments().getString(ARGS_PLAYLIST_DESCRIPTION))
        }

        binding.makePlaylistButton.setOnClickListener {
            val id = requireArguments().getInt(ARGS_PLAYLIST_ID)
            val newName = binding.playlistName.text.toString()
            val newDescription = binding.playlistDescription.text.toString()
            var newCoverFilePath: String?

            if (selectedImageUri != null) {
                viewModel.saveImageAndGetPath(selectedImageUri!!)
                viewModel.observeImagePath().observe(viewLifecycleOwner) {
                    newCoverFilePath = it
                    viewModel.editPlaylistInformation(id, newName, newDescription, newCoverFilePath)
                    findNavController().navigateUp()
                }
            } else {
                viewModel.editPlaylistInformation(id, newName, newDescription, coverFilePath)
                findNavController().navigateUp()
            }
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