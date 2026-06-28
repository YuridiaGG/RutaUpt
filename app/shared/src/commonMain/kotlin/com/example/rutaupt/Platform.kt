package com.example.rutaupt

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

interface Platform {
    val name: String
    fun showNotification(title: String, message: String)
    fun openCamera(onSuccess: (String) -> Unit)
}

expect fun getPlatform(): Platform

@Composable
expect fun rememberBitmapFromBase64(base64: String?): ImageBitmap?
