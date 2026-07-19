package com.example.rutaupt

object CameraBridge {
    var onLaunchCamera: ((onResult: (String) -> Unit) -> Unit)? = null
    var onRequestPermission: ((onResult: (Boolean) -> Unit) -> Unit)? = null
    var hasPermission: (() -> Boolean)? = null
}
