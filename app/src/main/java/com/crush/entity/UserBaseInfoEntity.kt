package com.crush.entity

data class UserBaseInfoEntity(
    val code: Int, // 200
    val data: Data,
    val msg: String // 操作成功
) {
    data class Data(
        val nickName: String,
        val userCode: String,
        val avatarUrl: String,
        val online: Int,
        val flashchatFlag: Int,
    )
}