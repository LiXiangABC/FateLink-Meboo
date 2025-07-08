package com.crush.entity

data class TrafficEntity(
    val code: Int, // 200
    val data: Data,
    val msg: String // 操作成功
) {
    data class Data(
        val trafficSource: Int? //1-自然流量 2-非自然流量 3-不作处理，返回1走加白需求
    )
}