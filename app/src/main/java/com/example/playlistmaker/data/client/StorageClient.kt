package com.example.playlistmaker.data.client

import android.content.SharedPreferences

interface StorageClient<T> {
    fun storeData(data: T)
    fun getData(): T?
}