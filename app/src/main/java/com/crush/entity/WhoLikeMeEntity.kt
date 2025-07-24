package com.crush.entity

import io.rong.imkit.entity.WLMListBean
import java.io.Serializable

data class WhoLikeMeEntity(
    val code: Int, // 200
    val data: Data,
    val msg: String // 操作成功
):Serializable{
    data class Data(
        val wlmList:ArrayList<WLMListBean>,
        val wlmNum:Int,
        val wlmCount:Int?,
        val content:String?,
        val message:String = "",
        val isMember:Boolean,
        val wlmWindow: String,
        val wlmUsageNum: String,
    ):Serializable
}