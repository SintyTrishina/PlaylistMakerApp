package com.example.playlistmakerapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TrackApi {
    @GET("/search?entity=song")
    fun search(@Query("term", encoded = false) term: String): Call<TrackResponse>
}