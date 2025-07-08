package com.crush.entity

data class UserAccountEntity(
    val code: Int, // 200
    val data: Data,
    val msg: String // 操作成功
) {
    data class Data(
        val facebookBind:Boolean,
        val googleBind:Boolean,
        val mobileBind:Boolean,
        val mobile:String,
        val loginType:Int,
        val autoRenew:Int,
        val googleName:String,
        val facebookName:String,
        val effectiveDate:String,
        val expiryDate:String //会员结束时间
    )
}