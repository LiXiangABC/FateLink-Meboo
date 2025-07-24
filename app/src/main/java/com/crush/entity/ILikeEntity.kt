package com.crush.entity

import io.rong.imkit.entity.WLMListBean

data class ILikeEntity(
    val code: Int, // 200
    val data: ArrayList<WLMListBean>,
    val msg: String // 操作成功
)