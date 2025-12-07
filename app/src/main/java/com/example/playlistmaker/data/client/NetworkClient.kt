package com.example.playlistmaker.data.client

import com.example.playlistmaker.data.dto.Response

interface NetworkClient {
    suspend fun doRequest(dto: Any) : Response
}