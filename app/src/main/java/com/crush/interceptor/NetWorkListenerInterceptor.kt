package com.crush.interceptor

import com.crush.BuildConfig
import com.custom.base.http.OkHttpManager
import okhttp3.OkHttpClient

object HttpRequestInterceptor {
    fun addInterceptor() {
        if (!BuildConfig.DEBUG)
            return
        reflexGetOkHttpClient().addNetworkInterceptor(LogWithRequestInterceptor())
    }

    /**
     * 通过反射拿到okhttoclient
     */
    private fun reflexGetOkHttpClient(): OkHttpClient.Builder {
        val okHttpManager = OkHttpManager.instance
        val clazz = OkHttpManager::class.java
        val any = clazz.newInstance()
        val clientField = any.javaClass.getDeclaredField("client")
        clientField.isAccessible = true
        return clientField.get(okHttpManager) as OkHttpClient.Builder
    }
}