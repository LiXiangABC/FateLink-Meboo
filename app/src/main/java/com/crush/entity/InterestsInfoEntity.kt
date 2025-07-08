package com.crush.entity

import com.crush.bean.TagBean

data class InterestsInfoEntity(
    val code: Int, // 200
    val data: MutableList<TagBean>,
    val msg: String // 操作成功
)