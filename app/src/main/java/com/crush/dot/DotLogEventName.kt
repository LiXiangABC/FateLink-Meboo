package com.crush.dot

/**
 * 埋点
 */
object DotLogEventName {

    //登陆
    val LOGING = 101

    //新用户注册地理位置授权引导页面
    val NEW_REGISTER_USER_LOCATION_PERMISSION_GRANT_PAGE = 400

    //5次pass后弹出的地理位置授权引导页面
    val LOCATION_PERMISSION_GRANT_PAGE_WITH_5_PASS = 401

    //非注册当日首次启动APP
    val LOCATION_PERMISSION_GRANT_PAGE_WITH_APP_LAUNCH = 402

    //地理位置授权上报
    val LOCATION_PERMISSION_GRANTED = 403

    //首次弹出通知授权引导页面
    val FIRST_NOTIFICATION_PAGE = 500

    //新增有效会话弹出通知授权引导页面
    val NOTIFICATION_PAGE_WITH_EFFECTIVE_CHAT = 501

    //会话列表常驻通知授权引导
    val NOTIFICATION_PAGE_IN_CHAT_LIST = 502

    //通知授权上报
    val NOTIFICATION_PAGE_GRANTED = 503

    //填写nickName页面点击事件
    val USER_PROFILE_NICKNAME_ON_NEXT_CLICK = "Register_Nickname_Next"

    //填写BIRTHDATE页面点击事件
    val USER_PROFILE_BIRTHDAY_ON_NEXT_CLICK = "Register_Age_Next"

    //选择gender页面点击事件
    val USER_PROFILE_GENDER_ON_NEXT_CLICK = "Register_Gender_Next"

    //填写性向事件
    val USER_PROFILE_LOOKING_FOR_ON_NEXT_CLICK = 603

    //选择相册照片上传点击事件
    val USER_PROFILE_UPLOAD_IMAGE_ON_CLICK = 604

    //选择拍摄上传事件
    val USER_PROFILE_CAMERA_IMAGE_ON_CLICK = 605

    //照片上传结果事件
    val USER_PROFILE_UPLOAD_IMAGE_RESULT = 606

    val USER_PROFILE_NO_PREFERENCES_CLICK = "Register_Gender_No_preferences"

    //上传照片页面点击next事件
    val USER_PROFILE_UPLOAD_ON_NEXT_CLICK = "Register_Photo_Next"

    //社交目的选择页面点击事件
    val USER_PROFILE_WYH_ON_NEXT_CLICK = "Register_I_want_Next"

    //上传照片页面点击skip事件
    val USER_PROFILE_UPLOAD_ON_SKIP_CLICK = "Register_Photo_Skip"

    //appsflyer 初始化
    val APPSFLYER_INT_RESULT = 1001

    //新手礼包弹窗埋点
    val All_Egg_Sub = "All_Egg_Sub"//首次自动弹出
    val ALL_Egg_Buy = "ALL_Egg_Buy"//首次自动弹出彩蛋入口拉起购买弹窗
    val ALL_Egg_Buysuccess = "ALL_Egg_Buysuccess"//首次自动弹出彩蛋入口拉起购买弹窗支付成功

    val Home_Egg_Sub = "Home_Egg_Sub"//首页点击彩蛋入口
    val Home_Egg_Buysuccess = "Home_Egg_Buysuccess"//首页点击彩蛋入口拉起购买弹窗支付成功
    val Home_Egg_Buy = "Home_Egg_Buy"//首页点击彩蛋入口拉起购买弹窗

    val WLM_Egg_Sub = "WLM_Egg_Sub"//wlm页面点击彩蛋入口
    val WLM_Egg_Buy = "WLM_Egg_Buy"//wlm页面点击彩蛋入口拉起购买弹窗
    val WLM_Egg_Buysuccess = "WLM_Egg_Buysuccess"//wlm页面点击彩蛋入口拉起购买弹窗支付成功

    val Chat_Egg_Sub = "Chat_Egg_Sub"//im列表点击彩蛋入口
    val Chat_Egg_Buy = "Chat_Egg_Buy"//im列表点击彩蛋入口拉起购买弹窗
    val Chat_Egg_Buysuccess = "Chat_Egg_Buysuccess"//im列表点击彩蛋入口拉起购买弹窗支付成功

    val Me_Egg_Sub = "Me_Egg_Sub"//me页面点击彩蛋入口
    val Me_Egg_Buy = "Me_Egg_Buy"//me页面点击彩蛋入口拉起购买弹窗
    val Me_Egg_Buysuccess = "Me_Egg_Buysuccess"//me页面点击彩蛋入口拉起购买弹窗支付成功

}