package io.rong.imkit.entity

data class AgreementEntity(
    val code: Int, // 200
    val data: Data,
    val msg: String // 操作成功
) {
    data class Data(
        val privacyPolicy: String? = "",
        val terms: String? = "",
        val paymentAgreement: String? = "",
        val orderSnapshotAddSwitch: Boolean = false
    )
}