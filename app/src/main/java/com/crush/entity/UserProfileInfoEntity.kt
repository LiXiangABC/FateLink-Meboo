package com.crush.entity

import com.crush.bean.TurnOnsListBean
import com.crush.bean.UserPhotoV2Bean
import com.crush.bean.UserWantOrYouAcceptBean

data class UserProfileInfoEntity(
    val code: Int, // 200
    val data: Data,
    val msg: String // 操作成功
){
    data class Data(
        val userCode: String,
        val nickName: String,
        val lookingFor: Int,
        val gender: Int,
        val online: Int,
        val nearby: Int,
        val age: Int,
        var userType: Int,
        val avatarUrl: String,
        val birthday: String?,
        val aboutMe: String,
        val inchHeight: String,
        val socialConnections: String,
        val socialConnectionsUrl: String,
        val member:Boolean,//暂时为自定义，服务端未配置
        val images:ArrayList<String>,
        val imagesV2:ArrayList<UserPhotoV2Bean>,
        val interests:MutableList<String>,
        val turnOnsList:ArrayList<TurnOnsListBean>,
        val lng:String,// 经度
        val lat:String?,// 纬度
        val state:String,// 州
        val city:String?,// 城市
        val userWant:ArrayList<UserWantOrYouAcceptBean>?,
        val youAccept:ArrayList<UserWantOrYouAcceptBean>?,

        )
}