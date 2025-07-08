package com.crush.entity

data class QueryBenefitsEntity(
    val code: Int, // 200
    val data:List<Data>,
    val msg: String // 操作成功
){
    data class Data(
        val memberCode:String,
        val benefitCode:Int,
        val maxUses:Int,

    )
}