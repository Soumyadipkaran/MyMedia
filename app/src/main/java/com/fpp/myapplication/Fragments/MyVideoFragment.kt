package com.fpp.myapplication.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.fpp.myapplication.Adapters.MyVideoRVAdapter
import com.fpp.myapplication.Models.Video
import com.fpp.myapplication.databinding.FragmentMyVideosBinding
import com.fpp.myapplication.utils.VIDEO
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class MyVideoFragment : Fragment() {
    private lateinit var binding: FragmentMyVideosBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMyVideosBinding.inflate(inflater, container, false)
        val videoList = ArrayList<Video>()
        val adapter = MyVideoRVAdapter(requireContext(), videoList)

        binding.rv.layoutManager =
            StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        binding.rv.adapter = adapter

        // Fetch videos from Firestore
        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid + VIDEO)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                videoList.clear()
                for (document in result.documents) {
                    document.toObject<Video>()?.let { video ->
                        // Add the new video at the top of the list
                        videoList.add(video)
                    }
                }
                adapter.notifyDataSetChanged()
            }

        return binding.root
    }
}
