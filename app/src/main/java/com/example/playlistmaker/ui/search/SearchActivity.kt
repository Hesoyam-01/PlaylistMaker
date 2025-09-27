package com.example.playlistmaker.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.models.PlaceholderType
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.SearchActivity.SearchActivityViewModel
import com.example.playlistmaker.ui.player.PlayerActivity
import com.example.playlistmaker.ui.search.models.SearchActivityState
import com.example.playlistmaker.util.Creator
import com.google.android.material.appbar.MaterialToolbar

class SearchActivity : AppCompatActivity() {

    private companion object {
        const val INPUT_TEXT = "INPUT_TEXT"
        const val INPUT_TEXT_DEF = ""
        const val SEARCH_DEBOUNCE_DELAY = 1500L
        const val HISTORY_UPDATE_DELAY = 600L

        fun hideKeyboard(view: View) {
            val keyboardService =
                view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboardService.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private val tracksInteractor = Creator.provideTracksInteractor(this)

    private val handler = Handler(Looper.getMainLooper())

    private var inputText: String = INPUT_TEXT_DEF
    private var lastQuery: String = ""

    private var viewModel: SearchActivityViewModel? = null

    private lateinit var textWatcher: TextWatcher

    private lateinit var searchHistoryInteractor: SearchHistoryInteractor

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

        /*if (savedInstanceState != null) {
            inputText = savedInstanceState.getString(INPUT_TEXT, inputText)
            searchBar.setText(inputText)
        }*/

        searchHistoryInteractor = Creator.provideSearchHistoryInteractor(this)

        trackAdapter = TrackAdapter(mutableListOf()) { track ->
            startPlayerActivity(track)
        }
        lastTracksAdapter = TrackAdapter(searchHistoryInteractor.getLastTracksList()) { track ->
            startPlayerActivity(track)
        }

        binding.apply {
            tracksRecyclerView.adapter = trackAdapter
            lastTracksRecyclerView.adapter = lastTracksAdapter
        }

        viewModel = ViewModelProvider(
            this,
            SearchActivityViewModel.getFactory()
        )[SearchActivityViewModel::class.java]

        viewModel?.observeState()?.observe(this) {
            render(it)
        }

        binding.searchToolbar.setNavigationOnClickListener {
            finish()
        }

        binding.recentClearButton.setOnClickListener {
            searchHistoryInteractor.clearLastTracksList()
            searchHistoryInteractor.saveLastTracksList()
            showLastTracksList()
            lastTracksAdapter.notifyDataSetChanged()
        }

        binding.searchBar.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) binding.lastTracksView.isVisible =
                searchHistoryInteractor.getLastTracksList().isNotEmpty()
        }

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchClearButton.isVisible = !s.isNullOrEmpty()
                tracksRecyclerView.isVisible = !s.isNullOrEmpty()
                searchPlaceholder.visibility = View.GONE

                if (!s.isNullOrEmpty()) viewModel?.debounceSearch(s.toString())
//                else handler.removeCallbacks(searchRunnable)
            }

            override fun afterTextChanged(s: Editable?) {
                inputText = s.toString()
                trackAdapter.clearTrackList()
                showLastTracksList()
            }
        }

        binding.searchBar.addTextChangedListener(textWatcher)

        binding.searchClearButton.setOnClickListener {
            trackAdapter.clearTrackList()
            binding.searchBar.setText("")
            hideKeyboard(searchBar)
            showLastTracksList()
            handler.removeCallbacks(searchRunnable)
        }

        binding.searchUpdateQueryButton.setOnClickListener {
            tracksInteractor.searchTracks(lastQuery, this)
            trackAdapter.notifyDataSetChanged()
        }
    }

    /* override fun consume(resource: Resource<MutableList<Track>>) {
         handler.post {
             searchProgressBar.visibility = View.GONE
             trackAdapter.clearTrackList()
             when (resource) {
                 is Resource.Success -> {
                     searchPlaceholder.visibility = View.GONE
                     searchUpdateQueryButton.visibility = View.GONE
                     trackAdapter.updateList(resource.data)
                     trackAdapter.notifyDataSetChanged()
                     if (resource.data.isEmpty()) {
                         showPlaceholder(PlaceholderType.NOTHING_FOUND)
                     }
                 }

                 is Resource.Error -> showPlaceholder(PlaceholderType.CONNECTION_PROBLEMS)
             }
         }
     }

     private val searchRunnable = Runnable {
         val query = lastQuery
         searchProgressBar.visibility = View.VISIBLE
         searchPlaceholder.visibility = View.GONE
         searchUpdateQueryButton.visibility = View.GONE
         tracksInteractor.searchTracks(query, this)
     }


     fun debounceSearch(query: String) {
         this.lastQuery = query
         handler.removeCallbacks(searchRunnable)
         handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
     }*/

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
        val updateHistoryRunnable = Runnable {
            searchHistoryInteractor.addToLastTracksList(track)
            lastTracksAdapter.updateList(searchHistoryInteractor.getLastTracksList())
        }
        handler.postDelayed(updateHistoryRunnable, HISTORY_UPDATE_DELAY)
    }

    private fun render(state: SearchActivityState) {
        when (state) {
            is SearchActivityState.SearchHistory -> showSearchHistory(state.lastTracksList)
            is SearchActivityState.FoundTracks -> showFoundTracks(state.tracksList)
            is SearchActivityState.Empty -> showEmpty()
            is SearchActivityState.Error -> showError()
            is SearchActivityState.Loading -> showLoading()
        }
    }

    /*private fun showLastTracksList() {
        lastTracksView.isVisible = searchHistoryInteractor.getLastTracksList().isNotEmpty()
    }*/

    private fun showSearchHistory(lastTracksList: MutableList<Track>) {
        binding.lastTracksRecyclerView.visibility = View.VISIBLE
        lastTracksAdapter.updateList(lastTracksList)
    }

    private fun showFoundTracks(tracksList: MutableList<Track>) {
        binding.apply {
            searchProgressBar.visibility = View.GONE
            searchPlaceholder.visibility = View.GONE
            searchUpdateQueryButton.visibility = View.GONE
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
            tracksRecyclerView.visibility = View.GONE
            searchPlaceholder.visibility = View.GONE
            searchProgressBar.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        handler.removeCallbacks(searchRunnable)
        binding.searchBar.removeTextChangedListener(textWatcher)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_TEXT, inputText)
    }
}

