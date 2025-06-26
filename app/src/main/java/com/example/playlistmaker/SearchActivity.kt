package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class SearchActivity : AppCompatActivity() {
    private var inputText: String = INPUT_TEXT_DEF
    private val trackList = ArrayList<Track>()
    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val tracksService = retrofit.create(SearchActivityAPI::class.java)
    private val trackAdapter = TrackAdapter(trackList)

    private val searchPlaceholder = findViewById<LinearLayout>(R.id.search_placeholder)
    private val searchPlaceholderImage = findViewById<ImageView>(R.id.search_placeholder_image)
    private val searchPlaceholderText = findViewById<TextView>(R.id.search_placeholder_text)
    private val searchUpdateQueryButton = findViewById<CardView>(R.id.search_update_query_button)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val searchToolbar = findViewById<MaterialToolbar>(R.id.search_toolbar)
        val searchBar = findViewById<EditText>(R.id.search_bar)
        val clearButton = findViewById<MaterialButton>(R.id.clear_button)
        val trackRecyclerView = findViewById<RecyclerView>(R.id.track_recycler_view)

        searchToolbar.setNavigationOnClickListener {
            finish()
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
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

        clearButton.setOnClickListener {
            searchBar.setText("")
            hideKeyboard(searchBar)
        }

        trackRecyclerView.adapter = trackAdapter

        searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                true
            }
            false
        }
    }

    fun showPlaceholder(placeholderType: PlaceholderType) {
        searchPlaceholder.visibility = View.VISIBLE
        trackList.clear()
        trackAdapter.notifyDataSetChanged()

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

    fun search() {
        tracksService.getTracks(inputText)
            .enqueue(object : Callback<TracksResponse> {
                override fun onResponse(
                    call: Call<TracksResponse>,
                    response: Response<TracksResponse>
                ) {
                    when (response.code()) {
                        200 -> {
                            if (response.body()?.results?.isNotEmpty() == true) {
                                trackList.clear()
                                trackList.addAll(response.body()?.results!!)
                                trackAdapter.notifyDataSetChanged()
                            } else showPlaceholder(PlaceholderType.NOTHING_FOUND)
                        }

                        else -> {
                            showPlaceholder(PlaceholderType.CONNECTION_PROBLEMS)
                        }
                    }
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    showPlaceholder(PlaceholderType.CONNECTION_PROBLEMS)
                }

            })
    }

    interface SearchActivityAPI {
        @GET("/search?entity=song")
        fun getTracks(
            @Query("term") text: String
        ): Call<TracksResponse>
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_TEXT, inputText)
    }

    private companion object {
        const val INPUT_TEXT = "INPUT_TEXT"
        const val INPUT_TEXT_DEF = ""
        fun hideKeyboard(view: View) {
            val keyboardService =
                view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboardService.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}

enum class PlaceholderType() {
    NOTHING_FOUND,
    CONNECTION_PROBLEMS
}