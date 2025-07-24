package io.rong.imkit.entity

import java.io.Serializable

data class IMTagBean(
        val interestCode: Int,
        val interest: String,
        var check: Boolean,
):Serializable