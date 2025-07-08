package com.crush.entity

data class MemberSubscribeEntity(
    val code: Int, // 200
    val data: Data,
    val msg: String // 操作成功
) {
    data class Data(
        val userCode: String?,
        val memberCode: String?,
        val memberType: String?,
        val refreshTime: Long?,//剩余倒计时 秒
        val status: Int?,
        val points: Int?,
        val autoRenew: Int?,//是否自动订阅0-初始化、1-开启、2关闭
        val expiryDate: String?,//结束时间
        val subscriptions: ArrayList<Subscriptions>,
        val memberDescription: MemberDescription,
        val productDescriptions: List<ProductDescriptions>,
    ){
        data class MemberDescription(
            val tip:String,
            val content:String,
            val header:String,
            val productDescriptions:String?,
        )
        data class Subscriptions(
            val tip:String,
            val content:String,
            val icon:String,
        )
        data class ProductDescriptions(
            val tip:String?,
            val productCode:String,
            val benefitNum:String,
            val benefitUnit:String,
            val price:String,
            val priceOriginal:String,
            val saving:String?,
            var check:Boolean,
        )
    }
}