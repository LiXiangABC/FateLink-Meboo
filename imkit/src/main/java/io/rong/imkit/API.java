package io.rong.imkit;

public class API {
    public static final String user_reduce_benefits_url = "api/benefits/reduceBenefits";  //用户福利扣减接口
    public static final String user_member_product_category_url = "api/member/product/category";  //会员产品订阅列表接口
    public static final String user_config_url = "api/app/configs";  //其他信息配置接口
    public static final String user_create_order_url = "api/order/generateOrder"; //会员产品订阅接口
    public static final String user_info_url = "api/users/userInfo"; //个人信息接口
    public static final String user_user_albums_url="api/users/albums";  //会员私密相册接口
    public static final String user_send_message_url="api/im/message/tryToSend";  //能否发送消息接口
    public static final String user_user_albums_add_url="api/users/addAlbums";  //会员私密相册上传接口
    public static final String user_chat_open_url="api/chat/openchat";  //聊天页面操作接口

    public static final String user_order_token_url="api/order/storeToken";  //购买产品成功的消耗token接口
    public static final String user_add_action_url="api/users/addAction";  //用户行为接口

    public static final String user_common_notify_url="api/common/receive/common/notify";  //通用日志接口
    public static final String evaluate_check_url="api/evaluate/check";  //是否需要弹窗奖励接口
    public static final String evaluate_go_shop_url="api/evaluate/goEvaluate";  //去应用商店评价接口

    public static final String evaluate_back_app_url="api/evaluate/backApp";  //评价后返回app接口

    public static final String greeting_message_url="api/greeting/getMessage";  //快捷回复接口
    public static final String greeting_flirting_url="api/greeting/flirting";  //快捷回复接口
    public static final String im_notice_getIMPop="api/im/notice/getIMPop";  //M私聊触发提醒-弹窗
    public static final String im_notice_getTips="api/im/notice/getTips";  //M私聊触发提醒-弹窗
    public static final String order_log_url="api/order/log";  //订单系统上报
    public static final String pay_fail_url = "api/apy/payFail";// 支付失败通知接口
    public static final String pay_fail_notice_url = "api/apy/send/payment/notice";// 支付失败通知接口


}
