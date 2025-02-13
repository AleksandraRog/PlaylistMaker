package com.example.playlistmaker.search.data.network


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.playlistmaker.common.data.NetworkClient
import com.example.playlistmaker.common.data.dto.Response
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.getKoin
import retrofit2.Retrofit

class RetrofitNetworkClient(private val context: Context) : NetworkClient {

    private val itunsBaseUrl = "https://itunes.apple.com"

    private val retrofit: Retrofit by lazy {
        getKoin().get<Retrofit> { parametersOf(itunsBaseUrl) }
    }

    private val iTunsService = retrofit.create(RetrofitTrackApi::class.java)

    override fun doRequest(dto: Any): Response {

        if (!isConnected()) {
            return Response().apply { resultCode = -1 }
        }

        if (dto !is TracksSearchRequest) {
            return Response().apply { resultCode = 400 }
        } else {
            val resp = iTunsService.search(dto.expression).execute()
            val responseBody = resp.body() ?: Response()
            return responseBody.apply { resultCode = resp.code() }
        }
    }

    private fun isConnected(): Boolean {

        val connectivityManager = context.getSystemService( Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}
