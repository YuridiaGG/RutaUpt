package com.example.rutaupt

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.rutaupt.App
import java.io.ByteArrayOutputStream
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import android.location.Location
import android.os.Looper

class MainActivity : ComponentActivity() {
    
    private var cameraCallback: ((String) -> Unit)? = null
    private var locationPermissionCallback: ((Boolean) -> Unit)? = null
    private var cameraPermissionCallback: ((Boolean) -> Unit)? = null
    private lateinit var locationCallback: LocationCallback

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val base64Image = encodeBitmapToBase64(bitmap)
            cameraCallback?.invoke(base64Image)
            cameraCallback = null
        }
    }

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        cameraPermissionCallback?.invoke(isGranted)
        cameraPermissionCallback = null
        if (isGranted && cameraCallback != null) {
            takePictureLauncher.launch(null)
        }
    }

    private val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        locationPermissionCallback?.invoke(isGranted)
        locationPermissionCallback = null
    }

    private fun encodeBitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        initPlatform(this)

        CameraBridge.onLaunchCamera = { callback ->
            cameraCallback = callback
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                takePictureLauncher.launch(null)
            } else {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        CameraBridge.hasPermission = {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        }

        CameraBridge.onRequestPermission = { callback ->
            cameraPermissionCallback = callback
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        LocationBridge.hasPermission = {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }

        LocationBridge.onRequestPermission = { callback ->
            locationPermissionCallback = callback
            requestLocationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
        
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        
        // 1. Obtener ubicación una sola vez
        LocationBridge.getCurrentLocation = { callback ->
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener { location: Location? ->
                        location?.let { callback(it.latitude, it.longitude) }
                    }
            }
        }

        // 2. Rastreo en TIEMPO REAL (Como WhatsApp)
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setMinUpdateIntervalMillis(500)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let {
                    LocationBridge.onLocationUpdate?.invoke(it.latitude, it.longitude)
                }
            }
        }

        LocationBridge.startLocationUpdates = {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
            }
        }

        LocationBridge.stopLocationUpdates = {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }

        setContent {
            App()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocationBridge.stopLocationUpdates?.invoke()
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
