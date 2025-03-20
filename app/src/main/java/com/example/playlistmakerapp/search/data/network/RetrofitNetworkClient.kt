package com.example.playlistmakerapp.search.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.playlistmakerapp.search.data.NetworkClient
import com.example.playlistmakerapp.search.data.dto.Response
import com.example.playlistmakerapp.search.data.dto.TrackSearchRequest

class RetrofitNetworkClient(private val context: Context, private val trackService: TrackApi) :
    NetworkClient {


    override fun doRequest(dto: Any): Response {

        if (isConnected() == false) {
            return Response().apply { resultCode = -1 }
        }

        if (dto is TrackSearchRequest) {
            val resp = trackService.search(dto.term).execute()

            val body = resp.body() ?: Response()

            return body.apply { resultCode = resp.code() }
        } else {
            return Response().apply { resultCode = 400 }
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