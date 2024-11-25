package com.example.playlistmakerapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView

class Search : AppCompatActivity() {

    private var userText: String = ""
    private lateinit var inputEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        inputEditText = findViewById(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        val buttonBack = findViewById<ImageView>(R.id.back)

        buttonBack.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
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


        val trackAdapter = TrackAdapter(
            listOf(
                Track(
                    getString(R.string.trackName1),
                    getString(R.string.artistName1),
                    getString(R.string.trackTime1),
                    getString(R.string.artworkUrl1)
                ),
                Track(
                    getString(R.string.trackName2),
                    getString(R.string.artistName2),
                    getString(R.string.trackTime2),
                    getString(R.string.artworkUrl2)
                ),
                Track(
                    getString(R.string.trackName3),
                    getString(R.string.artistName3),
                    getString(R.string.trackTime3),
                    getString(R.string.artworkUrl3)
                ),
                Track(
                    getString(R.string.trackName4),
                    getString(R.string.artistName4),
                    getString(R.string.trackTime4),
                    getString(R.string.artworkUrl4)
                ),
                Track(
                    getString(R.string.trackName5),
                    getString(R.string.artistName5),
                    getString(R.string.trackTime5),
                    getString(R.string.artworkUrl5)
                )
            )
        )
        val trackRecyclerView = findViewById<RecyclerView>(R.id.trackRecyclerView)
        trackRecyclerView.adapter = trackAdapter
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(USER_TEXT, userText)
    }

    companion object {
        const val USER_TEXT = "USER_TEXT"
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        userText = savedInstanceState.getString(USER_TEXT, "")
        inputEditText.setText(userText)
    }
}