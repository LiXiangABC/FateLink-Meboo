package com.crush.ui.index.helper

import android.os.CountDownTimer
import android.text.TextUtils
import android.util.Log
import com.crush.Constant
import com.crush.dialog.NewBieGiftPackDialog
import com.crush.dot.DotLogEventName
import com.crush.dot.DotLogUtil
import com.crush.entity.NewBieGiftPackEntity
import com.crush.entity.NewComerGifEntity
import com.crush.view.LayoutNewBieMiniView
import com.custom.base.config.BaseConfig
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.custom.base.manager.SDActivityManager
import com.custom.base.util.ToastUtil
import com.sunday.eventbus.SDEventManager
import io.rong.imkit.API
import io.rong.imkit.SpName
import io.rong.imkit.activity.Activities
import io.rong.imkit.dialog.MemberBuySuccessDialog
import io.rong.imkit.entity.OrderCreateEntity
import io.rong.imkit.event.EnumEventTag
import io.rong.imkit.pay.EmptySuccessCallBack
import io.rong.imkit.pay.PayUtils
import io.rong.imkit.utils.RongUtils

/**
 * @Author ct
 * @Date 2024/5/11 14:46
 * 新手礼包弹窗逻辑类
 */
object NewBieHelper {
    private var dialog: NewBieGiftPackDialog? = null
    private var firstShowNewBie: Boolean = false//只是为了区分首次显示上报打点的标识字段

    //隐藏新手礼包弹窗
    fun dismissDialog(layoutNewBieMiniViews: LayoutNewBieMiniView) {
        dialog?.dismiss()
        showNewBie(layoutNewBieMiniViews, -1, true)
    }

    //显示新手礼包弹窗
    fun showNewBieDialog(
        data: NewBieGiftPackEntity.Data?,
        layoutNewBieMiniViews: LayoutNewBieMiniView
    ) {
        if (RongUtils.isDestroy(Activities.get().top)) {
            return
        }
        if (data == null) {
            //为空就隐藏所有的悬浮窗
            ToastUtil.toast("The limited-time special has ended")
            showNewBie(layoutNewBieMiniViews, -1, false)
            return
        }
        dialog = Activities.get().top?.let {
            NewBieGiftPackDialog.Builder(it)
                .setCallback(object : NewBieGiftPackDialog.Callback {
                    override fun onBackClick() {
                        //关闭弹窗显示出来悬浮球
                        showNewBie(layoutNewBieMiniViews, -1, true)
                    }

                    override fun onBuyClick() {
                        //点击购买
                        handleBuyNewbieData(data, layoutNewBieMiniViews)
                    }
                }, data, false).create()
        }

        dialog?.show()
    }

    //统一处理调支付接口后的回调
    fun handleBuyNewbieData(
        data: NewBieGiftPackEntity.Data?,
        layoutNewBieMiniViews: LayoutNewBieMiniView
    ) {
        //Log.i("新手礼包", "用户点击了购买按钮")
        if (RongUtils.isDestroy(Activities.get().top)) {
            return
        }
        if (data == null) {
            showNewBie(layoutNewBieMiniViews, -1, false)
            return
        }
        if (TextUtils.isEmpty(data.productCode) && TextUtils.isEmpty(data.productCategory)) {
            return
        }
        requestBuyNewBie(data.productCode!!, data.productCategory!!) {
            //打点
            if(firstShowNewBie){
                //Log.i("新手礼包打点","ALL_Egg_Buysuccess")
                DotLogUtil.setEventName(DotLogEventName.ALL_Egg_Buysuccess)
                    .commit(Activities.get().top)
            }else{
                //打点上报
                when (BaseConfig.getInstance.getInt(SpName.homeIndex, -1)) {
                    0 -> {
                        DotLogUtil.setEventName(DotLogEventName.Home_Egg_Buysuccess)
                            .commit(Activities.get().top)
                    }

                    1 -> {
                        DotLogUtil.setEventName(DotLogEventName.WLM_Egg_Buysuccess)
                            .commit(Activities.get().top)
                    }

                    2 -> {
                        DotLogUtil.setEventName(DotLogEventName.Chat_Egg_Buysuccess)
                            .commit(Activities.get().top)
                    }

                    3 -> {
                        DotLogUtil.setEventName(DotLogEventName.Me_Egg_Buysuccess)
                            .commit(Activities.get().top)
                    }
                }
            }
            //最终支付成功 需要关闭礼包弹窗 反之、失败不关闭
            dialog?.dismiss()
            dialog?.setOnDismissListener {
                firstShowNewBie = false
            }
            //关闭悬浮球
            showNewBie(layoutNewBieMiniViews, -1, false)
            Activities.get().top?.let {
                MemberBuySuccessDialog(
                    it,
                    object :
                        MemberBuySuccessDialog.ChangeMembershipListener {
                        override fun onListener() {
                        }
                    }).showPopupWindow()
            }
            //也要刷新一下首页的fc
            SDEventManager.post(EnumEventTag.REFRESH_GET_FLASH_DATA.ordinal)
        }
    }

