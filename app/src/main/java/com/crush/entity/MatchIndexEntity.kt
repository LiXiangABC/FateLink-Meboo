package com.crush.entity

import com.crush.bean.MatchIndexBean
import java.io.Serializable

data class MatchIndexEntity(
    val code: Int, // 200
    val data: Data,
    val msg: String // 操作成功
){
    data class Data(
        val homepageList:List<MatchIndexBean>,
        val preferentialFlag:Int,//享受优惠标识：1-非会员注册前3天浏览上限 2-非会员注册3天后浏览上限
        val remainingBrowseBNum:Int,
        val memberBrowseEnableSwitch:Boolean,//会员浏览限制   true 控制浏览  false无限浏览
        val remainingBrowseNotice:String,//剩余可浏览的卡片数的文案提示
        val displayTimes:Int,//剩余可浏览的卡片数的文案提示消失倒计时
        val noChatTriggerTimes:Int,//划卡下限提醒：首先获取倒计时信息，X秒
        val remainingWlmNum:Int,
        val flashChatNum:Int,//flashchat剩余次数
        val flashChatLimit:Int,//flashchat展示位置
        val locationLimit:Int,//定位授权连续pass次数
        val ismember:Boolean,
        val turnOns:Boolean,//是否编辑turnOns
        val newUserFlag:Boolean,// ture表示新客 false 表示老客
        val turnOnsLimit:Int,//turn-ons编辑引导滑动次数
        val refreshTime:Long,//剩余时间秒
        val noticeMsg:String,//划卡到达上限的提示信息
        val coverImage:String,//划卡到达上限的视频链接
        val defaultCoverImage:String,//划卡到达上限的视频封面
    ):Serializable
}