package com.fpp.myapplication.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.fpp.myapplication.Adapters.VideoAdapter
import com.fpp.myapplication.Models.Video
import com.fpp.myapplication.R
import com.fpp.myapplication.databinding.FragmentVideoBinding
import com.fpp.myapplication.utils.VIDEO
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class VideoFragment : Fragment() {

    //change bar color for particular fragment
    override fun onResume() {
        super.onResume()
        // Change status bar color when this fragment is visible
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.black_bar)
    }

    override fun onPause() {
        super.onPause()
        // Optionally, reset status bar color when navigating away
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.app_color)
    }




    private lateinit var binding: FragmentVideoBinding
    lateinit var adapter: VideoAdapter
    var videoList = ArrayList<Video>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentVideoBinding.inflate(inflater, container, false)

        adapter = VideoAdapter(requireContext(), videoList)
        binding.viewPager.adapter = adapter
        Firebase.firestore.collection(VIDEO).orderBy("timestamp", Query.Direction.DESCENDING).get()
            .addOnSuccessListener {
                var tempList = ArrayList<Video>()
                videoList.clear()
                for (i in it.documents) {
                    var video = i.toObject<Video>()!!
                    tempList.add(video)
                }
                videoList.addAll(tempList)
                adapter.notifyDataSetChanged()
            }

        return binding.root
    }

    companion object {

    }
}