    //购买新手礼包接口
    private fun requestBuyNewBie(
        productCode: String,
        productCategory: String,
        callBack: () -> Unit
    ) {
        //Log.i("新手礼包", "请求购买新手礼包信息接口")
        if (RongUtils.isDestroy(Activities.get().top)) {
            return
        }
        OkHttpManager.instance.requestInterface(object :
            OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(API.user_create_order_url)
                requestBody.add("productCode", productCode)
                requestBody.add("productCategory", productCategory)
            }
        }, object : SDOkHttpResoutCallBack<OrderCreateEntity>() {
            override fun onSuccess(entity: OrderCreateEntity) {
                //Log.i("新手礼包", "请求购买新手礼包信息接口 onSuccess")
                //打点上报
                //区分首次弹出支付:因为刚出来肯定会记录这个值、所以拿这个值做一下判断
                if (firstShowNewBie) {
                    //Log.i("新手礼包打点","ALL_Egg_Buy")
                    DotLogUtil.setEventName(DotLogEventName.ALL_Egg_Buy)
                        .commit(Activities.get().top)
                } else {
                    when (BaseConfig.getInstance.getInt(SpName.homeIndex, -1)) {
                        0 -> {
                            DotLogUtil.setEventName(DotLogEventName.Home_Egg_Buy)
                                .commit(Activities.get().top)
                        }

                        1 -> {
                            DotLogUtil.setEventName(DotLogEventName.WLM_Egg_Buy)
                                .commit(Activities.get().top)
                        }

                        2 -> {
                            DotLogUtil.setEventName(DotLogEventName.Chat_Egg_Buy)
                                .commit(Activities.get().top)
                        }

                        3 -> {
                            DotLogUtil.setEventName(DotLogEventName.Me_Egg_Buy)
                                .commit(Activities.get().top)
                        }
                    }
                }

                Activities.get().top?.let {
                    PayUtils.instance.start(
                        entity,
                        it,
                        object : EmptySuccessCallBack {
                            override fun OnSuccessListener() {
                                //Log.i("新手礼包", "请求购买新手礼包支付成功")
                                callBack.invoke()
                            }
                        })
                }
            }
        })
    }

    //请求新手礼包开关
    fun requestNewBieOpen(callBack: (NewComerGifEntity) -> Unit) {
        //Log.i("新手礼包", "去请求了开关接口")
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.newcomer_trigger_url)
            }
        }, object : SDOkHttpResoutCallBack<NewComerGifEntity>(false) {
            override fun onSuccess(entity: NewComerGifEntity) {
                callBack.invoke(entity)
            }
        })
    }

    //获取新手礼包弹窗数据
    fun requestNewBieData(callBack: (NewBieGiftPackEntity) -> Unit) {
        //Log.i("新手礼包", "获取新手礼包弹窗数据")
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.request_new_bie_gift_url)
            }
        }, object : SDOkHttpResoutCallBack<NewBieGiftPackEntity>(false) {
            override fun onSuccess(entity: NewBieGiftPackEntity) {
                //Log.i("新手礼包", "获取新手礼包弹窗数据接口成功")
                callBack.invoke(entity)
            }
        })
    }

    private var countDownTimer: CountDownTimer? = null

    //释放一下资源
    fun releaseNewBieData() {
        dialog = null
        countDownTimer?.cancel()
        countDownTimer = null
    }

    //开始新手弹窗倒计时
    fun startNewBieTime(
        layoutNewBieMiniViews: LayoutNewBieMiniView,
        countDownTime: Int,
        data: NewBieGiftPackEntity.Data?
    ) {
        //每次重新打开的时候去请求完礼包数据要判断是否显示（付费过 非自然流量）
        if (data == null) {
            //说明符合这类情况不需要再显示悬浮球了 也不需要倒计时
            //Log.i("新手礼包", "触发（付费过 非自然流量）不需要展示")
            showNewBie(layoutNewBieMiniViews, -1, false)
            return
        }
        //首次显示弹窗，后面不再倒计时触发,只是悬浮窗 == 经产品确认不跟设备走 跟账户走
        Log.i(
            "新手礼包", "NewBieGiftPack_${BaseConfig.getInstance.getString(SpName.userCode, "")}==${
                BaseConfig.getInstance.getBoolean(
                    "NewBieGiftPack_${
                        BaseConfig.getInstance.getString(
                            SpName.userCode,
                            ""
                        )
                    }", false
                )
            }"
        )
        if (BaseConfig.getInstance.getBoolean(
                "NewBieGiftPack_${
                    BaseConfig.getInstance.getString(
                        SpName.userCode,
                        ""
                    )
                }", false
            )
        ) {
            //Log.i("新手礼包", "已经展示过一次弹窗了 只显示悬浮球")
            //防止用户展示弹窗的时候 杀掉进程下一次进来还是要显示悬浮球
            showNewBie(layoutNewBieMiniViews, -1, true)
            return
        }
        //Log.i("新手礼包", "显示弹窗 进入倒计时")
        countDownTimer = object : CountDownTimer(countDownTime.toLong() * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // 每次计时间隔执行的操作，比如更新 UI 显示剩余时间
                val secondsRemaining = millisUntilFinished / 1000
                // 更新 UI 显示剩余时间
                //Log.i("新手礼包", "剩余时间: $secondsRemaining 秒")
                if (RongUtils.isDestroy(Activities.get().top)) {
                    countDownTimer?.cancel()
                    return
                }
            }

            override fun onFinish() {
                firstShowNewBie = true
                // 倒计时结束时执行的操作
                if (RongUtils.isDestroy(Activities.get().top)) {
                    return
                }
                //需要再去请求一下开关接口
                requestNewBieOpen {
                    if (it.data.newUserGiftFlag) {
                        //拿到当前请求到的data
                        showNewBieDialog(data, layoutNewBieMiniViews)
                    }
                }

            }
        }
        countDownTimer?.start()
    }

    //新手礼包弹窗的view显示状态
    fun showNewBie(layoutNewBieMiniViews: LayoutNewBieMiniView, index: Int, isShow: Boolean) {
        //Log.i("新手礼包", "悬浮球显示状态=$isShow")
        layoutNewBieMiniViews.show(isShow, index)
    }

    //动态设置悬浮球在每个tab页面的间距
    fun setMarginData(layoutNewBieMiniViews: LayoutNewBieMiniView, index: Int) {
        //Log.i("新手礼包", "更改tab悬浮球高度==$index")
        when (index) {
            0 -> {
                //home
                layoutNewBieMiniViews.setMarginData(90f, 10f)
            }

            1 -> {
                //wlm
                layoutNewBieMiniViews.setMarginData(120f, 20f)
            }

            2 -> {
                //im
                layoutNewBieMiniViews.setMarginData(100f, 10f)
            }

            3 -> {
                //me
                layoutNewBieMiniViews.setMarginData(30f, 10f)
            }
        }
    }
}