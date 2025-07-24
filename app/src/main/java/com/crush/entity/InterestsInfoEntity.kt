package com.crush.entity

import io.rong.imkit.entity.IMTagBean

data class InterestsInfoEntity(
    val code: Int, // 200
    val data: MutableList<IMTagBean>,
    val msg: String // 操作成功
)