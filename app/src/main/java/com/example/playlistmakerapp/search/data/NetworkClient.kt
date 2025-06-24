package com.example.playlistmakerapp.search.data

import com.example.playlistmakerapp.search.data.dto.Response

interface NetworkClient {
    suspend fun doRequest(dto: Any) : Response
}