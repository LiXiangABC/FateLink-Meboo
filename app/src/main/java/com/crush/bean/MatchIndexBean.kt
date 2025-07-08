package com.crush.bean

import java.io.Serializable

data class MatchIndexBean(
    val userCode:String,//用户聊天表示
    val friendUserCode:String,//用户聊天表示
    val images:List<Avatar>,//头像地址
    val nickName:String,//名称
    val age:String,//年龄
    val avatarUrl:String,//头像
    val aboutMe:String,//个性签名
    var online:Int,//1-在用(用户最后一次心跳时间<=3min) 2-离线
    val nearby:Int,//1-nearby（用户未授权定位则不显示） 2-其它
    val gender:Int,//性别   1男  2女  0未知
    val turnOnsListSize:Int,//TurnOns数量
    val lng:String,// 经度
    val lat:String?,// 纬度
    val state:String,// 州
    val city:String?,// 城市

    val interests:List<TagBean>,//兴趣，多个兴趣标签用逗号分隔
    val matched:Boolean,//true-匹配(对应已like我，则触发match效果) false-不匹配
    val userType:Int,//  2是CM  3是M

):Serializable