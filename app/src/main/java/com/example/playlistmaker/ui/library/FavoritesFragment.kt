package com.example.playlistmaker.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.domain.model.search.Track
import com.example.playlistmaker.presentation.library.FavoritesState
import com.example.playlistmaker.presentation.library.FavoritesViewModel
import com.example.playlistmaker.ui.player.PlayerFragment
import com.example.playlistmaker.ui.search.TrackAdapter
import com.example.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {
    private val viewModel: FavoritesViewModel by viewModel()

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private lateinit var favoritesAdapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeFavoritesLiveData().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.fillData()

        favoritesAdapter = TrackAdapter(
            onTrackClick = {
                onTrackClickDebounce(it)
            },
            onTrackLongClick = {}
        )

        binding.favoritesRecyclerView.adapter = favoritesAdapter

        onTrackClickDebounce =
            debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) {
                navigateToPlayerFragment(it)
            }

    }

    private fun showContent(tracks: List<Track>) {
        binding.apply {
            emptyFavoritesPlaceholder.visibility = View.GONE
            favoritesRecyclerView.visibility = View.VISIBLE
        }
        favoritesAdapter.updateList(tracks)
    }

    private fun showEmpty() {
        binding.apply {
            favoritesRecyclerView.visibility = View.GONE
            emptyFavoritesPlaceholder.visibility = View.VISIBLE
        }
    }

    private fun render(state: FavoritesState) {
        when (state) {
            is FavoritesState.Content -> showContent(state.tracks)
            is FavoritesState.Empty -> showEmpty()
        }
    }

    private fun navigateToPlayerFragment(track: Track) {
        findNavController().navigate(
            R.id.action_libraryFragment_to_playerFragment,
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
        fun newInstance() = FavoritesFragment()

        private const val CLICK_DEBOUNCE_DELAY = 300L
    }
}
