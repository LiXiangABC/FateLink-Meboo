package com.crush.ui.start

data class AppInfoEntity(
    val code: Int, // 0
    val `data`: Data,
    val msg: String
) {
    data class Data(
        val appId: String,
        val pushToken: String
    )
}