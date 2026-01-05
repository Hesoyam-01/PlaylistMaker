package com.example.playlistmaker.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.domain.model.library.Playlist
import com.example.playlistmaker.presentation.library.PlaylistsFragmentViewModel
import com.example.playlistmaker.presentation.library.PlaylistsState
import com.example.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {
    private val viewModel: PlaylistsFragmentViewModel by viewModel()

    private lateinit var binding: FragmentPlaylistsBinding

    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit

    private lateinit var playlistsAdapter: PlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_makePlaylistFragment)
        }

        onPlaylistClickDebounce =
            debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) {
                findNavController().navigate(R.id.action_libraryFragment_to_playlistScreenFragment)
            }

        playlistsAdapter = PlaylistAdapter {
            onPlaylistClickDebounce(it)
        }

        binding.playlistsRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = playlistsAdapter
        }

        viewModel.observePlaylistLiveData().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.fillData()
    }

    private fun render(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Content -> showContent(state.playlists)
            is PlaylistsState.Empty -> showEmpty()
        }
    }

    private fun showContent(playlists: List<Playlist>) {
        binding.apply {
            emptyPlaylistsPlaceholder.visibility = View.GONE
            playlistsRecyclerView.visibility = View.VISIBLE
        }
        playlistsAdapter.updateList(playlists)
    }

    private fun showEmpty() {
        binding.apply {
            playlistsRecyclerView.visibility = View.GONE
            emptyPlaylistsPlaceholder.visibility = View.VISIBLE
        }
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 300L

        fun newInstance() = PlaylistsFragment()
    }
}