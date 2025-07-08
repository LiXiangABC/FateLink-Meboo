package com.crush.entity

data class UserMemberStatusEntity(
    val code: Int, // 200
    val data: Data,
    val msg: String // 操作成功
) {
    data class Data(
        val userCode: String,
        val memberCode: String,
        val appCode: String,
        val remainingOfFlashChatNum: Int,//flashChat剩余次数
        val remainingOfViewImages: Int,//查看私密照片剩余次数
        val remainingOfViewVideo: Int,//查看私密视频剩余次数
        val status: Int,//状态：0-无效 1-有效

    )
}