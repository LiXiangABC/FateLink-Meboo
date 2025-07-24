package com.crush.bean

import java.io.Serializable

data class RegisterTagBean(
        val code: Int,
        val value: String,
        var check: Boolean,
):Serializable