package com.crush.entity

data class ResultDataEntity<T>(
    val code: Int, // 200
    val data: T? = null,
    val msg: String // 操作成功
)
