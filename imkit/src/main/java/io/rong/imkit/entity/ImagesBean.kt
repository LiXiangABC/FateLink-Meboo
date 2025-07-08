package io.rong.imkit.entity

import java.io.Serializable

data class ImagesBean(
    val imageUrl:String,
    val imageCode:String?,
    val videoLength:Long?,
):Serializable
