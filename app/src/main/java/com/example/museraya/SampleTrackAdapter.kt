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

    private val stopPlaybackRunnable = Runnable { stopPlayback() }

    private val updateSeekBarRunnable = object : Runnable {
        override fun run() {
            currentMediaPlayer?.let { player ->
                if (player.isPlaying) {
                    findViewHolderForAdapterPosition(currentPlayingPosition)?.let { holder ->
                        val currentTime = player.currentPosition
                        if (currentTime <= 15000) {
                            holder.trackSeekBar.progress = currentTime
                            holder.trackTimestamp.text = formatTime(currentTime)
                        }
                    }
                    handler.postDelayed(this, 500)
                }
            }
        }
    }

    private var recyclerView: RecyclerView? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
        stopPlayback()
    }

    fun setPlaybackListener(listener: TurntableFragment.PlaybackListener) {
        playbackListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleTrackViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_sample_track, parent, false)
        return SampleTrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: SampleTrackViewHolder, position: Int) {
        holder.trackTitle.text = tracks[position]
        holder.trackSeekBar.max = 15000

        if (position == currentPlayingPosition) {
            // This is the active track, check if it's currently playing or paused
            if (currentMediaPlayer?.isPlaying == true) {
                holder.playPauseButton.setImageResource(R.drawable.ic_pause)
            } else {
                holder.playPauseButton.setImageResource(R.drawable.ic_play)
            }
            holder.trackSeekBar.progress = currentMediaPlayer?.currentPosition ?: 0
            holder.trackTimestamp.text = formatTime(currentMediaPlayer?.currentPosition ?: 0)
        } else {
            // This is an inactive track, reset its UI
            holder.playPauseButton.setImageResource(R.drawable.ic_play)
            holder.trackSeekBar.progress = 0
            holder.trackTimestamp.text = "00:00"
        }

        holder.playPauseButton.setOnClickListener {
            val clickedPosition = holder.adapterPosition
            if (clickedPosition == RecyclerView.NO_POSITION) return@setOnClickListener

            if (clickedPosition == currentPlayingPosition) {
                // Clicked on the currently active track
                if (currentMediaPlayer?.isPlaying == true) {
                    pauseTrack()
                } else {
                    resumeTrack()
                }
            } else {
                // Clicked on a new track
                playTrack(clickedPosition)
            }
        }

        holder.trackSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && holder.adapterPosition == currentPlayingPosition) {
                    currentMediaPlayer?.seekTo(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    override fun getItemCount(): Int = tracks.size

    private fun playTrack(position: Int) {
        stopPlayback() // Fully stop and release any previous track
        currentPlayingPosition = position
        currentMediaPlayer = MediaPlayer.create(context, getTrackResource(position)).apply {
            setOnCompletionListener { stopPlayback() }
            start()
        }
        playbackListener?.onTrackPlay()
        notifyItemChanged(position)
        handler.post(updateSeekBarRunnable)
        handler.postDelayed(stopPlaybackRunnable, 15000)
    }

    private fun pauseTrack() {
        currentMediaPlayer?.pause()
        // Stop the timeout and seekbar updaters
        handler.removeCallbacks(stopPlaybackRunnable)
        handler.removeCallbacks(updateSeekBarRunnable)
        playbackListener?.onTrackPause()
        // Update the UI to show the 'play' icon
        notifyItemChanged(currentPlayingPosition)
    }

    private fun resumeTrack() {
        currentMediaPlayer?.start()
        playbackListener?.onTrackPlay()
        handler.post(updateSeekBarRunnable)
        // Recalculate remaining time and restart the timeout handler
        val remainingTime = 15000 - (currentMediaPlayer?.currentPosition ?: 0)
        if (remainingTime > 0) {
            handler.postDelayed(stopPlaybackRunnable, remainingTime.toLong())
        }
        // Update the UI to show the 'pause' icon
        notifyItemChanged(currentPlayingPosition)
    }

    private fun stopPlayback() {
        // Cancel any pending operations
        handler.removeCallbacks(stopPlaybackRunnable)
        handler.removeCallbacks(updateSeekBarRunnable)

        currentMediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        currentMediaPlayer = null

        val previouslyPlayingPosition = currentPlayingPosition
        currentPlayingPosition = -1

        playbackListener?.onTrackPause()

        if (previouslyPlayingPosition != -1) {
            // Update the UI of the stopped track to its reset state
            notifyItemChanged(previouslyPlayingPosition)
        }
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

    private fun findViewHolderForAdapterPosition(position: Int): SampleTrackViewHolder? {
        return recyclerView?.findViewHolderForAdapterPosition(position) as? SampleTrackViewHolder
    }

    class SampleTrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val trackTitle: TextView = itemView.findViewById(R.id.trackTitle)
        val playPauseButton: ImageButton = itemView.findViewById(R.id.playPauseButton)
        val trackSeekBar: SeekBar = itemView.findViewById(R.id.trackSeekBar)
        val trackTimestamp: TextView = itemView.findViewById(R.id.trackTimestamp)
    }
}