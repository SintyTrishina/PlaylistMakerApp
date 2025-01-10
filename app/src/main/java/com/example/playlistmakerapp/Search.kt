package com.example.playlistmakerapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val SEARCH_HISTORY = "search_history"

class Search : AppCompatActivity() {

    private var userText: String = ""
    private lateinit var inputEditText: EditText
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderImage: ImageView
    private lateinit var updateButton: Button
    private lateinit var cleanSearchButton: Button
    private lateinit var textSearch: TextView
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var searchHistory: SearchHistory
    private var tracks = ArrayList<Track>()
    private lateinit var trackAdapter: TrackAdapter
    private val trackService = RetrofitClient.trackService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        inputEditText = findViewById(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        val buttonBack = findViewById<ImageView>(R.id.back)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        placeholderImage = findViewById(R.id.placeholderImage)
        updateButton = findViewById(R.id.updateButton)
        val trackRecyclerView = findViewById<RecyclerView>(R.id.trackRecyclerView)
        cleanSearchButton = findViewById(R.id.searchHistoryCleanButton)
        textSearch = findViewById(R.id.searchHint)

        //СОЗДАЕМ ЭКЗЕМПЛЯР SP
        sharedPrefs = getSharedPreferences(SEARCH_HISTORY, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPrefs)

        //СОЗДАЕМ ЭКЗЕМПЛЯР АДАПТЕРА
        trackAdapter = TrackAdapter() {
            searchHistory.addTrack(it)
            val intentAudioplayer = Intent(this, AudioPlayer::class.java)
            startActivity(intentAudioplayer)
        }
        //ПЕРЕДАЕМ АДАПТЕРУ SP
        trackAdapter.initSharedPrefs(sharedPrefs)
        trackRecyclerView.adapter = trackAdapter
        searchHistory.loadHistoryFromPrefs()

        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty()) {
                showSearchHistory()
            } else {
                hideSearchHistory()
            }
        }

        buttonBack.setOnClickListener {
            finish()
        }

        cleanSearchButton.setOnClickListener {
            searchHistory.cleanHistory()
            hideSearchHistory()
        }

        clearButton.setOnClickListener {
            tracks.clear()
            inputEditText.setText("")
            searchHistory.loadHistoryFromPrefs()
            showSearchHistory()
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
                if (inputEditText.hasFocus() && s.isNullOrEmpty()) {
                    showSearchHistory()
                } else {
                    hideSearchHistory()
                }

                if (s.isNullOrEmpty()) {
                    placeholderImage.visibility = View.GONE
                    placeholderMessage.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                userText = s.toString()
            }
        }

        inputEditText.addTextChangedListener(textWatcher)

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
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
                                tracks.addAll(response.body()?.results!!)
                                showTracks()
                            }
                            if (tracks.isEmpty()) {

                                placeholderImage.setImageResource(R.drawable.error)
                                placeholderImage.visibility = View.VISIBLE
                                updateButton.visibility = View.GONE
                                textSearch.visibility = View.GONE
                                cleanSearchButton.visibility = View.GONE
                                showMessage(getString(R.string.nothing_found), "")
                            } else {
                                placeholderImage.visibility = View.GONE
                                updateButton.visibility = View.GONE
                                textSearch.visibility = View.GONE
                                cleanSearchButton.visibility = View.GONE
                                showMessage("", "")
                            }
                        } else {
                            placeholderImage.setImageResource(R.drawable.errorconnection)
                            placeholderImage.visibility = View.VISIBLE
                            updateButton.visibility = View.VISIBLE
                            textSearch.visibility = View.GONE
                            cleanSearchButton.visibility = View.GONE
                            showMessage(
                                getString(R.string.something_went_wrong), response.code().toString()
                            )
                        }
                    }

                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                        placeholderImage.setImageResource(R.drawable.errorconnection)
                        placeholderImage.visibility = View.VISIBLE
                        updateButton.visibility = View.VISIBLE
                        textSearch.visibility = View.GONE
                        cleanSearchButton.visibility = View.GONE
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

    private fun showSearchHistory() {
        val history = searchHistory.getHistory()
        if (history.isNotEmpty()) {
            textSearch.visibility = View.VISIBLE
            cleanSearchButton.visibility = View.VISIBLE
            placeholderImage.visibility = View.GONE
            updateButton.visibility = View.GONE
            trackAdapter.updateTracks(history)
        } else {
            hideSearchHistory()
        }
    }

    private fun hideSearchHistory() {
        textSearch.visibility = View.GONE
        cleanSearchButton.visibility = View.GONE
        placeholderImage.visibility = View.GONE
        updateButton.visibility = View.GONE
        placeholderMessage.visibility = View.GONE
        trackAdapter.updateTracks(ArrayList())
    }

    private fun showTracks() {
        if (tracks.isNotEmpty()) {
            trackAdapter.updateTracks(tracks)
            textSearch.visibility = View.GONE
            cleanSearchButton.visibility = View.GONE
            placeholderImage.visibility = View.GONE
            placeholderMessage.visibility = View.GONE
            updateButton.visibility = View.GONE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(USER_TEXT, userText)

        val json = Gson().toJson(tracks)
        outState.putString(TRACK_LIST_KEY, json)

        searchHistory.getHistory()
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
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        tracks = Gson().fromJson(json, type)
        trackAdapter.updateTracks(tracks)

        searchHistory.loadHistoryFromPrefs()
        showSearchHistory()
    }
}