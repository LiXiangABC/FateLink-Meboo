package com.crush.bean

data class UserProfileInfoItemBean(
    var nickName: String?="",
    var lookingFor: Int?=0,
    var gender: Int?=0,
    var online: Int?=0,
    var nearby: Int?=0,
    var age: Int?=0,
    var birthday: String?="",
    var aboutMe: String?="",
    val inchHeight: String?="",
    var socialConnections: String?="",
    var socialConnectionsUrl: String?="",
    var image:String?="",
    var interests:MutableList<String>?= arrayListOf(),
    val turnOnsList:ArrayList<TurnOnsListBean>?= null

)
