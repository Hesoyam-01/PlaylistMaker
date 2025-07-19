package com.example.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
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
        Toast.makeText(this, "Выбран трек: ${track.trackName}", Toast.LENGTH_SHORT).show()
        updateLastTrackList(track)
        saveLastTrackList()
    }
    private val lastTrackAdapter = TrackAdapter(lastTrackList) { track ->
        Toast.makeText(this, "Выбран трек: ${track.trackName}", Toast.LENGTH_SHORT).show()
        updateLastTrackList(track)
        saveLastTrackList()
    }

    private lateinit var lastQuery: String

    private lateinit var searchPlaceholder: LinearLayout
    private lateinit var searchPlaceholderImage: ImageView
    private lateinit var searchPlaceholderText: TextView
    private lateinit var searchUpdateQueryButton: CardView
    private lateinit var searchBar: EditText
    private lateinit var searchToolbar: MaterialToolbar
    private lateinit var searchClearButton: Button
    private lateinit var lastTracks: LinearLayout
    private lateinit var trackRecyclerView: RecyclerView
    private lateinit var lastTrackRecyclerView: RecyclerView

    private val trackSharedPrefs by lazy {
        getSharedPreferences(TRACK_SHARED_PREFS, Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchPlaceholder = findViewById(R.id.search_placeholder)
        searchPlaceholderImage = findViewById(R.id.search_placeholder_image)
        searchPlaceholderText = findViewById(R.id.search_placeholder_text)
        searchUpdateQueryButton = findViewById(R.id.search_update_query_button)
        searchBar = findViewById(R.id.search_bar)
        searchToolbar = findViewById(R.id.search_toolbar)
        searchClearButton = findViewById(R.id.search_clear_button)
        lastTracks = findViewById(R.id.last_tracks)
        trackRecyclerView = findViewById(R.id.track_recycler_view)
        lastTrackRecyclerView = findViewById(R.id.last_track_recycler_view)

        trackRecyclerView.adapter = trackAdapter
        lastTrackRecyclerView.adapter = lastTrackAdapter

        searchToolbar.setNavigationOnClickListener {
            finish()
        }

        loadLastTrackList()
        showLastTracks()

        val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == LAST_TRACK_LIST_KEY) {
                val jsonLastTrackList = sharedPreferences?.getString(LAST_TRACK_LIST_KEY, null)
                if (jsonLastTrackList != null) {
                    val type = object : TypeToken<MutableList<Track>>() {}.type
                    lastTrackAdapter.trackList.clear()
                    lastTrackAdapter.trackList.addAll(Gson().fromJson<MutableList<Track>>(jsonLastTrackList, type))
                    lastTrackAdapter.notifyDataSetChanged()
                }
            }
        }

        trackSharedPrefs.registerOnSharedPreferenceChangeListener(listener)

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchClearButton.isVisible = !s.isNullOrEmpty()
                trackRecyclerView.isVisible = !s.isNullOrEmpty()
            }

            override fun afterTextChanged(s: Editable?) {
                inputText = s.toString()
            }
        }

        searchBar.addTextChangedListener(textWatcher)

        if (savedInstanceState != null) {
            inputText = savedInstanceState.getString(INPUT_TEXT, inputText)
            searchBar.setText(inputText)
        }

        searchClearButton.setOnClickListener {
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            searchBar.setText("")
            hideKeyboard(searchBar)
        }

        searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (searchBar.text.isNotEmpty()) {
                    searchPlaceholder.visibility = View.GONE
                    searchUpdateQueryButton.visibility = View.GONE
                    search(searchBar.text.toString())
                }
                true
            }
            false
        }

        searchUpdateQueryButton.setOnClickListener {
            search(lastQuery)
        }
    }

    private fun saveLastTrackList() {
        trackSharedPrefs.edit()
            .putString(LAST_TRACK_LIST_KEY, Gson().toJson(lastTrackList))
            .apply()
    }

    private fun updateLastTrackList(track: Track) {
        lastTrackList.removeAll { it.trackId == track.trackId }
        if (lastTrackList.size >= 10) {
            lastTrackList.removeAt(9)
        }
        lastTrackList.add(0, track)
        Log.d("index", "${lastTrackList.indexOf(track)}")
        Log.d("size", "${lastTrackList.size}")
    }

    private fun loadLastTrackList() {
        val jsonLastTrackList = trackSharedPrefs.getString(LAST_TRACK_LIST_KEY, null)
        val type = object : TypeToken<MutableList<Track>>() {}.type
        lastTrackList.addAll(Gson().fromJson<MutableList<Track>>(jsonLastTrackList, type))
    }

    private fun showLastTracks() {
        lastTracks.visibility = View.VISIBLE
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

    private fun search(query: String) {
        tracksService.getTracks(query)
            .enqueue(object : Callback<TracksResponse> {
                override fun onResponse(
                    call: Call<TracksResponse>,
                    response: Response<TracksResponse>
                ) {
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


                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
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
        const val LAST_TRACK_LIST_KEY = "last_track_list_key"
        const val INPUT_TEXT = "INPUT_TEXT"
        const val INPUT_TEXT_DEF = ""
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