package io.rong.imkit.entity

data class DiscountInfoEntity(
    val discountInfoResponse: DiscountInfoResponse,//(商品促销信息, optional): 商品促销信息 ,
    val havaDiscount: Int,//(integer, optional): 是否有商品促销 0否 1是 ,
    val havePop: Int,//(integer, optional): 是否需要弹窗 ,
    val popOverTime: Long,//(integer, optional): 折扣有效时间(秒)
) {
    data class DiscountInfoResponse(
        val firstDisplay: Int,//(integer, optional): 是否是第一个显示 0否 1是 ,
        val popButton: String,//(string, optional): 弹窗按钮文案 ,
        val popButtonContent: String,//(string, optional): 弹窗底部内容 ,
        val popButtonTitle: String,//(string, optional): 弹窗底部标题 ,
        val popContent: String,//(string, optional): 弹窗内容 ,
        val popTag: String,//(string, optional): 折扣标签 ,
        val popTitle: String,//(string, optional): 弹窗标题 ,
        val productCode: String,//(string, optional): Google 支付产品Id ,
        val productCategory: String,//(string, optional): 分类 ,
        val promotionDelPrice: String,//(string, optional): 促销划线价格 ,
        val promotionNote: String,//(string, optional): 标后价注释 ,
        val promotionPrice: String//(string, optional): 促销价格
    )

    enum class HavePop(val value: Int) {
        NEED_POP(1),NOT_POP(0)
    }
}

