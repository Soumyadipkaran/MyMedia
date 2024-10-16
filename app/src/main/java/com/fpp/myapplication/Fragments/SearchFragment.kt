package com.fpp.myapplication.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.fpp.myapplication.Adapters.SearchAdapter
import com.fpp.myapplication.Models.User
import com.fpp.myapplication.databinding.FragmentSearchBinding
import com.fpp.myapplication.utils.USER_NODE
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import androidx.appcompat.widget.SearchView // Ensure this import is correct

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: SearchAdapter
    private var userList = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        // Set up RecyclerView
        binding.searchrv.layoutManager = LinearLayoutManager(requireContext())
        adapter = SearchAdapter(requireContext(), userList)
        binding.searchrv.adapter = adapter

        // Load all users from Firestore initially
        loadAllUsers()

        // Set OnQueryTextListener for SearchView
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Perform the Firestore query for specific user
                if (!query.isNullOrEmpty()) {
                    Firebase.firestore.collection(USER_NODE)
                        .whereEqualTo("name", query)
                        .get()
                        .addOnSuccessListener { snapshot ->
                            val tempList = ArrayList<User>()
                            userList.clear()
                            if (!snapshot.isEmpty) {
                                for (document in snapshot.documents) {
                                    if (document.id != Firebase.auth.currentUser!!.uid) {
                                        val user: User = document.toObject<User>()!!
                                        tempList.add(user)
                                    }
                                }
                            }
                            userList.addAll(tempList)
                            adapter.notifyDataSetChanged()
                        }
                }
                return true // Return true to indicate the query was handled
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    // Load all users if the search view is cleared
                    loadAllUsers()
                }
                return false // Return false if you don't want to handle further changes
            }
        })

        return binding.root
    }

    private fun loadAllUsers() {
        Firebase.firestore.collection(USER_NODE).get().addOnSuccessListener { documents ->
            val tempList = ArrayList<User>()
            userList.clear()
            for (document in documents) {
                if (document.id != Firebase.auth.currentUser!!.uid) {
                    val user: User = document.toObject<User>()!!
                    tempList.add(user)
                }
            }
            userList.addAll(tempList)
            adapter.notifyDataSetChanged()
        }
    }
}
