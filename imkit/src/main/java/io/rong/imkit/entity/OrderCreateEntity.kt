package io.rong.imkit.entity

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
        val latestPaidOrderPurchaseToken: String,//当前订阅产品token
        val orderNo: String,//订单号
        val currencyType:String,
        val productCode: String,//产品名
        val discountCode: String?,//优惠id
        val payableAmount: String,//金额
        val purchaseTypeExt:Int,//因为新增礼包场景，特添加此字段区分
        val orderPayFailedSwitch:Boolean = true, //支付报错显示弹窗开关
        val pushAfSwitch:Boolean,//是否推送Appsflyer数据 true 是
        val pushFBSwitch:Boolean,//是否推送Firebase广告数据 true 是
    )
}