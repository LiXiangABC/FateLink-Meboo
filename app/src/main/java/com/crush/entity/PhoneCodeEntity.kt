package com.crush.entity

data class PhoneCodeEntity(
    val code: Int, // 200
    val data: Data,
    val msg: String // 操作成功
) {
    data class Data(
        val privacyPolicy: String?="",
        val terms: String?="",
        val paymentAgreement: String?="",
    )
}