package com.example.playlistmaker.ui.library

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.databinding.FragmentMakePlaylistBinding
import com.example.playlistmaker.presentation.library.MakePlaylistFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MakePlaylistFragment : Fragment() {
    private val viewModel: MakePlaylistFragmentViewModel by viewModel()

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

        binding.setPlaylistCover.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}