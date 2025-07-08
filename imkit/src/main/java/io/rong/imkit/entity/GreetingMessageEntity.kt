package io.rong.imkit.entity

data class GreetingMessageEntity(
    val code: Int, // 200
    val data: List<String>,
    val msg: String // 操作成功
)