package io.rong.imkit.entity

data class BaseResutlEntity<T>(
    val code: Int, // 200
    val msg: String, // 操作成功
    val data:T
)