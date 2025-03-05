package com.example.playlistmakerapp.data

import com.example.playlistmakerapp.data.dto.Response

interface NetworkClient {
    fun doRequest(dto: Any) : Response
}