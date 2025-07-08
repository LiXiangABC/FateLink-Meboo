package com.crush.entity

import com.crush.bean.WLMListBean
import java.io.Serializable

data class ILikeEntity(
    val code: Int, // 200
    val data: ArrayList<WLMListBean>,
    val msg: String // 操作成功
)