package com.example.playlistmakerapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Adapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class Search : AppCompatActivity() {

    private var userText: String = ""
    private lateinit var inputEditText: EditText
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderImage: ImageView
    private lateinit var updateButton: Button
    private val baseUrl = "https://itunes.apple.com"
    private var tracks = ArrayList<Track>()
    private val trackAdapter = TrackAdapter()


    private val retrofit =
        Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create())
            .build()

    private val trackService = retrofit.create(TrackApi::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        savedInstanceState?.let {
            val json = it.getString(TRACK_LIST_KEY, "[]")
            val gson = Gson()
            val type = object : TypeToken<ArrayList<Track>>() {}.type
            tracks = gson.fromJson(json, type)
            trackAdapter.updateTracks(tracks)
        }


        inputEditText = findViewById(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        val buttonBack = findViewById<ImageView>(R.id.back)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        placeholderImage = findViewById(R.id.placeholderImage)
        updateButton = findViewById(R.id.updateButton)
        val trackRecyclerView = findViewById<RecyclerView>(R.id.trackRecyclerView)
        trackRecyclerView.adapter = trackAdapter

        buttonBack.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            tracks.clear()
            trackAdapter.updateTracks(tracks)
            inputEditText.setText("")
            placeholderImage.visibility = View.GONE
            updateButton.visibility = View.GONE
            showMessage("", "")

            clearButton.visibility = View.GONE
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
            }

            override fun afterTextChanged(s: Editable?) {
                userText = s.toString()
            }
        }
        inputEditText.addTextChangedListener(textWatcher)

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }



        updateButton.setOnClickListener {
            search()
        }
    }

    private fun search() {
        if (inputEditText.text.isNotEmpty()) {

            trackService.search(inputEditText.text.toString())
                .enqueue(object : Callback<TrackResponse> {
                    override fun onResponse(
                        call: Call<TrackResponse>, response: Response<TrackResponse>
                    ) {
                        if (response.code() == 200) {
                            tracks.clear()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                placeholderImage.visibility = View.GONE
                                updateButton.visibility = View.GONE
                                tracks.addAll(response.body()?.results!!)
                                trackAdapter.updateTracks(tracks)
                            }
                            if (tracks.isEmpty()) {

                                placeholderImage.setImageResource(R.drawable.error)
                                placeholderImage.visibility = View.VISIBLE
                                updateButton.visibility = View.GONE
                                showMessage(getString(R.string.nothing_found), "")
                            } else {
                                placeholderImage.visibility = View.GONE
                                updateButton.visibility = View.GONE
                                showMessage("", "")
                            }
                        } else {
                            placeholderImage.setImageResource(R.drawable.errorconnection)
                            placeholderImage.visibility = View.VISIBLE
                            updateButton.visibility = View.VISIBLE
                            showMessage(
                                getString(R.string.something_went_wrong), response.code().toString()
                            )
                        }
                    }

                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                        placeholderImage.setImageResource(R.drawable.errorconnection)
                        placeholderImage.visibility = View.VISIBLE
                        updateButton.visibility = View.VISIBLE
                        showMessage(
                            getString(R.string.something_went_wrong), t.message.toString()
                        )
                    }
                })
        }
    }

    private fun showMessage(text: String, additionalMessage: String) {
        if (text.isNotEmpty()) {
            placeholderMessage.visibility = View.VISIBLE
            tracks.clear()
            trackAdapter.updateTracks(tracks)
            placeholderMessage.text = text
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG).show()
            }
        } else {
            placeholderMessage.visibility = View.GONE
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(USER_TEXT, userText)
        val gson = Gson()
        val json = gson.toJson(tracks)
        outState.putString(TRACK_LIST_KEY, json)
    }

    companion object {
        const val USER_TEXT = "USER_TEXT"
        const val TRACK_LIST_KEY = "TRACK_LIST_KEY"

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        userText = savedInstanceState.getString(USER_TEXT, "")
        inputEditText.setText(userText)
        val json = savedInstanceState.getString(TRACK_LIST_KEY, "[]")
        val gson = Gson()
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        tracks = gson.fromJson(json, type)
        trackAdapter.updateTracks(tracks)
    }
}