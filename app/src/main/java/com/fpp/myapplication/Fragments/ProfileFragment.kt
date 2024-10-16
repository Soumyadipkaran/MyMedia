package com.fpp.myapplication.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fpp.myapplication.Adapters.ViewPagerAdapter
import com.fpp.myapplication.Models.User
import com.fpp.myapplication.SignUpActivity
import com.fpp.myapplication.databinding.FragmentProfileBinding
import com.fpp.myapplication.utils.USER_NODE
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.google.firebase.auth.FirebaseAuth
import com.fpp.myapplication.LoginActivity

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.editProfile.setOnClickListener {
            val intent = Intent(activity, SignUpActivity::class.java)
            intent.putExtra("MODE", 1) // Profile update mode
            activity?.startActivity(intent)
            activity?.finish()
        }

        // Setup ViewPager for posts and reels
        viewPagerAdapter = ViewPagerAdapter(requireActivity().supportFragmentManager)
        viewPagerAdapter.addFragments(MyPostFragment(), "MY POST")
        viewPagerAdapter.addFragments(MyVideoFragment(), "MY VIDEOS")
        binding.viewPager.adapter = viewPagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        // Fetch user data from Firestore
        Firebase.firestore.collection(USER_NODE)
            .document(Firebase.auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener {
                val user: User = it.toObject<User>()!!

                // Display user details
                binding.name.text = user.name // Display the bio

                if (!user.image.isNullOrEmpty()) {
                    Picasso.get().load(user.image).into(binding.profileImage)
                }
            }
            .addOnFailureListener {
                // Handle error if needed
            }

        binding.Logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }
    }
}
