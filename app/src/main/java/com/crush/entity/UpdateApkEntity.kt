package com.crush.entity

data class UpdateApkEntity(
    val code: Int, // 200
    val data: Data,
    val msg: String // 操作成功
) {
    data class Data(
        val updateFlag: Boolean,//1-是 0-否
        val updateType: Int,//1-强制升级 2-非强制升级
        val appStoreUrl: String,
        val otherPlatforms: String,
        val upGradationDesc: String,

        )
}