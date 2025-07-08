package com.crush.entity


data class IMMatchEntity(
    val code: Int, // 200
    val data: ArrayList<Data>,
    val msg: String // 操作成功
) {
    data class Data(
        val userCodeFriend:String,
        val newFlag:Int,
        var online:Int,
        val nearby:Int,
        val avatarUrl:String,
        val lng:String,// 经度
        val lat:String?,// 纬度
    )
}