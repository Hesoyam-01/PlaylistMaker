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
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.data.SearchResult
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.PlaceholderType
import com.example.playlistmaker.ui.player.PlayerActivity
import com.google.android.material.appbar.MaterialToolbar

class SearchActivity () : AppCompatActivity(), TracksInteractor.TracksConsumer {
    private val tracksInteractor = Creator.getTracksInteractor()

    private val handler = Handler(Looper.getMainLooper())

    private var inputText: String = INPUT_TEXT_DEF

    private lateinit var searchHistoryInteractor: SearchHistoryInteractor
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var lastTracksAdapter: TrackAdapter
    private lateinit var lastQuery: String

    private lateinit var searchPlaceholder: LinearLayout
    private lateinit var searchPlaceholderImage: ImageView
    private lateinit var searchPlaceholderText: TextView
    private lateinit var searchUpdateQueryButton: CardView
    private lateinit var searchBar: EditText
    private lateinit var searchToolbar: MaterialToolbar
    private lateinit var searchClearButton: Button
    private lateinit var recentClearButton: CardView
    private lateinit var lastTracksView: LinearLayout
    private lateinit var tracksRecyclerView: RecyclerView
    private lateinit var lastTracksRecyclerView: RecyclerView
    private lateinit var searchProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        searchPlaceholder = findViewById(R.id.search_placeholder)
        searchPlaceholderImage = findViewById(R.id.search_placeholder_image)
        searchPlaceholderText = findViewById(R.id.search_placeholder_text)
        searchUpdateQueryButton = findViewById(R.id.search_update_query_button)
        searchBar = findViewById(R.id.search_bar)
        searchToolbar = findViewById(R.id.search_toolbar)
        searchClearButton = findViewById(R.id.search_clear_button)
        recentClearButton = findViewById(R.id.recent_clear_button)
        lastTracksView = findViewById(R.id.last_tracks)
        tracksRecyclerView = findViewById(R.id.track_recycler_view)
        lastTracksRecyclerView = findViewById(R.id.last_track_recycler_view)

        searchHistoryInteractor = Creator.getSearchHistoryInteractor(this)
        searchHistoryInteractor.loadLastTracksList()

        searchBar.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) showLastTracksList()

        }

        trackAdapter = TrackAdapter(searchHistoryInteractor.getLastTracksList()) { track ->
            startPlayerActivity(track)
        }

        lastTracksAdapter = TrackAdapter(searchHistoryInteractor.getLastTracksList()) { track ->
            startPlayerActivity(track)
        }

        tracksRecyclerView.adapter = trackAdapter
        lastTracksRecyclerView.adapter = lastTracksAdapter

        searchToolbar.setNavigationOnClickListener {
            finish()
        }

        recentClearButton.setOnClickListener {
            searchHistoryInteractor.getLastTracksList().clear()
            searchHistoryInteractor.saveLastTracksList()
            showLastTracksList()
            lastTracksAdapter.notifyDataSetChanged()
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchClearButton.isVisible = !s.isNullOrEmpty()
                tracksRecyclerView.isVisible = !s.isNullOrEmpty()
                searchPlaceholder.visibility = View.GONE
                if (searchBar.text.isNotEmpty()) debounceSearch()
                else handler.removeCallbacks(searchRunnable)
            }

            override fun afterTextChanged(s: Editable?) {
                inputText = s.toString()
                searchHistoryInteractor.getLastTracksList().clear()
                trackAdapter.notifyDataSetChanged()
                showLastTracksList()


            }
        }

        searchBar.addTextChangedListener(textWatcher)
        searchProgressBar = findViewById(R.id.search_progressBar)

        if (savedInstanceState != null) {
            inputText = savedInstanceState.getString(INPUT_TEXT, inputText)
            searchBar.setText(inputText)
        }

        searchClearButton.setOnClickListener {
            searchHistoryInteractor.getLastTracksList().clear()
            trackAdapter.notifyDataSetChanged()
            searchBar.setText("")
            hideKeyboard(searchBar)
            showLastTracksList()
            handler.removeCallbacks(searchRunnable)
        }

        searchUpdateQueryButton.setOnClickListener {
            tracksInteractor.searchTracks(lastQuery, this)
            trackAdapter.notifyDataSetChanged()
        }
    }

    override fun consume(searchResult: SearchResult) {
        handler.post {
            searchProgressBar.visibility = View.GONE
            searchHistoryInteractor.getLastTracksList().clear()
            when (searchResult) {
                is SearchResult.Success -> {
                    searchPlaceholder.visibility = View.GONE
                    searchUpdateQueryButton.visibility = View.GONE
                    searchHistoryInteractor.getLastTracksList().addAll(searchResult.tracks)
                    trackAdapter.notifyDataSetChanged()
                    if (searchHistoryInteractor.getLastTracksList().isEmpty()) {
                        showPlaceholder(PlaceholderType.NOTHING_FOUND)
                    }
                }

                is SearchResult.Failure -> showPlaceholder(PlaceholderType.CONNECTION_PROBLEMS)
            }
        }
    }

    private val searchRunnable = Runnable {
        searchProgressBar.visibility = View.VISIBLE
        searchPlaceholder.visibility = View.GONE
        searchUpdateQueryButton.visibility = View.GONE
        tracksInteractor.searchTracks(searchBar.text.toString(), this)
        lastQuery = searchBar.text.toString()
    }

    fun debounceSearch() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun showLastTracksList() {
        lastTracksView.isVisible = searchHistoryInteractor.getLastTracksList().isNotEmpty()
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
        val updateHistoryRunnable = Runnable {
            searchHistoryInteractor.addToLastTracksList(track)
            lastTracksAdapter.notifyDataSetChanged()
        }
        handler.postDelayed(updateHistoryRunnable, HISTORY_UPDATE_DELAY)
    }

    private fun showPlaceholder(placeholderType: PlaceholderType) {
        searchHistoryInteractor.getLastTracksList().clear()
        trackAdapter.notifyDataSetChanged()
        searchPlaceholder.visibility = View.VISIBLE

        when (placeholderType) {
            PlaceholderType.NOTHING_FOUND -> {
                searchPlaceholderImage.setImageResource(R.drawable.ic_nothing_found_120)
                searchPlaceholderText.setText(R.string.nothing_found)
            }

            PlaceholderType.CONNECTION_PROBLEMS -> {
                searchPlaceholderImage.setImageResource(R.drawable.ic_connection_problems_120)
                searchPlaceholderText.setText(R.string.connection_problems)
                searchUpdateQueryButton.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_TEXT, inputText)
    }

    private companion object {
        const val INPUT_TEXT = "INPUT_TEXT"
        const val INPUT_TEXT_DEF = ""
        const val SEARCH_DEBOUNCE_DELAY = 2000L
        const val HISTORY_UPDATE_DELAY = 600L

        fun hideKeyboard(view: View) {
            val keyboardService =
                view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboardService.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}

