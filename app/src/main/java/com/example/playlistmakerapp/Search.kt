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

        val searchList = listOf(
            Track(R.string.trackName1.toString(), R.string.artistName1.toString(), R.string.trackTime1.toString(), R.string.artworkUrl1.toString()),
            Track(R.string.trackName2.toString(), R.string.artistName2.toString(), R.string.trackTime2.toString(), R.string.artworkUrl2.toString()),
            Track(R.string.trackName3.toString(), R.string.artistName3.toString(), R.string.trackTime3.toString(), R.string.artworkUrl3.toString()),
            Track(R.string.trackName4.toString(), R.string.artistName4.toString(), R.string.trackTime4.toString(), R.string.artworkUrl4.toString()),
            Track(R.string.trackName5.toString(), R.string.artistName5.toString(), R.string.trackTime5.toString(), R.string.artworkUrl5.toString())
        )
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