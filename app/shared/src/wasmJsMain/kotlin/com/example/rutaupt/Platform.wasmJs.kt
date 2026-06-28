package com.example.rutaupt

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
    override fun showNotification(title: String, message: String) {}
    override fun openCamera(onSuccess: (String) -> Unit) {}
}

actual fun getPlatform(): Platform = WasmPlatform()

@Composable
actual fun rememberBitmapFromBase64(base64: String?): ImageBitmap? = null
