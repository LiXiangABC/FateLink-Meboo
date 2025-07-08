package com.crush.bean

import java.io.Serializable

data class UserWantOrYouAcceptBean(
    val type: Int = -1,
    val code: Int = -1,
    val value: String?,
    val iconUrl: String?,
    var selected: Int = -1,//是否选择：0-未选择，1-已选择
    var extendInfo: String?,
) : Serializable
