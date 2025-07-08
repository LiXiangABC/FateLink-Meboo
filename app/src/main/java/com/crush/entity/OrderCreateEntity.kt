package com.crush.entity

data class OrderCreateEntity(
    val code: Int, // 200
    val data: Data,
    val msg: String // 操作成功
) {
    data class Data(
        var productCategory: Int,//单个产品购买类型，手动塞入
        var benefitNum:String,//单个产品的购买数量，手动塞入
        val purchaseType: Int,//1 消耗类型产品  2订阅产品
        val purchaseProductName: String,//订阅产品名
        val purchaseToken: String,//当前订阅产品token
        val orderNo: String,//订单号
        val productCode: String,//产品名
    )
}