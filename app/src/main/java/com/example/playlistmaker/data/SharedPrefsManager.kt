package com.example.playlistmaker.data

import android.content.Context
import com.google.gson.Gson

class SharedPrefsManager(context: Context) {
    companion object {
        const val TRACK_SHARED_PREFS = "track_shared_prefs"
        const val LAST_TRACK_LIST_KEY = "last_track_list_key"
    }

    private val sharedPrefs = context.getSharedPreferences(TRACK_SHARED_PREFS, Context.MODE_PRIVATE)
    private val gson = Gson()


}