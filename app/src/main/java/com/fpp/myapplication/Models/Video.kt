package com.fpp.myapplication.Models

data class Video(
    val videoUrl: String? = null,  // Make fields nullable with default values
    val caption: String? = null,
    val profile_img: String? = null,
    val user_name: String? = null,
    val timestamp: com.google.firebase.Timestamp? = null // Adding timestamp
) {

    constructor() : this(null, null, null, null, null)
}
