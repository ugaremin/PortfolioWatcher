package com.ugaremin.portfoliowatcher.Utilities

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest

class NetworkMonitorService(context: Context, val onNetworkChanged: (Boolean) -> Unit) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            // İnternet bağlantısı mevcut
            onNetworkChanged(true)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            // İnternet bağlantısı kayboldu
            onNetworkChanged(false)
        }
    }

    init {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        // İlk başta internet bağlantısını kontrol et
        val isConnected = isNetworkAvailable(context)
        onNetworkChanged(isConnected)
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    fun stopNetworkCallback() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}
