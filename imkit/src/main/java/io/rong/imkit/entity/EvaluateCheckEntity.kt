package io.rong.imkit.entity


data class EvaluateCheckEntity(
    val code: Int, // 200
    val data:EvaluateCheckBean,
    val msg: String // 操作成功
)