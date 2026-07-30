package com.example.rutaupt

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
    override fun showNotification(title: String, message: String) {}
    override fun openCamera(onSuccess: (String) -> Unit) {}
    override fun openUrl(url: String) {}
}

actual fun getPlatform(): Platform = JVMPlatform()

@Composable
actual fun rememberBitmapFromBase64(base64: String?): ImageBitmap? = null
