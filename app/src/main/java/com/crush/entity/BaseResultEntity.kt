package com.crush.entity

class BaseResultEntity<T>(
    val code: Int, // 200
    val msg: String, // 操作成功
    val data:T
)