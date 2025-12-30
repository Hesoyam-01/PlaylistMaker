package com.example.playlistmaker.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.presentation.library.PlaylistsFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistsFragment : Fragment() {
    private val viewModel: PlaylistsFragmentViewModel by viewModel {
        parametersOf(true)
    }

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeIsPlaylistsEmpty().observe(viewLifecycleOwner) {
            if (it) binding.emptyPlaylistsPlaceholder.visibility = View.VISIBLE
        }

        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_makePlaylistFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}