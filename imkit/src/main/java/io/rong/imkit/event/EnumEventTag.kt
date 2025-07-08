package io.rong.imkit.event

/**
    使用方法：
 发送：
    SDEventManager.post(EnumEventTag.ORDER_DETAIL_REFRESH.ordinal)
 接收：
    override fun onEventMainThread(event: SDBaseEvent) {
        when (EnumEventTag.valueOf(event.tagInt)) {
            EnumEventTag.PLAY_VIP_SUCCESS -> {
            }
        }
    }
 */
enum class EnumEventTag{
    INDEX_TO_INDEX,//跳转到INDEX页面
    INDEX_TO_WLM,//跳转到wlm页面
    INDEX_MATCH_REFRESH_DATA,//首页已匹配数据刷新
    INDEX_RELOAD_DATA,//首页假性刷新数据
    INDEX_REFRESH_DATA,//首页刷新数据
    INDEX_COUNTDOWN_SHOW,//首页显示倒计时
    INDEX_DISLIKE_SWIPED,//首页匹配左滑不喜欢
    INDEX_LIKE_SWIPED,//首页匹配右滑喜欢
    WLM_LIKE_SWIPED,//wlm 右滑喜欢
    WLM_DISLIKE_SWIPED,//wlm 左滑不喜欢
    WLM_REFRESH,//wlm 刷新
    WLM_INIT_REFRESH,//wlm 刷新
    MY_REFRESH,//my 刷新
    GO_MAIN,//去首页
    PRIVATE_ALBUMS_REMOVE,//
    BUY_MEMBER_CHAT,//IM购买会员
    REFRESH_WLM_LIKE_SIZE,//刷新wlm底部bottom的数量
    BUY_MEMBER_PRIVATE,//IM购买私密次数
    SHOW_NOTIFICATION_DIALOG,//显示通知权限弹窗
    BUY_MEMBER_Subscription,//购买会员
    LOGIN_FINISH,//登陆页面关闭
    MY_ACCOUNT_REFRESH,//账号页面刷新
    WLM_ADD_ITEM,//wlm单个添加
    WLM_REFRESH_ITEM,//wlm 刷新
    CHAT_LIST_ITEM,//chat 列表 刷新
    INDEX_REFRESH_DATA_ITEM,//首页刷新数据
    SEND_VIP_NOTICE,//发送聊天消息次数为空的请求
    UPDATE_YEAR,//刷新年纪
    PAY_RESULT,//支付结果上报
    IM_CHAT_LOCATION,//更新位置
    DISCOUNT_COUNTDOWN_REFRESH,//折扣倒计时刷新
    DISCOUNT_COUNTDOWN_END,//折扣倒计时结束
    LOGIN_OUT,//退出
    CLOSE_DISCOUNT_POP,
    CLOSE_MEMBER_POP,
    REFRESH_ME_BOTTOM_AVATAR,
    TRIGGER_DISCOUNT_POP,//促发促销弹窗
    WLM_DISCOUNT_DOWN_SHOW,//wlm倒计时展示
    VIEW_CHAT_WLM_START,//IM 轮播滚动
    VIEW_CHAT_WLM_STOP,//IM 轮播停止
    START_VIDEO_STOP,//启动页视频停止播放
    PAY_FAILED_POP_SHOW,//支付失败弹窗
    FLASH_CHAT_END_NUM_ADD,//flash chat 剩余次数 添加
    FLASH_CHAT_END_NUM_REDUCTION,//flash chat 剩余次数 减少
    FLASH_CHAT_REMOVE,//flash chat 移除i like次数
    LOWER_LIMIT_DEBIT_SHOW,//首页划卡下限
    LOWER_LIMIT_DEBIT_TO_INDEX,//首页划卡下限
    EMAIL_EDIT,//跳转到email编辑页面
    START_REQUEST,//启动页重新请求
    REFRESH_CHAT_HEAD_WLM,//刷新聊天列表wlm
    REFRESH_GET_FLASH_DATA,//购买会员成功=获取查询权益接口=刷新首页fc次数
    INDEX_LOCATION_SWIPED,//点击了首页请求位置功能卡=权限
    CLOSE_PAY_BUY_DIALOG;//用户购买成功，支付失败，显示支付报错提示弹窗都关闭当前购买弹窗

    companion object {
        fun valueOf(index: Int): EnumEventTag? {
            return if (index >= 0 && index < values().size) {
                values()[index]
            } else {
                null
            }
        }
    }
}
