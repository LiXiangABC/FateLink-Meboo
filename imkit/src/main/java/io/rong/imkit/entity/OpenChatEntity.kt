package io.rong.imkit.entity

data class OpenChatEntity(
    val code: Int, // 200
    val data: Data,
    val msg: String // 操作成功
) {
    data class Data(
        val effectiveChat: Boolean,//是否是有效会话
        val member: Boolean,//是否会员
        val userType: Int,//2 CM 3 M
        val messagesNumber: Int,//可发送消息数量
        val onlineStatus: Int,//1-在线，其它-离线
        val flashchatFlag: Int,//1-flash chat，其它-离线
        val turnOnsList: ArrayList<TagBean>,
        val flashChatMsg: String,// 消息显示的文案
        var userMessageNum: Int,// 用户全局累计聊天多少句
        val matchFlag: Int,// 显示macth
        val state: String,// 州
        val city: String?,// 城市
        val sayHiUrl: String?,// 城市
        val userWant: SelectVal?,//
        val youAccept: List<SelectVal>?,//
        val imReadyCountList: List<Integer>,//im私聊触发提醒聊天数量
        val imTipsCountList: List<Integer>//Tips私聊触发提醒聊天数量
    )

    data class SelectVal(
        val value: String
    )
}