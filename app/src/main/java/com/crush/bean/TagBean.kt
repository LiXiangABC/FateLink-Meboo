package com.crush.bean

import java.io.Serializable

data class TagBean(
        val interestCode: Int,
        val interest: String,
        var check: Boolean,
):Serializable