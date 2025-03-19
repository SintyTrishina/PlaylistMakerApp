package com.example.playlistmakerapp.player.ui.activity

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmakerapp.R
import com.example.playlistmakerapp.databinding.ActivityAudioPlayerBinding
import com.example.playlistmakerapp.player.ui.viewmodel.AudioPlayerViewModel
import com.example.playlistmakerapp.search.domain.models.Track
import com.example.playlistmakerapp.util.Constants
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding
    private val viewModel: AudioPlayerViewModel by viewModels()
//    private var mediaPlayer = MediaPlayer()
//    private var playerState = STATE_DEFAULT
//    private var previewUrl: String? = null
//    private val handler = Handler(Looper.getMainLooper())

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
        val previewUrl = intent.getStringExtra(Constants.PREVIEW_URL)


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
        binding.trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(track.trackTimeMillis)

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


//        binding.buttonPlay.isEnabled = false
        viewModel.setDataSource(track.previewUrl)

        viewModel.isPlayButtonEnabled.observe(this) { isEnabled ->
            binding.buttonPlay.isEnabled = isEnabled
        }

        viewModel.playerState.observe(this) { state ->
            when (state) {
                AudioPlayerViewModel.STATE_PLAYING -> {
                    binding.buttonPlay.setImageResource(R.drawable.pause_button)
                }
                AudioPlayerViewModel.STATE_PAUSED -> {
                    binding.buttonPlay.setImageResource(R.drawable.black_button)
                }
                AudioPlayerViewModel.STATE_PREPARED -> {
                    binding.buttonPlay.setImageResource(R.drawable.black_button)
                    binding.timePlay.setText(R.string.timer)
                }
            }
        }

        viewModel.currentPosition.observe(this) { position ->
            binding.timePlay.text = position
        }

        // Обработка нажатия на кнопку воспроизведения
        binding.buttonPlay.setOnClickListener {
            viewModel.playbackControl()
        }

    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
//        handler.removeCallbacks(updatingTime)
//        pausePlayer()
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

//    private fun preparePlayer() {
////        mediaPlayer.setDataSource(previewUrl)
////        mediaPlayer.prepareAsync()
////        mediaPlayer.setOnPreparedListener {
////            binding.buttonPlay.isEnabled = true
////            playerState = STATE_PREPARED
////        }
//        mediaPlayer.setOnCompletionListener {
////            playerState = STATE_PREPARED
//            binding.buttonPlay.setImageResource(R.drawable.black_button)
////            handler.removeCallbacks(updatingTime)
//            binding.timePlay.setText(R.string.timer)
//        }
//    }
//
//    private val updatingTime = object : Runnable {
////        override fun run() {
////            if (playerState == STATE_PLAYING) {
//                binding.timePlay.text =
////                    SimpleDateFormat(
////                        "mm:ss",
////                        Locale.getDefault()
////                    ).format(mediaPlayer.currentPosition)
////                handler.postDelayed(this, UPDATE_TIME)
//            }
//        }
//    }
//
//    private fun startPlayer() {
////        mediaPlayer.start()
//        binding.buttonPlay.setImageResource(R.drawable.pause_button)
////        playerState = STATE_PLAYING
////        handler.post(updatingTime)
//    }
//
//    private fun pausePlayer() {
////        mediaPlayer.pause()
//        binding.buttonPlay.setImageResource(R.drawable.black_button)
////        playerState = STATE_PAUSED
////        handler.removeCallbacks(updatingTime)
//    }
//
////    private fun playbackControl() {
////        when (playerState) {
////            STATE_PLAYING -> {
////                pausePlayer()
////            }
////
////            STATE_PREPARED, STATE_PAUSED -> {
////                startPlayer()
////            }
////        }
////    }
////
////    companion object {
////        private const val STATE_DEFAULT = 0
////        private const val STATE_PREPARED = 1
////        private const val STATE_PLAYING = 2
////        private const val STATE_PAUSED = 3
////        private const val UPDATE_TIME = 300L
////    }
}