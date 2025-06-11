package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

class SearchActivity : AppCompatActivity() {
    private var inputText: String = INPUT_TEXT_DEF

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
                clearButton.visibility = setVisibilityByInput(s)
                trackRecyclerView.visibility = setVisibilityByInput(s)
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

        val trackAdapter = TrackAdapter(trackList)
        trackRecyclerView.adapter = trackAdapter
    }

    private fun setVisibilityByInput(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_TEXT, inputText)
    }

    fun hideKeyboard(view: View) {
        val keyboardService = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        keyboardService.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private companion object {
        const val INPUT_TEXT = "INPUT_TEXT"
        const val INPUT_TEXT_DEF = ""
    }
}