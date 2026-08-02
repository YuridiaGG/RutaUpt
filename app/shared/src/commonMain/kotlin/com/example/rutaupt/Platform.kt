package com.example.rutaupt

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

interface Platform {
    val name: String
    fun showNotification(title: String, message: String)
    fun openCamera(onSuccess: (String) -> Unit)
    fun openUrl(url: String)
    fun exitApp()
    
    // Persistencia simple
    fun saveString(key: String, value: String)
    fun getString(key: String): String?
    fun clearSettings()
}

expect fun getPlatform(): Platform

@Composable
expect fun rememberBitmapFromBase64(base64: String?): ImageBitmap?
