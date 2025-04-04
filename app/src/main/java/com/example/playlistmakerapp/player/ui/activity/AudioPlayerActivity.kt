package com.example.playlistmakerapp.player.ui.activity

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmakerapp.R
import com.example.playlistmakerapp.databinding.ActivityAudioPlayerBinding
import com.example.playlistmakerapp.player.ui.viewmodel.AudioPlayerState
import com.example.playlistmakerapp.player.ui.viewmodel.AudioPlayerViewModel
import com.example.playlistmakerapp.search.domain.models.Track
import com.example.playlistmakerapp.util.Constants
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding
    private val viewModel by viewModel<AudioPlayerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            finish()
        }

        // Получение данных о треке
        val track = getTrackFromIntent()

        // Настройка UI
        setupTrackInfo(track)

        // Подготовка плеера
        viewModel.setDataSource(track.previewUrl)

        // Наблюдение за состоянием экрана
        viewModel.playerState.observe(this) { state ->
            updateUI(state)
        }

        // Обработка нажатия на кнопку воспроизведения
        binding.buttonPlay.setOnClickListener {
            viewModel.playbackControl()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    private fun getTrackFromIntent(): Track {
        return Track(
            trackId = intent.getIntExtra(Constants.TRACK_ID, 0),
            trackName = intent.getStringExtra(Constants.TRACK_NAME) ?: "",
            artistName = intent.getStringExtra(Constants.ARTIST_NAME) ?: "",
            trackTimeMillis = intent.getLongExtra(Constants.TRACK_TIME_MILLIS, 0),
            artworkUrl100 = intent.getStringExtra(Constants.ART_WORK_URL) ?: "",
            collectionName = intent.getStringExtra(Constants.COLLECTION_NAME) ?: "",
            releaseDate = intent.getStringExtra(Constants.RELEASE_DATE) ?: "",
            primaryGenreName = intent.getStringExtra(Constants.PRIMARY_GENRE_NAME) ?: "",
            country = intent.getStringExtra(Constants.COUNTRY) ?: "",
            previewUrl = intent.getStringExtra(Constants.PREVIEW_URL) ?: ""
        )
    }

    private fun setupTrackInfo(track: Track) {
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(track.trackTimeMillis)

        if (track.collectionName.isEmpty()) {
            binding.collectionName.visibility = View.GONE
        } else {
            binding.collectionName.text = track.collectionName
        }

        val year = track.releaseDate.substring(0, 4)
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
    }

    private fun updateUI(state: AudioPlayerState) {
        when (state) {
            is AudioPlayerState.Default -> {
                binding.buttonPlay.isEnabled = state.isPlayButtonEnabled
            }

            is AudioPlayerState.Prepared -> {
                binding.buttonPlay.isEnabled = state.isPlayButtonEnabled
                binding.buttonPlay.setImageResource(R.drawable.black_button)
                binding.timePlay.setText(R.string.timer)
            }

            is AudioPlayerState.Playing -> {
                binding.buttonPlay.isEnabled = state.isPlayButtonEnabled
                binding.buttonPlay.setImageResource(R.drawable.pause_button)
                binding.timePlay.text = state.currentPosition
            }

            is AudioPlayerState.Paused -> {
                binding.buttonPlay.isEnabled = state.isPlayButtonEnabled
                binding.buttonPlay.setImageResource(R.drawable.black_button)
                binding.timePlay.text = state.currentPosition
            }
        }
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}