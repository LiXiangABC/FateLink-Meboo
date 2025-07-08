package com.crush.bean

import java.io.Serializable

data class TurnOnsListBean(
    val turnOnsCode:Int,
    val title:String,
    val content:String,
    val imageUrl:String,
    var selected:Int,//是否选择：0-未选择，1-已选择
):Serializable
