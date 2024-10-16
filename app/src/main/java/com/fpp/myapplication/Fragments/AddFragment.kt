package com.fpp.myapplication.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fpp.myapplication.Post.PicturePostActivity
import com.fpp.myapplication.Post.VideoPostActivity
import com.fpp.myapplication.databinding.FragmentAddBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAddBinding.inflate(inflater, container, false)
        binding.addImg.setOnClickListener {
            activity?.startActivity(Intent(requireContext(),PicturePostActivity::class.java))
        }
        binding.addVideo.setOnClickListener {
            activity?.startActivity(Intent(requireContext(),VideoPostActivity::class.java))
        }
        return binding.root
    }

    companion object {

    }
}