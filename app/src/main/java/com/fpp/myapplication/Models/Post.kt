package com.fpp.myapplication.Models


data class Post(
    val postUrl: String? = null,
    val caption: String? = null,
    val profile_img: String? = null,
    val uid: String? = null,
    val timestamp: com.google.firebase.Timestamp? = null,
    val time:String?=null
) {
    constructor() : this(null, null, null,null,null,null)
}

