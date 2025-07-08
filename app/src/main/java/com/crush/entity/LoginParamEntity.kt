package com.crush.entity

data class LoginParamEntity(
    val code: Int, // 200
    val data: Data,
    val msg: String // 操作成功
) {
    data class Data(
        val token: String,
        val isUserValid: Boolean,
        val userCode: String,
        val nickName: String,
        val avatarUrl: String?,
        val trafficControlEnableSwitch: Int?,//0-关闭 1-开启
    )
}