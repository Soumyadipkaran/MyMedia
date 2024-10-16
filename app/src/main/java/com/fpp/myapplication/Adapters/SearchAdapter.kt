package com.fpp.myapplication.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fpp.myapplication.Models.User
import com.fpp.myapplication.R
import com.fpp.myapplication.databinding.SearchRvBinding
import com.fpp.myapplication.utils.FOLLOW
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SearchAdapter(var context: Context, var userList: ArrayList<User>) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: SearchRvBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SearchRvBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]

        // Load user profile image
        Glide.with(context)
            .load(user.image)
            .placeholder(R.drawable.user_img)
            .into(holder.binding.profileImage)

        // Set user name
        holder.binding.name.text = user.name




        // Check follow status
        Firebase.firestore.collection("${Firebase.auth.currentUser!!.uid}$FOLLOW")
            .whereEqualTo("email", user.email)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    holder.binding.follow.text = "Follow"
                } else {
                    holder.binding.follow.text = "Unfollow"
                }
            }



        holder.binding.follow.setOnClickListener {
            when (holder.binding.follow.text) {
                "Follow" -> {
                    Firebase.firestore.collection("${Firebase.auth.currentUser!!.uid}$FOLLOW")
                        .document()
                        .set(user)
                        .addOnSuccessListener {
                            holder.binding.follow.text = "Unfollow"
                        }
                }
                "Unfollow" -> {
                    Firebase.firestore.collection("${Firebase.auth.currentUser!!.uid}$FOLLOW")
                        .whereEqualTo("email", user.email)
                        .get()
                        .addOnSuccessListener { documents ->
                            if (!documents.isEmpty()) {
                                Firebase.firestore.collection("${Firebase.auth.currentUser!!.uid}$FOLLOW")
                                    .document(documents.first().id)
                                    .delete()
                                    .addOnSuccessListener {
                                        holder.binding.follow.text = "Follow"
                                    }
                            }
                        }
                }


            }




        }
    }
}
