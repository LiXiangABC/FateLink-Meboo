package com.crush


/**
 * 作者：
 * 日期：2022/3/11
 * 说明：
 */
object Constant {
    var login_Url="api/auth/login"  //登录/注册
    var account_bind_Url="api/users/accountBind"  //账号绑定
    var account_remove_Url="api/users/removeAccount"  //账号绑定
    var login_code_Url="api/sms/sendSMS"  //登录/注册
    var im_token_Url="api/auth/getIMtoken"  //获取IM的token
    var user_account_url="api/users/userAccount"  //账号绑定页面接口
    var user_user_info_init_url="api/users/initUserInfo"  //个人信息添加接口
    var user_user_change_url="api/users/changeUserInfo"  //个人信息修改接口
    var match_user_url="api/home/homepage"  //首页
    var user_info_url="api/users/userInfo"  //个人信息接口
    var user_add_wlm_url="api/users/addWlm"  //个人喜欢接口
    var user_user_info_url="api/users/userInfo"  //个人信息接口
    var user_wlm_list_url="api/users/wlmList"  //who like me列表接口
    var user_interests_url="api/users/interests"  //个人兴趣接口
    var user_add_action_url="api/users/addAction"  //用户行为接口
    var user_logout_url="api/auth/logout"  //用户注销接口
    var user_users_matched_list_url="api/users/matchedList"  //新建联消息列表
    var user_config_url="api/app/configs"  //其他信息配置接口
    var user_add_device_info_url="api/app/device/addDeviceInfo"  //设备信息接口

    var user_add_new_flag_url="api/users/addNewFlag"  //设置newFlag接口
    var user_apk_update_url="api/app/upgradation"  //apk更新接口
    var user_user_base_url="api/users/getUserInfo"  //获取用户的姓名和头像接口
    var user_member_product_subscription_url="api/member/product/subscription"  //会员产品订阅列表接口
    var user_create_order_url="api/order/generateOrder"  //会员产品订阅接口
    var user_off_auto_renew_url="api/member/offAutoRenew"  //会员取消自动订阅接口
    var user_user_albums_url="api/users/albums"  //会员私密相册接口
    var user_user_albums_add_url="api/users/addAlbums"  //会员私密相册上传接口
    var user_user_albums_remove_url="api/users/removeImages"  //会员私密相册删除接口
    var user_member_query_benefits_url="api/member/queryBenefits"  //查询会员福利剩余次数接口
    var user_reduce_benefits_url="api/benefits/reduceBenefits"  //用户福利扣减接口
    var user_message_try_to_browse_url="api/im/message/tryToBrowse"  //用户福利扣减接口
    var user_benefits_reduceWLM_url="api/benefits/reduceWLM"  //用户福利扣减接口
    var user_push_token_url="api/app/device/addPushToken"  //pushToken推送接口
    var user_common_notify_url="api/common/receive/common/notify"  //通用日志接口
    var user_addAppflyer_info_url="api/appsflyer/addAppFlyerInfo"  //增加appsflyerUID接口
    var user_addAdjust_info_url="api/adjust/addAdjustInfo"  //增加appsflyerUID接口
    var user_permission_operate_url="api/app/device/operate/permission"  //是否拥有权限接口
    var user_changeImage_url="api/images/changeImage"  //照片上传接口
    var user_conversation_list_url="api/auth/conversation/list"  //会话列表及在线状态接口
    var sms_config_url="/api/sms/smsConfig"  //短信调研配置接口
    var check_logout="api/auth/checkLogout"  //是否是注销账户
    var delete_account_message="api/auth/message"  //删除账户文案
    var merry_christmas="api/greeting/merryChristmas"  //圣诞推送接口
    var blind_box_add_prize_url="api/blindBox/addPrize"  //每日盲盒领取奖励接口
    var blind_box_get_prize_url="api/blindBox/getPrize"  //每日盲盒读取奖励接口
    var blind_box_info_url="api/blindBox/bindBoxInfo"  //盲盒是否开启、暧昧次数、流量曝光接口
    var blind_box_barrage_url="api/blindBox/barrage"  //弹幕消息接口
    var register_config_url="api/app/register/config"  //登陆、注册页面初始化加载选项配置接口
    var product_discount_getDiscount = "api/product/discount/getDiscount"// 读取促销商品
    var product_discount_triggerDiscount = "api/product/discount/triggerDiscount"// 触发促销
    var user_like_url = "api/user/like/list"// i like接口
    var user_like_count_url = "api/user/like/count"// i like count接口
    var pay_fail_url = "api/apy/payFail"// 支付失败通知接口
    var request_new_bie_gift_url = "api/member/product/newcomer/welcomePack" //新手礼包弹窗数据
    var gifting_equity_msg_url = "api/notice/giftingEquityMsg" //公告弹窗数据
    var home_notice_url = "api/home/notice" //划卡下限提醒
    var home_notice_read_url = "api/home/notice/read" //划卡下限提醒已提醒
    var newcomer_trigger_url="api/member/product/newcomer/trigger/delay/times"  //新手礼包配置接口
    var traffic_from_url="api/traffic/from"  //加白版本控制
    var firebase_init_url="api/firebase/init"  //上传firebase的id
    var review_review_pop_status_url="api/review/review/pop/status"  //是否允许评价
    var review_addReview_url="api/review/addReview"  //保存评价结果，好评跳转到play console
    var review_backApp_url="api/review/backApp"  //评价后返回app, 下发权益
    var heart_beat_url="api/heartbeat"  //

}