package com.darkminstrel.weatherradar.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.Handler
import android.os.Looper

class NetworkNotifier(context:Context, onInternetAvailable:()->Unit) {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    private val handler: Handler = Handler(Looper.getMainLooper())
    private var callback:ConnectivityManager.NetworkCallback? = null

    init{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    handler.post {
                        onInternetAvailable.invoke()
                    }
                }
                override fun onLost(network: Network?) {
                }
            }.also {
                connectivityManager?.registerDefaultNetworkCallback(it)
            }
        }
    }

    fun unsubscribe(){
        callback?.let{
            connectivityManager?.unregisterNetworkCallback(it)
        }
        callback = null
    }

}