package com.crush.entity

data class MatchResultEntity(
    val code: Int, // 200
    val data:Data,
    val msg: String // 操作成功
){
    data class Data(
        val userCode:String,
        val matched:Boolean,

    )
}