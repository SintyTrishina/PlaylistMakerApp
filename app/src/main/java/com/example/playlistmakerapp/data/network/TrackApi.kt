package com.example.playlistmakerapp.data.network

import com.example.playlistmakerapp.data.dto.TrackSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TrackApi {
    @GET("/search?entity=song")
    fun search(@Query("term", encoded = false) term: String): Call<TrackSearchResponse>
}