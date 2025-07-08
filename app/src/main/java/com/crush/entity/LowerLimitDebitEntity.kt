package com.crush.entity

data class LowerLimitDebitEntity(
    val code: Int, // 200
    val data: Data,
    val msg: String // 操作成功
) {
    data class Data(
        val title: String?="",
        val btnStr: String?="",
        val content: String?="",
        val showFlag: Boolean,
    )
}