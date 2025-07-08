package com.crush.bean

data class BuyMemberPageBean (
    val tip:String,
    val content:String,
    val productDescriptions:List<ProductExt>,
    ){
    data class ProductExt(
        val tip:String,
        val productCode:String,
        val benefitNum:String,
        val benefitUnit:String?,
        val price:String,
        val priceOriginal:String?,
        val saving:String,
        var check:Boolean?=false
    )
}
