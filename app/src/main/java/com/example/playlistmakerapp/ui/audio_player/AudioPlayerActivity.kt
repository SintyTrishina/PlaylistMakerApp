package com.example.playlistmakerapp.ui.audio_player

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmakerapp.R
import com.example.playlistmakerapp.databinding.ActivityAudioPlayerBinding
import com.example.playlistmakerapp.domain.models.Track
import com.example.playlistmakerapp.ui.Constants
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding
    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private var previewUrl: String? = null
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            finish()
        }

        val trackId = intent.getIntExtra(Constants.TRACK_ID, 0)
        val trackName = intent.getStringExtra(Constants.TRACK_NAME)
        val artistName = intent.getStringExtra(Constants.ARTIST_NAME)
        val collectionName = intent.getStringExtra(Constants.COLLECTION_NAME)
        val releaseDate = intent.getStringExtra(Constants.RELEASE_DATE)
        val primaryGenreName = intent.getStringExtra(Constants.PRIMARY_GENRE_NAME)
        val country = intent.getStringExtra(Constants.COUNTRY)
        val trackTimeMillis = intent.getLongExtra(Constants.TRACK_TIME_MILLIS, 0)
        val artworkUrl100 = intent.getStringExtra(Constants.ART_WORK_URL)
        previewUrl = intent.getStringExtra(Constants.PREVIEW_URL)


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
            previewUrl = previewUrl ?: "",
        )

        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName

        val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        val formattedTime = dateFormat.format(track.trackTimeMillis)
        binding.trackTime.text = formattedTime

        if (collectionName == null) {
            binding.collectionName.visibility = View.GONE
        }
        binding.collectionName.text = track.collectionName

        val year = releaseDate?.substring(0, 4)
        binding.releaseDate.text = year

        binding.primaryGenreName.text = track.primaryGenreName
        binding.country.text = track.country

        val cornerRadius = dpToPx(8f, this)
        Glide.with(binding.imageMusic.context)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(cornerRadius))
            .into(binding.imageMusic)


        binding.buttonPlay.isEnabled = false
        preparePlayer()

        binding.buttonPlay.setOnClickListener {
            playbackControl()
        }

    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updatingTime)
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updatingTime)
        mediaPlayer.release()
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            binding.buttonPlay.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            binding.buttonPlay.setImageResource(R.drawable.black_button)
            handler.removeCallbacks(updatingTime)
            binding.timePlay.setText(R.string.timer)
        }
    }

    private val updatingTime = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING) {
                binding.timePlay.text =
                    SimpleDateFormat(
                        "mm:ss",
                        Locale.getDefault()
                    ).format(mediaPlayer.currentPosition)
                handler.postDelayed(this, UPDATE_TIME)
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        binding.buttonPlay.setImageResource(R.drawable.pause_button)
        playerState = STATE_PLAYING
        handler.post(updatingTime)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        binding.buttonPlay.setImageResource(R.drawable.black_button)
        playerState = STATE_PAUSED
        handler.removeCallbacks(updatingTime)
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val UPDATE_TIME = 300L
    }
}