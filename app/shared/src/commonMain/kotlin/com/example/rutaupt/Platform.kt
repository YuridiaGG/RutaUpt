package com.example.rutaupt

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform