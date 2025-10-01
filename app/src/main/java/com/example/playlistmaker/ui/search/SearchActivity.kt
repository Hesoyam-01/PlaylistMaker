package com.example.playlistmaker.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.models.search.Track
import com.example.playlistmaker.presentation.search.SearchState
import com.example.playlistmaker.presentation.search.SearchViewModel
import com.example.playlistmaker.ui.player.PlayerActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {
    private companion object {
        private const val HISTORY_UPDATE_DELAY = 600L
    }

    private val viewModel: SearchViewModel by viewModel()

    private val handler = Handler(Looper.getMainLooper())

    private lateinit var textWatcher: TextWatcher

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var lastTracksAdapter: TrackAdapter

    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        trackAdapter = TrackAdapter { track ->
            startPlayerActivity(track)
        }
        lastTracksAdapter = TrackAdapter { track ->
            startPlayerActivity(track)
            handler.postDelayed({
                viewModel.getSearchHistory()
            }, HISTORY_UPDATE_DELAY)
        }

        binding.apply {
            tracksRecyclerView.adapter = trackAdapter
            lastTracksRecyclerView.adapter = lastTracksAdapter
        }

        viewModel.observeSearchState().observe(this) {
            render(it)
        }

        binding.searchToolbar.setNavigationOnClickListener {
            finish()
        }

        binding.recentClearButton.setOnClickListener {
            viewModel.clearSearchHistory()
            binding.searchHistoryView.visibility = View.GONE
        }

        binding.searchBar.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) viewModel.getSearchHistory()
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

                viewModel.debounceSearch(
                    s?.toString() ?: ""
                )
                if (s.isNullOrEmpty()) viewModel.getSearchHistory()

            }

            override fun afterTextChanged(s: Editable?) {
                trackAdapter.clearTrackList()
                binding.searchPlaceholder.visibility = View.GONE
            }
        }

        binding.searchBar.addTextChangedListener(textWatcher)
    }

    private fun startPlayerActivity(track: Track) {
        val trackIntent = Intent(this@SearchActivity, PlayerActivity::class.java).apply {
            putExtra("TRACK_COVER", track.artworkUrl100)
            putExtra("TRACK_NAME", track.trackName)
            putExtra("ARTIST_NAME", track.artistName)
            putExtra("TRACK_TIME", track.trackTime)
            putExtra("ALBUM_NAME", track.collectionName)
            putExtra("RELEASE_DATE", track.releaseDate)
            putExtra("GENRE_NAME", track.primaryGenreName)
            putExtra("COUNTRY", track.country)
            putExtra("PREVIEW_URL", track.previewUrl)
        }
        startActivity(trackIntent)
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

    private fun showSearchHistory(lastTracksList: MutableList<Track>) {
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

    private fun showFoundTracks(tracksList: MutableList<Track>) {
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

    override fun onDestroy() {
        super.onDestroy()
        binding.searchBar.removeTextChangedListener(textWatcher)
    }
}

