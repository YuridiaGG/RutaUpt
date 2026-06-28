package com.example.rutaupt

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Base64
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.app.NotificationCompat

object CameraBridge {
    var onLaunchCamera: ((onResult: (String) -> Unit) -> Unit)? = null
}

class AndroidPlatform(private val context: Context?) : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"

    override fun showNotification(title: String, message: String) {
        context?.let { ctx ->
            val notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channelId = "rutaupt_notifications"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "RutaUPT Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }

            val notification = NotificationCompat.Builder(ctx, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()

            notificationManager.notify(System.currentTimeMillis().toInt(), notification)
        }
    }

    override fun openCamera(onSuccess: (String) -> Unit) {
        val launcher = CameraBridge.onLaunchCamera
        if (launcher != null) {
            launcher(onSuccess)
        }
    }
}

private var appContext: Context? = null

fun initPlatform(context: Context) {
    appContext = context
}

actual fun getPlatform(): Platform = AndroidPlatform(appContext)

@Composable
actual fun rememberBitmapFromBase64(base64: String?): ImageBitmap? {
    return remember(base64) {
        if (base64.isNullOrBlank()) return@remember null
        try {
            // Limpieza extrema: quitar prefijos data: y eliminar cualquier espacio/salto de línea
            val cleanBase64 = base64
                .substringAfter("base64,")
                .replace("\\s".toRegex(), "")
                .trim()

            val imageBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }
}
