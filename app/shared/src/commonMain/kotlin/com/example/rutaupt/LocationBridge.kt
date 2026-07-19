package com.example.rutaupt

object LocationBridge {
    var onRequestPermission: ((onResult: (Boolean) -> Unit) -> Unit)? = null
    var hasPermission: (() -> Boolean)? = null
}
