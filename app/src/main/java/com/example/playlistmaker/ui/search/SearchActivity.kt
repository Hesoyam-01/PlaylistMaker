package com.example.playlistmaker.ui.search

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.example.playlistmaker.R
import com.example.playlistmaker.data.network.SearchActivityAPI
import com.example.playlistmaker.SearchHistory
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.ui.player.PlayerActivity
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Locale

class SearchActivity : AppCompatActivity() {
    private val handler = Handler(Looper.getMainLooper())

    private var inputText: String = INPUT_TEXT_DEF
    private val trackList = mutableListOf<Track>()
    private val lastTrackList = mutableListOf<Track>()
    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val tracksService = retrofit.create(SearchActivityAPI::class.java)
    private val trackAdapter = TrackAdapter(trackList) { track ->
        val trackIntent = Intent(this@SearchActivity, PlayerActivity::class.java)
        trackIntent.putExtra(
            "TRACK_COVER",
            track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
        )
        trackIntent.putExtra("TRACK_NAME", track.trackName)
        trackIntent.putExtra("ARTIST_NAME", track.artistName)
        trackIntent.putExtra(
            "TRACK_TIME",
            SimpleDateFormat("m:ss", Locale.getDefault()).format(track.trackTimeMillis)
        )
        trackIntent.putExtra("ALBUM_NAME", track.collectionName)
        trackIntent.putExtra("RELEASE_DATE", track.releaseDate.substring(0, 4))
        trackIntent.putExtra("GENRE_NAME", track.primaryGenreName)
        trackIntent.putExtra("COUNTRY", track.country)
        trackIntent.putExtra("PREVIEW_URL", track.previewUrl)
        startActivity(trackIntent)
        searchHistory.updateLastTrackList(track)
    }

    private lateinit var trackSharedPrefs: SharedPreferences
    private lateinit var searchHistory: SearchHistory

    private lateinit var lastQuery: String

    private lateinit var searchPlaceholder: LinearLayout
    private lateinit var searchPlaceholderImage: ImageView
    private lateinit var searchPlaceholderText: TextView
    private lateinit var searchUpdateQueryButton: CardView
    private lateinit var searchBar: EditText
    private lateinit var searchToolbar: MaterialToolbar
    private lateinit var searchClearButton: Button
    private lateinit var recentClearButton: CardView
    private lateinit var lastTracks: LinearLayout
    private lateinit var trackRecyclerView: RecyclerView
    private lateinit var lastTrackRecyclerView: RecyclerView
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
        lastTracks = findViewById(R.id.last_tracks)
        trackRecyclerView = findViewById(R.id.track_recycler_view)
        lastTrackRecyclerView = findViewById(R.id.last_track_recycler_view)

        trackSharedPrefs = getSharedPreferences(TRACK_SHARED_PREFS, Context.MODE_PRIVATE)
        searchHistory = SearchHistory(trackSharedPrefs, lastTrackList, this) {
            searchBar.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) visibilityOfLastTracks()
            }
        }

        trackRecyclerView.adapter = trackAdapter
        lastTrackRecyclerView.adapter = searchHistory.lastTrackAdapter

        searchToolbar.setNavigationOnClickListener {
            finish()
        }

        recentClearButton.setOnClickListener {
            lastTrackList.clear()
            searchHistory.saveLastTrackList()
            visibilityOfLastTracks()
            searchHistory.lastTrackAdapter.notifyDataSetChanged()
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchClearButton.isVisible = !s.isNullOrEmpty()
                trackRecyclerView.isVisible = !s.isNullOrEmpty()
                searchPlaceholder.visibility = View.GONE
                if (searchBar.text.isNotEmpty()) debounceSearch()
            }

            override fun afterTextChanged(s: Editable?) {
                inputText = s.toString()
                trackList.clear()
                trackAdapter.notifyDataSetChanged()
                visibilityOfLastTracks()

            }
        }

        searchBar.addTextChangedListener(textWatcher)
        searchProgressBar = findViewById(R.id.search_progressBar)

        if (savedInstanceState != null) {
            inputText = savedInstanceState.getString(INPUT_TEXT, inputText)
            searchBar.setText(inputText)
        }

        searchClearButton.setOnClickListener {
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            searchBar.setText("")
            hideKeyboard(searchBar)
            visibilityOfLastTracks()
            handler.removeCallbacks(searchRunnable)
        }

        searchUpdateQueryButton.setOnClickListener {
            search(lastQuery)
        }
    }

    private val searchRunnable = Runnable {
            searchPlaceholder.visibility = View.GONE
            searchUpdateQueryButton.visibility = View.GONE
            search(searchBar.text.toString())
    }

    fun debounceSearch() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun visibilityOfLastTracks() {
        lastTracks.isVisible = lastTrackList.isNotEmpty()
    }

    private fun showPlaceholder(placeholderType: PlaceholderType) {
        trackList.clear()
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

    private fun search(query: String) {
        searchProgressBar.visibility = View.VISIBLE
        tracksService.searchTracks(query)
            .enqueue(object : Callback<TracksSearchResponse> {
                override fun onResponse(
                    call: Call<TracksSearchResponse>,
                    response: Response<TracksSearchResponse>
                ) {
                    searchProgressBar.visibility = View.GONE
                    val responseResults = response.body()?.results
                    lastQuery = searchBar.text.toString()
                    if (response.isSuccessful) {
                        searchPlaceholder.visibility = View.GONE
                        searchUpdateQueryButton.visibility = View.GONE
                        if (!responseResults.isNullOrEmpty()) {
                            trackList.clear()
                            trackList.addAll(responseResults)
                            trackAdapter.notifyDataSetChanged()
                        } else showPlaceholder(PlaceholderType.NOTHING_FOUND)
                    }
                }


                override fun onFailure(call: Call<TracksSearchResponse>, t: Throwable) {
                    searchProgressBar.visibility = View.GONE
                    lastQuery = searchBar.text.toString()
                    showPlaceholder(PlaceholderType.CONNECTION_PROBLEMS)
                }

            })
    }



    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_TEXT, inputText)
    }

    private companion object {
        const val TRACK_SHARED_PREFS = "track_shared_prefs"
        const val INPUT_TEXT = "INPUT_TEXT"
        const val INPUT_TEXT_DEF = ""
        const val SEARCH_DEBOUNCE_DELAY = 2000L

        fun hideKeyboard(view: View) {
            val keyboardService =
                view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboardService.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}

enum class PlaceholderType {
    NOTHING_FOUND,
    CONNECTION_PROBLEMS
}