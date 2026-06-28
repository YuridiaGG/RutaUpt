package com.example.rutaupt

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import web.navigator.navigator

class JsPlatform: Platform {
    private val userAgent = navigator.userAgent
    private val browserList = listOf("Chrome", "Firefox", "Safari", "Edge")

    override val name: String = userAgent.findAnyOf(browserList, ignoreCase = true)
            ?.let { (startIndex) -> userAgent.substring(startIndex).substringBefore(" ") }
            ?: "Unknown"

    override fun showNotification(title: String, message: String) {
        // Implementación básica para web
    }

    override fun openCamera(onSuccess: (String) -> Unit) {
        // Implementación básica para web
    }
}

actual fun getPlatform(): Platform = JsPlatform()

@Composable
actual fun rememberBitmapFromBase64(base64: String?): ImageBitmap? = null
