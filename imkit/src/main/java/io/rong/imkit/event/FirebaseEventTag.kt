package io.rong.imkit.event

enum class FirebaseEventTag {
    Home_Like,	//点击/右滑成功切换到下一card
    Home_Pass,//	点击/左滑成功切换到下一card
    Home_Flash_chat,//	成功跳转至私聊页面
    Home_Viewphotos,	//点击即计入次数
    Home_Profile,	//成功跳转至下一页面
    Home_Profile_Like,	//点击即计入次数
    Home_Profile_Pass,	//点击即计入次数
    Home_Profile_Flashchat,	//成功跳转至下一页面
    Home_Profile_More,	//点击即计入次数
    Home_Profile_Report,	//点击即计入次数
    Home_Profile_Reportsuccess,	//举报成功即计入次数
    Home_Profile_Block,	//点击即计入次数
    WLM_Like,//,点击喜欢/右滑
    WLM_Pass,//点击不喜欢/左滑
    WLM_Profile,//  点击进入他人主页
    WLM_Profile_Like,// 点击喜欢
    WLM_Profile_Pass,//   点击不喜欢
    WLM_Profile_More,// 点击更多
    WLM_Profile_Report,// 点击举报
    WLM_Profile_Reportsuccess,//  举报成功
    WLM_Profile_Block,//点击拉黑
    IM_Notification,        //	点击系统通知
    IM_Newfriend,        //	点击新关系
    IM_Chat,        //	点击会话
    IM_Left,        //	会话左滑
    IM_Deletechat,        //	点击删除会话
    Chat_Avatar,        //	点击对方头像
    Chat_More,        //	点击更多
    Chat_More_Report,        //	点击举报
    Chat_More_Reportsuccess,        //	举报成功
    Chat_More_Block,        //	点击拉黑
    Chat_Photo,        //	点击公开照片
    Chat_Video,        //	点击公开视频
    Chat_PP,        //	点击私密照片
    Chat_PV,        //	点击私密视频
    Chat_Album,        //	点击相册
    Chat_Album_P,        //	点击本地相册任意照片
    Chat_Album_V,        //	点击本地相册任意视频
    Chat_Album_P_asPrivate,        //	勾选本地相册任意照片发送确认页的send as private
    Chat_Album_V_asPrivate,        //	勾选本地相册任意视频发送确认页的send as private
    Chat_Album_P_noPrivate,        //	取消勾选本地相册任意照片发送确认页的send as private
    Chat_Album_V_noPrivate,        //	取消勾选本地相册任意视频发送确认页的send as private
    Chat_Album_P_asPrivate_send,        //	勾选as private后点击本地相册任意照片发送确认页发送
    Chat_Album_V_asPrivate_send,        //	勾选as private后点击本地相册任意视频发送确认页发送
    Chat_Album_P_Send,        //	不勾选as private后点击本地相册任意照片发送确认页发送
    Chat_Album_V_Send,        //	不勾选as private后点击本地相册任意视频发送确认页发送
    Chat_PrivateAlbum,        //	点击私密相册
    Chat_PrivateAlbum_P,        //	点击私密相册任意照片
    Chat_PrivateAlbum_V,        //	点击私密相册任意视频
    Chat_PrivateAlbum_P_send,        //	点击私密相册任意照片发送确认页发送
    Chat_PrivateAlbum_V_send,        //	点击私密相册任意视频发送确认页发送
    Home_View_Sub,        //	首页左滑/右滑/不喜欢/喜欢用户达浏览上限拉起会员订阅
    Home_View_Subsuccess,        //	首页左滑/右滑/不喜欢/喜欢用户达浏览上限拉起会员订阅支付成功
    Home_Profile_View_Sub,        //	首页-profile页不喜欢/喜欢用户达浏览上限拉起会员订阅
    Home_Profile_View_Subsuccess,        //	首页-profile页不喜欢/喜欢用户达浏览上限拉起会员订阅支付成功
    Home_Like_Sub,        //	首页右滑/喜欢用户达上限拉起会员订阅
    Home_Like_Subsuccess,        //	首页右滑/喜欢用户达上限拉起会员订阅支付成功
    Home_Profile_Like_Sub,        //	首页-profile页右滑/喜欢用户达上限拉起会员订阅
    Home_Profile_Like_Subsuccess,        //	首页-profile页右滑/喜欢用户达上限拉起会员订阅支付成功
    Home_Unlock_Sub,        //	首页点击unlock now拉起会员订阅
    Home_Unlock_Subsuccess,        //	首页点击unlock now拉起会员订阅支付成功
    Home_Flashchat_Sub,        //	首页点击flash chat拉起会员订阅
    Home_Flashchat_Subsuccess,        //	首页点击flash chat拉起会员订阅支付成功
    Home_Profile_Flash_chat_Sub,        //	首页-profile页点击flash chat拉起会员订阅
    Home_Profile_Flash_chat_Subsuccess,        //	首页-profile页点击flash chat拉起会员订阅支付成功
    WLM_Like_Sub,        //	WLM右滑/点击喜欢拉起会员订阅
    WLM_Like_Subsuccess,        //	WLM右滑/点击喜欢拉起会员订阅支付成功
    WLM_Pass_Sub,        //	WLM左滑拉起会员订阅
    WLM_Pass_Subsuccess,        //	WLM左滑拉起会员订阅支付成功
    WLM_Unlock_Sub,        //	WLM点击成为会员拉起会员订阅
    WLM_Unlock_Subsuccess,        //	WLM点击成为会员拉起会员订阅支付成功
    Chat_PrivateAlbum_Sub,        //	私聊页点击私密相册成为会员拉起会员订阅
    Chat_PrivateAlbum_Subsuccess,        //	私聊页点击私密相册成为会员拉起会员订阅支付成功
    Chat_Album_PV_asPrivate_Send_Sub,        //	私聊页发送照片send as private拉起会员订阅
    Chat_Album_PV_asPrivate_Send_Subsuccess,        //	私聊页发送照片send as private拉起会员订阅支付成功
    Me_Nonmember_Premium,        //	Me非会员点击进入会员订阅页
    Me_Nonmember_Premiumsuccess,        //	Me非会员点击进入会员订阅页支付成功
    Me_Member_Premium,        //	Me会员点击进入会员订阅页
    Me_Member_Premium_Unsub,        //	Me会员点击进入会员订阅页取消订阅
    Me_Member_Premiumsuccess,        //	Me会员点击进入会员订阅页换购成功
    Home_Flashchat_Buy,        //	首页点击flash chat拉起购买弹窗
    Home_Flashchat_Buysuccess,        //	首页点击flash chat拉起购买弹窗支付成功
    Home_Profile_Flash_chat_Buy,        //	首页-profile页点击flash chat拉起购买弹窗
    Home_Profile_Flash_chat_Buysuccess,        //	首页-profile页点击flash chat拉起购买弹窗支付成功
    Me_FC_Sub,        //	Me购买flash chat时是非会员状态 拉起会员订阅
    Me_FC_Subsuccess,        //	Me购买flash chat时是非会员状态 拉起会员订阅支付成功
    Me_PP_Sub,        //	Me购买pp时是非会员状态 拉起会员订阅
    Me_PP_Subsuccess,        //	Me购买pp时是非会员状态 拉起会员订阅支付成功
    Me_PV_Sub,        //	Me购买pv时是非会员状态 拉起会员订阅
    Me_PV_Subsuccess,        //	Me购买pv时是非会员状态 拉起会员订阅支付成功
    Me_Flashchat_Buy,        //	Me点击flash chat拉起购买弹窗
    Me_Flashchat_Buysuccess,        //	Me点击flash chat拉起购买弹窗支付成功
    Chat_PP_Sub,        //	私聊页点击pp拉起购买弹窗
    Chat_PP_Subsuccess,        //	私聊页点击pp拉起购买弹窗
    Chat_PP_Buy,        //	私聊页点击pp拉起购买弹窗
    Chat_PP_Buysuccess,        //	私聊页点击pp拉起购买弹窗支付成功
    Me_PP_Buy,        //	Me点击pp拉起购买弹窗
    Me_PP_Buysuccess,        //	Me点击pp拉起购买弹窗支付成功
    Chat_PV_Buy,        //	私聊页点击pv拉起购买弹窗
    Chat_PV_Buysuccess,        //	私聊页点击pv拉起购买弹窗支付成功
    Chat_PV_Sub,        //	私聊页点击pv拉起购买弹窗
    Chat_PV_Subsuccess,        //	私聊页点击pv拉起购买弹窗
    Me_PV_Buy,        //	Me点击pv拉起购买弹窗
    Me_PV_Buysuccess;       //	Me点击pv拉起购买弹窗支付成功
}