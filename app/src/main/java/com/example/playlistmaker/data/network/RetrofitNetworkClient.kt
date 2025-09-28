package com.example.playlistmaker.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.playlistmaker.data.client.NetworkClient
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TracksSearchRequest

class RetrofitNetworkClient(private val tracksService: SearchAPI, private val context: Context) :
    NetworkClient {
    override fun doRequest(dto: Any): Response {
        return when {
            !isConnected() -> {
                Response().apply { resultCode = -1 }
            }

            dto is TracksSearchRequest -> {
                    val resp = tracksService.searchTracks(dto.query).execute()
                    val body = resp.body() ?: Response()
                    body.apply { resultCode = 200 }
                }

            else -> {
                Response().apply { resultCode = 400 }
            }
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}