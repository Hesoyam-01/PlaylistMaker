package com.example.playlistmaker.ui.library

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.databinding.FragmentMakePlaylistBinding
import com.example.playlistmaker.presentation.library.MakePlaylistFragmentViewModel
import com.example.playlistmaker.util.debounce
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MakePlaylistFragment : Fragment() {
    private val viewModel: MakePlaylistFragmentViewModel by viewModel()

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

        binding.newPlaylistToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    val transformation = MultiTransformation(
                        CenterCrop(),
                        RoundedCorners(dpToPx(8))
                    )

                    Glide.with(this)
                        .load(uri)
                        .apply(RequestOptions.bitmapTransform(transformation))
                        .into(binding.setPlaylistCover)

                    viewModel.saveImageToPrivateStorage(uri)
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

        binding.apply {

            val titleHintText = songTitle.hint
            val descriptionHintText = songDescription.hint

            songTitle.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    titleHint.visibility = View.VISIBLE
                    songTitle.hint = ""
                }
                else if (songTitle.text.isNullOrEmpty()) {
                    titleHint.visibility = View.GONE
                    songTitle.hint = titleHintText
                }
            }
            songDescription. setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    descriptionHint.visibility = View.VISIBLE
                    songDescription.hint = ""
                }
                else if (songDescription.text.isNullOrEmpty()) {
                    descriptionHint.visibility = View.GONE
                    songDescription.hint = descriptionHintText
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

        binding.songTitle.addTextChangedListener(textWatcher)

        binding.makePlaylistButton.setOnClickListener {
            viewModel.makePlaylist()
        }

    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding.apply {
            songTitle.removeTextChangedListener(textWatcher)
        }
        _binding = null

    }

    private companion object {
        private const val CLICK_DEBOUNCE_DELAY = 500L
    }
}