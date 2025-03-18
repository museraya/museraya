package com.example.museraya

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class SampleTrackAdapter(
    private val context: Context,
    private val tracks: List<String>
) : RecyclerView.Adapter<SampleTrackAdapter.SampleTrackViewHolder>() {

    private var currentMediaPlayer: MediaPlayer? = null
    private var currentPlayingPosition: Int = -1
    private var playbackListener: TurntableFragment.PlaybackListener? = null
    private val handler = Handler(Looper.getMainLooper())
    private val stopPlaybackRunnable = Runnable {
        stopPlayback()
    }
    private val updateSeekBarRunnable = object : Runnable {
        override fun run() {
            currentMediaPlayer?.let { player ->
                if (player.isPlaying) {
                    val currentTime = player.currentPosition
                    val timeText = formatTime(currentTime)
                    currentSeekBar?.progress = currentTime
                    currentTimestamp?.text = timeText

                    if (currentTime < 15000) {
                        handler.postDelayed(this, 1000)
                    }
                }
            }
        }
    }

    private var currentSeekBar: SeekBar? = null
    private var currentTimestamp: TextView? = null

    // Method to set the PlaybackListener
    fun setPlaybackListener(listener: TurntableFragment.PlaybackListener) {
        playbackListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleTrackViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_sample_track, parent, false)
        return SampleTrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: SampleTrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.trackTitle.text = track

        holder.playPauseButton.setOnClickListener {
            val currentPosition = holder.adapterPosition
            if (currentPosition == RecyclerView.NO_POSITION) return@setOnClickListener

            if (currentPlayingPosition == currentPosition && currentMediaPlayer?.isPlaying == true) {
                stopPlayback()
            } else {
                playTrack(holder, currentPosition)
            }
        }

        holder.trackSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) currentMediaPlayer?.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    override fun getItemCount(): Int = tracks.size

    private fun playTrack(holder: SampleTrackViewHolder, position: Int) {
        stopPlayback() // Stop any currently playing track
        currentPlayingPosition = position
        currentSeekBar = holder.trackSeekBar
        currentTimestamp = holder.trackTimestamp

        currentMediaPlayer = MediaPlayer.create(context, getTrackResource(position)).apply {
            start()
            holder.playPauseButton.setImageResource(R.drawable.ic_pause)
            playbackListener?.onTrackPlay()

            setOnPreparedListener {
                holder.trackSeekBar.max = 15000 // Set max to 15 seconds
            }

            setOnCompletionListener {
                stopPlayback()
            }
        }

        // Start updating SeekBar and timestamp
        handler.post(updateSeekBarRunnable)

        // Schedule stopping after 15 seconds
        handler.postDelayed(stopPlaybackRunnable, 15000)
    }

    private fun stopPlayback() {
        currentMediaPlayer?.apply {
            stop()
            release()
        }
        currentMediaPlayer = null
        currentPlayingPosition = -1
        currentSeekBar?.progress = 0
        currentTimestamp?.text = "00:00"
        playbackListener?.onTrackPause()
        handler.removeCallbacks(stopPlaybackRunnable)
        handler.removeCallbacks(updateSeekBarRunnable)
        notifyDataSetChanged()
    }

    private fun getTrackResource(position: Int): Int {
        return when (position) {
            0 -> R.raw.bighunk
            1 -> R.raw.love_me
            2 -> R.raw.stuck_on_you
            3 -> R.raw.good_luck_charm
            4 -> R.raw.return_to_sender
            else -> R.raw.bighunk
        }
    }

    private fun formatTime(ms: Int): String {
        val seconds = (ms / 1000) % 60
        return String.format(Locale.getDefault(), "00:%02d", seconds)
    }

    class SampleTrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val trackTitle: TextView = itemView.findViewById(R.id.trackTitle)
        val playPauseButton: ImageButton = itemView.findViewById(R.id.playPauseButton)
        val trackSeekBar: SeekBar = itemView.findViewById(R.id.trackSeekBar)
        val trackTimestamp: TextView = itemView.findViewById(R.id.trackTimestamp)
    }
}
