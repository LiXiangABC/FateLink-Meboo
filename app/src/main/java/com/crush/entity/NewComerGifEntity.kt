package com.crush.entity

data class NewComerGifEntity(
    val code: Int, // 200
    val data: Data,
    val msg: String // 操作成功
) {
    data class Data(
        val newUserGiftFlag: Boolean, //新手礼包弹窗开关
        val newUserGiftCountDownTimes: Int //新手礼包弹窗倒计时
    )
}