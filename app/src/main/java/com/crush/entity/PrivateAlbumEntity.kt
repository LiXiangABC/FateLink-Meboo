package com.crush.entity

import com.crush.bean.ImagesBean

data class PrivateAlbumEntity(
    val code: Int, // 200
    val data: Data,
    val msg: String // 操作成功
){
    data class Data(
        val images:List<ImagesBean>,
        val userCode:String,
        val albumCode:String,
    )
}