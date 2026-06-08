package com.example.rutaupt

interface Platform {
    val name: String
    fun showNotification(title: String, message: String)
}

expect fun getPlatform(): Platform