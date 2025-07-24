package com.crush.entity

import com.crush.bean.TurnOnsListBean
import com.crush.bean.UserPhotoBean
import com.crush.bean.UserWantOrYouAcceptBean
import java.io.Serializable

data class UserProfileEntity(
    val code: Int, // 200
    val data: Data,
    val msg: String // 操作成功
) : Serializable {
    data class Data(
        val userCode: String,
        val email: String,
        val nickName: String,
        val lookingFor: Int,
        val gender: Int,
        val privateAlbums: Int?,//用户私密照片数量
        val avatarUrl: String?,
        val birthday: String,
        val topTag: String?,
        val aboutMe: String,
        val images:ArrayList<String>,
        val imagesV2:ArrayList<UserPhotoBean>,
        val interests:MutableList<String>,
        val autoRenew:Int,//1-开启、2关闭
        val expiryDate:String,
        val isMember:Boolean,
        val hideHeight:Boolean,
        val inchHeight:String,//身高英寸
        val socialConnections:String,//社交目的
        val socialConnectionsUrl:String,//社交目的图标
        val turnOnsList:ArrayList<TurnOnsListBean>,
        var userWant:MutableList<UserWantOrYouAcceptBean>?,
        var youAccept:MutableList<UserWantOrYouAcceptBean>?,
    ):Serializable
}