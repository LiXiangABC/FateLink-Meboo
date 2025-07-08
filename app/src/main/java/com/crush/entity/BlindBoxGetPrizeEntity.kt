package com.crush.entity

data class BlindBoxGetPrizeEntity(
    val code: Int, // 200
    val data: Data,
    val msg: String // 操作成功
) {
    data class Data(
        val prize: Int,//奖品 0.没有中奖 1.pp 2.暧昧礼物 3.flash chat 4.流量曝光 5.首页无限划卡 ,
        val prizeCount: Int,//奖品数量
        val userCode: String,//用户编号
    )
}