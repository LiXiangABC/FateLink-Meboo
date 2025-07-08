package com.crush.bean

import java.io.Serializable

data class GenderPreferListBean(
    val code:Long,
    val value:String,
    val iconUrl:String,
    var selected:Int,//是否选择：0-未选择，1-已选择
):Serializable
