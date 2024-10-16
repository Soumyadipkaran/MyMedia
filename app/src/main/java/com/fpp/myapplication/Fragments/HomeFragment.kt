package com.fpp.myapplication.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.fpp.myapplication.Adapters.FollowAdapter
import com.fpp.myapplication.Adapters.PostAdapter
import com.fpp.myapplication.Models.Post
import com.fpp.myapplication.Models.User
import com.fpp.myapplication.R
import com.fpp.myapplication.databinding.FragmentHomeBinding
import com.fpp.myapplication.utils.FOLLOW
import com.fpp.myapplication.utils.POST
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    private var postList = ArrayList<Post>()
    private lateinit var adapter: PostAdapter

    private var followList = ArrayList<User>()

    private lateinit var followadapter: FollowAdapter


    override fun onResume() {
        super.onResume()
        // Change status bar color when this fragment is visible
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.app_color)
    }

    override fun onPause() {
        super.onPause()
        // Optionally, reset status bar color when navigating away
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.app_color)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Initialize PostAdapter
        adapter = PostAdapter(requireContext(), postList)
        binding.postrv.layoutManager = LinearLayoutManager(requireContext())
        binding.postrv.adapter = adapter

        // Initialize FollowAdapter
        binding.followIdRv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // Initialize followadapter before setting it to RecyclerView
        followadapter = FollowAdapter(requireContext(), followList)
        binding.followIdRv.adapter = followadapter

        setHasOptionsMenu(true)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.materialToolbar2)

        // Fetch Follow List
        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid + FOLLOW).get()
            .addOnSuccessListener {
                var tempList = ArrayList<User>()
                followList.clear()
                for (i in it.documents) {
                    var user: User = i.toObject<User>()!!
                    tempList.add(user)
                }
                followList.addAll(tempList)
                followadapter.notifyDataSetChanged()
            }

        // Fetch Posts
        val currentUserId = Firebase.auth.currentUser!!.uid
        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid + FOLLOW).get()
            .addOnSuccessListener { result ->
                val uidList = ArrayList<String>()
                uidList.add(currentUserId)


                for (document in result.documents) {
                    val uid = document.getString("uid")
                    if (uid != null) {
                        uidList.add(uid)
                    }
                }

                val allPostTasks = mutableListOf<Task<QuerySnapshot>>()

                for (uid in uidList) {
                    val postTask = Firebase.firestore.collection(uid + POST)
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .get()

                    allPostTasks.add(postTask)
                }

                Tasks.whenAllSuccess<QuerySnapshot>(allPostTasks)
                    .addOnSuccessListener { results ->
                        val tempList = ArrayList<Post>()

                        for (postResult in results) {
                            for (postDocument in postResult.documents) {
                                val post: Post = postDocument.toObject<Post>()!!
                                tempList.add(post)
                            }
                        }
                        postList.clear()
                        tempList.sortByDescending { it.timestamp }
                        postList.addAll(tempList)
                        adapter.notifyDataSetChanged()
                    }
                adapter.notifyDataSetChanged()
            }


        return binding.root
    }



    companion object {

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.option_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}