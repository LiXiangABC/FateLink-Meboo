package io.rong.imkit.entity

data class EvaluateCheckBean (
    val message:String,// 弹窗信息
    var status : Int,// 0不需要弹窗 1弹窗
)