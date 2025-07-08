package com.crush.entity

data class AnnouncementEntity(
    val code: Int, // 200
    val data: Data,
    val msg: String // 操作成功
) {
    data class Data(
        val title: String?="",
        val msg: String?="",
        val msg2: String?="",
        val ppNum: String?="",
        val pvNum: String?="",
    )
}