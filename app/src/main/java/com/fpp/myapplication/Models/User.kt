package com.fpp.myapplication.Models

data class User(
    var image: String? = null,
    var name: String? = null,
    var email: String? = null,
    var password: String? = null,
    var uid:String?=null


){
    constructor() : this(null, null, null,null,null)
}
