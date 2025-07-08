package com.crush.bean

import java.io.Serializable

data class UserPhotoBean(
    var imageUrl:String,
    var imageCode:String,
    var imageLoadUrl:String,
    var loading:Boolean,
):Serializable
