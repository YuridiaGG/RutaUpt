package com.example.rutaupt

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Base64
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.app.NotificationCompat

class AndroidPlatform(private val context: Context?) : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"

    private val prefs by lazy {
        context?.getSharedPreferences("ruta_upt_prefs", Context.MODE_PRIVATE)
    }

    override fun showNotification(title: String, message: String) {
        context?.let { ctx ->
            val notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channelId = "rutaupt_notifications_v3"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "RutaUPT Alerts",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Alertas de proximidad y sistema"
                    enableLights(true)
                }
                notificationManager.createNotificationChannel(channel)
            }

            val notification = NotificationCompat.Builder(ctx, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
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

    override fun openUrl(url: String) {
        context?.let { ctx ->
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                ctx.startActivity(intent)
            } catch (e: Exception) {
            }
        }
    }

    override fun exitApp() {
        (context as? Activity)?.finishAffinity() ?: (context as? android.content.ContextWrapper)?.baseContext?.let { 
            if (it is Activity) it.finishAffinity() 
        }
        System.exit(0)
    }

    override fun saveString(key: String, value: String) {
        prefs?.edit()?.putString(key, value)?.apply()
    }

    override fun getString(key: String): String? {
        return prefs?.getString(key, null)
    }

    override fun clearSettings() {
        prefs?.edit()?.clear()?.apply()
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
            val cleanBase64 = base64
                .substringAfter("base64,")
                .replace("\\s".toRegex(), "")
                .trim()

            val imageBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
            val options = BitmapFactory.Options().apply {
                inSampleSize = 2 
            }
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, options)?.asImageBitmap()
        } catch (t: Throwable) {
            null
        }
    }
}
