package com.fpp.myapplication.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fpp.myapplication.databinding.MyPostRvDesignBinding
import android.content.Context
import com.fpp.myapplication.Models.Post
import com.fpp.myapplication.R
import com.squareup.picasso.Picasso

class MyPostRVAdapter(private var context: Context, private var postList: ArrayList<Post>) :
    RecyclerView.Adapter<MyPostRVAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: MyPostRvDesignBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MyPostRvDesignBinding.inflate(LayoutInflater.from(context), parent, false)


        return ViewHolder(binding)


    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {



        val post = postList[position]

        // Check if postUrl is not null or empty before loading with Picasso
        if (!post.postUrl.isNullOrEmpty()) {
            Picasso.get()
                .load(post.postUrl)
                .into(holder.binding.postImage)
        } else {
            holder.binding.postImage.setImageResource(R.drawable.ic_launcher_background) // Replace with your actual placeholder resource
        }
    }
}