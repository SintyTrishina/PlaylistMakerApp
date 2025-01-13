package com.example.playlistmakerapp

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var buttonBack: ImageView

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        buttonBack = findViewById(R.id.back)
        buttonBack.setOnClickListener {
            finish()
        }

        val trackId = intent.getIntExtra("TRACK_ID", 0)
        val trackName = intent.getStringExtra("TRACK_NAME")
        val artistName = intent.getStringExtra("ARTIST_NAME")
        val collectionName = intent.getStringExtra("COLLECTION_NAME")
        val releaseDate = intent.getStringExtra("RELEASE_DATE")
        val primaryGenreName = intent.getStringExtra("PRIMARY_GENRE_NAME")
        val country = intent.getStringExtra("COUNTRY")
        val trackTimeMillis = intent.getLongExtra("TRACK_TIME_MILLIS", 0)
        val artworkUrl100 = intent.getStringExtra("ART_WORK_URL")

        val track = Track(
            trackId = trackId,
            trackName = trackName ?: "",
            artistName = artistName ?: "",
            trackTimeMillis = trackTimeMillis,
            artworkUrl100 = artworkUrl100 ?: "",
            collectionName = collectionName ?: "",
            releaseDate = releaseDate ?: "",
            primaryGenreName = primaryGenreName ?: "",
            country = country ?: "",
        )


        val artworkUrlView = findViewById<ImageView>(R.id.imageMusic)
        findViewById<TextView>(R.id.trackName).text = track.trackName
        findViewById<TextView>(R.id.artistName).text = track.artistName
        val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        val formattedTime = dateFormat.format(track.trackTimeMillis)
        findViewById<TextView>(R.id.trackTime).text = formattedTime

        val collectionNameView = findViewById<TextView>(R.id.collectionName)
        if (collectionName == null) {
            collectionNameView.visibility = View.GONE
        }
        collectionNameView.text = track.collectionName

        val year = releaseDate?.substring(0, 4)
        findViewById<TextView>(R.id.releaseDate).text = year

        findViewById<TextView>(R.id.primaryGenreName).text = track.primaryGenreName
        findViewById<TextView>(R.id.country).text = track.country

        val cornerRadius = dpToPx(8f, this)
        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(cornerRadius))
            .into(artworkUrlView)
    }
}