package com.crush.entity

data class ConfigsPrivateEntity(
    val code: Int, // 200
    val data: Data,
    val msg: String // 操作成功
) {
    data class Data(
        val photoShowUnableFlag: Boolean,
        val newcomerGiftFlag: Boolean, //新手礼包弹窗开关
        val newcomerGiftPopSecs: Int //新手礼包弹窗倒计时
    )
}