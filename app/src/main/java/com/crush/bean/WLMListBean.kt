package com.crush.bean

import java.io.Serializable

data class WLMListBean(
    val userCode:String,
    val userCodeFriend:String,
    val nickName:String,
    val avatarUrl:String,
    val age:String,
    val interests:List<TagBean>,
    val greetingContent:String,
    var online:Int,
    val nearby:Int,
    val newFlag:Int,
    val turnOnsListSize:Int?,//TurnOns数量
    val lng:String,// 经度
    val lat:String?,// 纬度
    val wlmtime:Long?,// 纬度

):Serializable
