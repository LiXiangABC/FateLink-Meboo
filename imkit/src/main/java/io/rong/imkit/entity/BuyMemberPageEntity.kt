package io.rong.imkit.entity


data class BuyMemberPageEntity(
    val code: Int, // 200
    val data:Data,
    val msg: String // 操作成功
){
    data class Data(
        val productCategoryName:String,
        val subscriptions:List<Subscriptions>,
        val productDescriptions:ArrayList<ProductExt>,
    )
    data class ProductExt(
        val tip:String?,
        val productCode:String,
        val benefitNum:String,
        val benefitUnit:String?,
        val price:String,
        val currencyType:String,
        val priceOriginal:String?,
        val saving:String,
        var check:Boolean?=false
    )
    data class Subscriptions(
        val tip:String,
        val content:String,
    )
}