package com.crush.entity

data class GoogleReviewEntity(
    val code: Int, // 200
    val data: Data,
    val msg: String // 操作成功
) {
    data class Data(
        val status:Int,//0不需要弹窗 1弹窗
        val buttonStr:String,//按扭显示文本
        val content:String,//弹窗信息
    )
}