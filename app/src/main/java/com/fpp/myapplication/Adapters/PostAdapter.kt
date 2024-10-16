package com.fpp.myapplication.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fpp.myapplication.Models.Post
import com.fpp.myapplication.Models.User
import com.fpp.myapplication.R
import com.fpp.myapplication.databinding.PostHomeBinding
import com.fpp.myapplication.utils.USER_NODE
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class PostAdapter(var context: Context, var postList: ArrayList<Post>) :
    RecyclerView.Adapter<PostAdapter.MyHolder>() {

    inner class MyHolder(var binding: PostHomeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        var binding = PostHomeBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {

        postList.get(position).uid?.let {
            Firebase.firestore.collection(USER_NODE).document(it).get().addOnSuccessListener {
                var user = it.toObject<User>()
                Glide.with(context).load(user!!.image).placeholder(R.drawable.user_img)
                    .into(holder.binding.profileImage)
                holder.binding.name.text = user.name

            }
        }
        Glide.with(context).load(postList.get(position).postUrl)
            .placeholder(R.drawable.loadd)
                .into(holder.binding.imageView)

        try {
            val textTime =  TimeAgo.using(postList.get(position).time!!.toLong())
            holder.binding.time.text = textTime
        }catch (e:Exception){
            holder.binding.time.text = ""
        }

        holder.binding.share.setOnClickListener {
            var i = Intent(android.content.Intent.ACTION_SEND)
            i.type="text/plain"
            i.putExtra(Intent.EXTRA_TEXT,postList.get(position).postUrl)
            context.startActivity(i)
        }


        holder.binding.caption.text = postList.get(position).caption

        var isLiked = false

        holder.binding.like.setOnClickListener {
            isLiked = !isLiked
            if (isLiked) {
                holder.binding.like.setImageResource(R.drawable.heartfill)
            } else {
                holder.binding.like.setImageResource(R.drawable.heartva)
            }
        }



    }
}