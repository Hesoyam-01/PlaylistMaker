package com.example.playlistmaker.util

sealed interface Resource<T> {
    class Success<T>(val data: T): Resource<T>
    class Error<T> : Resource<T>
}