package io.rong.imkit.entity


data class PrivateAlbumAddEntity(
    val code: Int, // 200
    val data: ImagesBean,
    val msg: String // 操作成功
)