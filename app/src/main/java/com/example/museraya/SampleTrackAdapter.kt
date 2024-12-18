package com.example.museraya

import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class SampleTrackAdapter(
    private val context: Context,
    private val tracks: List<String>
) : RecyclerView.Adapter<SampleTrackAdapter.SampleTrackViewHolder>() {

    private var currentMediaPlayer: MediaPlayer? = null
    private var currentPlayingPosition: Int = -1
    private var playbackListener: TurntableFragment.PlaybackListener? = null

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
                currentMediaPlayer?.pause()
                holder.playPauseButton.setImageResource(R.drawable.ic_play_arrow)
                playbackListener?.onTrackPause()
            } else {
                currentMediaPlayer?.release()
                currentPlayingPosition = currentPosition
                currentMediaPlayer = MediaPlayer.create(context, getTrackResource(currentPosition))
                currentMediaPlayer?.start()
                holder.playPauseButton.setImageResource(R.drawable.ic_pause)
                playbackListener?.onTrackPlay()

                // Update SeekBar
                currentMediaPlayer?.setOnPreparedListener {
                    holder.trackSeekBar.max = it.duration
                }
                currentMediaPlayer?.setOnCompletionListener {
                    holder.playPauseButton.setImageResource(R.drawable.ic_play_arrow)
                    playbackListener?.onTrackPause()
                }
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

    class SampleTrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val trackTitle: TextView = itemView.findViewById(R.id.trackTitle)
        val playPauseButton: ImageButton = itemView.findViewById(R.id.playPauseButton)
        val trackSeekBar: SeekBar = itemView.findViewById(R.id.trackSeekBar)
    }
}


