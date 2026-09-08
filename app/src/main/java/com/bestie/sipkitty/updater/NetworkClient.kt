package com.bestie.sipkitty.updater

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object NetworkClient {
    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }
}
