package com.example.playlistmaker.ui.search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.domain.model.search.Track
import com.example.playlistmaker.presentation.search.SearchState
import com.example.playlistmaker.presentation.search.SearchViewModel
import com.example.playlistmaker.ui.player.PlayerFragment
import com.example.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private val viewModel: SearchViewModel by viewModel()

    private lateinit var textWatcher: TextWatcher

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var lastTracksAdapter: TrackAdapter

    private lateinit var binding: FragmentSearchBinding

    private lateinit var getSearchHistoryDebounce: (Unit) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce = debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) {
            navigateToPlayerFragment(it)
        }

        getSearchHistoryDebounce = debounce(
            HISTORY_UPDATE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            true
        ) {
            viewModel.getSearchHistory()
        }

        trackAdapter = TrackAdapter {
            onTrackClickDebounce(it)
        }

        lastTracksAdapter = TrackAdapter {
            onTrackClickDebounce(it)
            getSearchHistoryDebounce
        }

        binding.apply {
            tracksRecyclerView.adapter = trackAdapter
            lastTracksRecyclerView.adapter = lastTracksAdapter
        }

        viewModel.observeSearchState().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.recentClearButton.setOnClickListener {
            viewModel.clearSearchHistory()
            binding.searchHistoryView.visibility = View.GONE
        }

        binding.searchBar.setOnFocusChangeListener { _, hasFocus ->
            if (binding.searchBar.text.isNullOrEmpty() and hasFocus) viewModel.getSearchHistory()
        }

        binding.searchClearButton.setOnClickListener {
            trackAdapter.clearTrackList()
            binding.searchBar.setText("")
            hideKeyboard(binding.searchBar)
            viewModel.getSearchHistory()
        }

        binding.searchUpdateQueryButton.setOnClickListener {
            viewModel.repeatLastSearch()
        }

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.searchClearButton.isVisible = !s.isNullOrEmpty()

                if (binding.searchBar.hasFocus()) {
                    if (s.isNullOrEmpty()) {
                        viewModel.cancelSearchRequest()
                        viewModel.getSearchHistory()
                    }
                    else viewModel.debounceSearch(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {
                trackAdapter.clearTrackList()
                binding.searchPlaceholder.visibility = View.GONE
                if (s.isNullOrEmpty()) viewModel.getSearchHistory()
            }
        }

        binding.searchBar.addTextChangedListener(textWatcher)
    }

    private fun navigateToPlayerFragment(track: Track) {
        findNavController().navigate(
            R.id.action_searchFragment_to_playerFragment,
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
                country = track.country,
//                isFavorite = track.isFavorite
            )
        )

        viewModel.addToSearchHistory(track)
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.SearchHistory -> showSearchHistory(state.lastTracksList)
            is SearchState.FoundTracks -> showFoundTracks(state.tracksList)
            is SearchState.Empty -> showEmpty()
            is SearchState.Error -> showError()
            is SearchState.Loading -> showLoading()
        }
    }

    private fun showSearchHistory(lastTracksList: List<Track>) {
        if (lastTracksList.isNotEmpty()) {
            binding.apply {
                searchPlaceholder.visibility = View.GONE
                searchUpdateQueryButton.visibility = View.GONE
                tracksRecyclerView.visibility = View.GONE
                searchHistoryView.visibility = View.VISIBLE
            }
            lastTracksAdapter.updateList(lastTracksList)
        }
    }

    private fun showFoundTracks(tracksList: List<Track>) {
        binding.apply {
            searchProgressBar.visibility = View.GONE
            searchPlaceholder.visibility = View.GONE
            searchUpdateQueryButton.visibility = View.GONE
            searchHistoryView.visibility = View.GONE
            tracksRecyclerView.visibility = View.VISIBLE
        }
        trackAdapter.updateList(tracksList)
    }

    private fun showPlaceholder(placeholderType: PlaceholderType) {
        binding.searchPlaceholder.visibility = View.VISIBLE

        when (placeholderType) {
            PlaceholderType.NOTHING_FOUND -> {
                binding.apply {
                    searchPlaceholderImage.setImageResource(R.drawable.ic_nothing_found_120)
                    searchPlaceholderText.setText(R.string.nothing_found)
                }
            }

            PlaceholderType.CONNECTION_PROBLEMS -> {
                binding.apply {
                    searchPlaceholderImage.setImageResource(R.drawable.ic_connection_problems_120)
                    searchPlaceholderText.setText(R.string.connection_problems)
                    searchUpdateQueryButton.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun showEmpty() {
        binding.apply {
            tracksRecyclerView.visibility = View.GONE
            searchProgressBar.visibility = View.GONE
            searchUpdateQueryButton.visibility = View.GONE
        }
        showPlaceholder(PlaceholderType.NOTHING_FOUND)
    }

    private fun showError() {
        binding.apply {
            searchHistoryView.visibility = View.GONE
            tracksRecyclerView.visibility = View.GONE
            searchProgressBar.visibility = View.GONE
        }
        showPlaceholder(PlaceholderType.CONNECTION_PROBLEMS)
    }

    private fun showLoading() {
        binding.apply {
            searchHistoryView.visibility = View.GONE
            tracksRecyclerView.visibility = View.GONE
            searchPlaceholder.visibility = View.GONE
            searchProgressBar.visibility = View.VISIBLE
        }
    }

    private fun hideKeyboard(view: View) {
        val keyboardService =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        keyboardService.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.searchBar.removeTextChangedListener(textWatcher)
    }

    private companion object {
        private const val HISTORY_UPDATE_DELAY = 500L
        private const val CLICK_DEBOUNCE_DELAY = 300L
    }
}