package com.fpp.myapplication.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.fpp.myapplication.Adapters.MyPostRVAdapter
import com.fpp.myapplication.Models.Post
import com.fpp.myapplication.databinding.FragmentMyPostBinding
import com.fpp.myapplication.utils.POST
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class My_Post_Details : Fragment() {
    private lateinit var binding: FragmentMyPostBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMyPostBinding.inflate(inflater, container, false)

        // Initialize postList and adapter
        val postList = ArrayList<Post>()
        val adapter = MyPostRVAdapter(requireContext(), postList)


        binding.rv.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        binding.rv.adapter = adapter

        // Fetch posts from Firestore
        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid + POST)
            .orderBy("timestamp", Query.Direction.DESCENDING) // Order by timestamp in descending order
            .get()
            .addOnSuccessListener { result ->
                postList.clear()
                for (document in result.documents) {
                    document.toObject<Post>()?.let { post ->
                        postList.add(post) // Add the new post at the end of the list
                    }
                }
                adapter.notifyDataSetChanged() // Notify adapter of data change
            }
            .addOnFailureListener { e ->
                Log.e("FetchPosts", "Error fetching posts: ", e)
            }

        return binding.root
    }
}
