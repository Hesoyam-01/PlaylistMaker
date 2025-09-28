package com.example.playlistmaker.data

import android.content.SharedPreferences

interface StorageClient<T> {
    fun storeData(data: T)
    fun getData(): T?
    fun registerListener(listener: SharedPreferences.OnSharedPreferenceChangeListener)
}