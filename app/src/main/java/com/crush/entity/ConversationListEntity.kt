package com.crush.entity

import com.crush.bean.ConversationListBean
import java.io.Serializable

data class ConversationListEntity(
    val code: Int, // 200
    val data:ArrayList<ConversationListBean>,
    val msg: String // 操作成功
):Serializable