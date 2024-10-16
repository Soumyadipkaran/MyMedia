package com.fpp.myapplication.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fpp.myapplication.Models.Video
import com.fpp.myapplication.R
import com.fpp.myapplication.databinding.VideoDesignBinding
import com.squareup.picasso.Picasso

class VideoAdapter(var context: Context, var videoList: ArrayList<Video>) : RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: VideoDesignBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = VideoDesignBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val video = videoList[position]

        // Load user profile image
        Picasso.get().load(video.profile_img).placeholder(R.drawable.user_img).into(holder.binding.videoImage)

        // Set video caption and user name
        holder.binding.videoCaption.text = video.caption
        holder.binding.videoUserName.text = video.user_name

        // Set video path for VideoView
        holder.binding.videoView.setVideoPath(video.videoUrl)

        // Start the video once prepared
        holder.binding.videoView.setOnPreparedListener { mediaPlayer ->
            holder.binding.progressBar.visibility=View.GONE
            mediaPlayer.start()

            // Set looping to true to ensure video loops after finishing
            mediaPlayer.isLooping = true
        }

    }
}
