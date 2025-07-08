package com.crush.entity

data class ConfigsEntity(
    val code: Int, // 200
    val data: List<Data>,
    val msg: String // 操作成功
) {
    data class Data(
        val iocn: String?,
        val code: Int?,
        val name: String?,
        val userCode: String?,
    )
}