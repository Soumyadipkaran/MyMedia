package com.fpp.myapplication.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.fpp.myapplication.Models.Video
import com.fpp.myapplication.databinding.MyVideoRvDesignBinding

class MyVideoRVAdapter(private var context: Context, private var videoList: ArrayList<Video>) :
    RecyclerView.Adapter<MyVideoRVAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: MyVideoRvDesignBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MyVideoRvDesignBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context)
            .load(videoList.get(position).videoUrl)
            .frame(1000000)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.binding.postVideo)
    }
}