package com.example.playlistmaker.ui.library

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMakePlaylistBinding
import com.example.playlistmaker.presentation.library.MakePlaylistFragmentViewModel
import com.example.playlistmaker.util.debounce
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MakePlaylistFragment : Fragment() {
    private val viewModel: MakePlaylistFragmentViewModel by viewModel()

    private lateinit var confirmDialog: MaterialAlertDialogBuilder
    private var isImageChosen = false

    private var selectedImageUri: Uri? = null

    private lateinit var textWatcher: TextWatcher

    private var _binding: FragmentMakePlaylistBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMakePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fun navigateUpWithConfirmation() {
            if (binding.playlistName.text.isNotEmpty()
                or binding.playlistDescription.text.isNotEmpty()
                or isImageChosen) confirmDialog.show()
            else findNavController().navigateUp()
        }

        binding.newPlaylistToolbar.setNavigationOnClickListener {
            navigateUpWithConfirmation()
        }

        requireActivity().onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateUpWithConfirmation()
            }
        })

        confirmDialog = MaterialAlertDialogBuilder(requireContext(), androidx.appcompat.R.style.Theme_AppCompat_DayNight_Dialog_Alert)
            .setTitle(R.string.finish_making)
            .setMessage(R.string.unsaved_data)
            .setNeutralButton(
                R.string.cancel
            ) { _, _ -> }
            .setPositiveButton(R.string.finish) { _, _ ->
                findNavController().navigateUp()
            }


        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                selectedImageUri = uri

                if (uri != null) {
                    val transformation = MultiTransformation(
                        CenterCrop(),
                        RoundedCorners(dpToPx(8))
                    )

                    Glide.with(this)
                        .load(uri)
                        .apply(RequestOptions.bitmapTransform(transformation))
                        .into(binding.setPlaylistCover)

                    isImageChosen = true

                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        val debouncedLauncher =
            debounce<Unit>(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

        binding.setPlaylistCover.setOnClickListener {
            debouncedLauncher(Unit)
        }

        binding.makePlaylistButton.setOnClickListener {
            val name = binding.playlistName.text.toString()
            val description = binding.playlistDescription.text.toString()
            var coverFilePath: String?

            viewLifecycleOwner.lifecycleScope.launch {

                coverFilePath = if (selectedImageUri != null) viewModel.saveImageToPrivateStorage(
                    selectedImageUri!!
                )
                else null
                viewModel.makePlaylist(name, description, coverFilePath)
            }

            val message = getString(R.string.playlist_maked, name)
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

            findNavController().navigateUp()
        }

        binding.apply {

            val typedValue = TypedValue()

            requireContext().theme.resolveAttribute(
                com.google.android.material.R.attr.colorOnPrimaryFixedVariant,
                typedValue,
                true
            )
            val color = typedValue.data

            val nameHintText = playlistName.hint
            val descriptionHintText = playlistDescription.hint


            playlistName.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    nameHint.visibility = View.VISIBLE
                    nameHint.setTextColor(resources.getColor(R.color.blue))
                    playlistName.hint = ""
                    nameBackground.strokeColor = resources.getColor(R.color.blue)
                } else if (playlistName.text.isNullOrEmpty()) {
                    nameHint.visibility = View.GONE
                    nameHint.setTextColor(color)
                    playlistName.hint = nameHintText
                    nameBackground.strokeColor = color
                } else {
                    nameHint.setTextColor(color)
                    nameBackground.strokeColor = color
                }
            }

            playlistDescription.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    descriptionHint.visibility = View.VISIBLE
                    descriptionHint.setTextColor(resources.getColor((R.color.blue)))
                    playlistDescription.hint = ""
                    descriptionBackground.strokeColor = resources.getColor(R.color.blue)
                } else if (playlistDescription.text.isNullOrEmpty()) {
                    descriptionHint.visibility = View.GONE
                    descriptionHint.setTextColor(color)
                    playlistDescription.hint = descriptionHintText
                    descriptionBackground.strokeColor = color
                } else {
                    descriptionHint.setTextColor(color)
                    descriptionBackground.strokeColor = color
                }
            }
        }

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.makePlaylistButton.isEnabled = !s.isNullOrEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.playlistName.addTextChangedListener(textWatcher)

    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding.apply {
            playlistName.removeTextChangedListener(textWatcher)
        }
        _binding = null

    }

    private companion object {
        private const val CLICK_DEBOUNCE_DELAY = 500L
    }
}