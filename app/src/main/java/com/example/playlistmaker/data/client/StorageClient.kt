package com.example.playlistmaker.data.client

interface StorageClient<T> {
    fun storeData(data: T)
    fun getData(): T?
}