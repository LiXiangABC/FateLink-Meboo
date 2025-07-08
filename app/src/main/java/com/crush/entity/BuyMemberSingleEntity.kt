package com.crush.entity

import com.crush.bean.BuyMemberPageBean

data class BuyMemberSingleEntity(
    val code: Int, // 200
    val data:BuyMemberPageBean,
    val msg: String // 操作成功
)