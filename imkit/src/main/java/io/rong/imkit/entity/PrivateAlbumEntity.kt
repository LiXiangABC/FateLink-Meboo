package io.rong.imkit.entity


data class PrivateAlbumEntity(
    val code: Int, // 200
    val data: Data,
    val msg: String // 操作成功
){
    data class Data(
        val images:ArrayList<ImagesBean>,
        val userCode:String,
        val albumCode:String,
    )
}