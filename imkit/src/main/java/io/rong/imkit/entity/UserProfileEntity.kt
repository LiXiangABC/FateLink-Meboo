package io.rong.imkit.entity

data class UserProfileEntity(
    val code: Int, // 200
    val data: Data,
    val msg: String // 操作成功
) {
    data class Data(
        val userCode: String,
        val nickName: String,
        val lookingFor: Int,
        val gender: Int,
        val privateAlbums: Int?,//用户私密照片数量
        val avatarUrl: String,
        val birthday: String,
        val aboutMe: String,
        val images:ArrayList<String>,
        val interests:MutableList<String>,
        val autoRenew:Int,//1-开启、2关闭
        val expiryDate:String,
        val isMember:Boolean,
    )
}