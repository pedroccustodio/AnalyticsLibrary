package com.example.gameanalytics

class User {

    private lateinit var userId: String

    fun setUserId (id: String){
        userId = id
    }

    fun getUserId(): String {
        return userId
    }
}