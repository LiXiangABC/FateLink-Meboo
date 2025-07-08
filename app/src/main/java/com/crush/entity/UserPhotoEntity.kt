package com.crush.entity

import java.io.Serializable

data class UserPhotoEntity(
    val code: Int, // 200
    val data: Data,
    val msg: String // 操作成功
) {
    data class Data(
        val imageUrl: String,//头像地址
        val imageCode: String,//照片码
    ):Serializable
}