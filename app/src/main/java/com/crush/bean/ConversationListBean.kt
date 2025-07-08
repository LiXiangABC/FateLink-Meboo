package com.crush.bean

import java.io.Serializable

data class ConversationListBean(
    val userCodeFriend:String,
    val onlineStatus:Int,//1-在线，其它-离线
    val flashchatFlag:Int,//1-是flash chat，其它-离线
):Serializable
