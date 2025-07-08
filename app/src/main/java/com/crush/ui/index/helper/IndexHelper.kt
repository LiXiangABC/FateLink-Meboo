package com.crush.ui.index.helper

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.crush.App
import com.crush.Constant
import com.crush.entity.BaseResultEntity
import com.crush.entity.QueryBenefitsEntity
import com.crush.util.DateUtils
import com.crush.util.MemberDialogShow
import com.crush.util.MyCountDownTimer
import com.custom.base.config.BaseConfig
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.custom.base.manager.SDActivityManager
import com.sunday.eventbus.SDEventManager
import io.rong.imkit.SpName
import io.rong.imkit.dialog.DiscountOfferDialog
import io.rong.imkit.entity.DiscountInfoEntity
import io.rong.imkit.event.EnumEventTag
import java.util.Date

object IndexHelper {

    //刷新一下获取权益接口 很尴尬
    fun requestBenefitsData(callback: (Int) -> Unit) {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.user_member_query_benefits_url)
            }
        }, object : SDOkHttpResoutCallBack<QueryBenefitsEntity>(false) {
            override fun onSuccess(entity: QueryBenefitsEntity) {
                if (entity.data != null) {
                    val fcData = entity.data.filter { it.benefitCode == 1 }
                    callback.invoke(fcData.firstOrNull()?.maxUses ?: 0)
                }
            }
        })
    }


    /**
     * 保存卡片进度跳最大值
     */
    fun saveProgressMaxValues(remainingBrowseBNum: Int) {
        val key = SpName.remainingBrowseBNum + BaseConfig.getInstance.getString(
            SpName.userCode,
            ""
        ) + DateUtils.getTime(Date(System.currentTimeMillis()))
        if (BaseConfig.getInstance.getInt(key, -1) < remainingBrowseBNum) {
            BaseConfig.getInstance.setInt(
                key, remainingBrowseBNum
            )
        }
    }

    /**
     * 获取卡片进度跳最大值
     */
    fun getProgressMaxValues(): Int {
        val key = SpName.remainingBrowseBNum + BaseConfig.getInstance.getString(
            SpName.userCode,
            ""
        ) + DateUtils.getTime(Date(System.currentTimeMillis()))
        return BaseConfig.getInstance.getInt(key, 0)
    }

    /**
     * 获取促销弹窗数据借口
     */
    fun getDiscountPopData(isMember: Boolean = false, result: (data: DiscountInfoEntity) -> Unit) {
        if (isMember) {
            return
        }
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setGet(Constant.product_discount_getDiscount)
            }
        }, object : SDOkHttpResoutCallBack<BaseResultEntity<DiscountInfoEntity>>() {
            override fun onSuccess(entity: BaseResultEntity<DiscountInfoEntity>) {
                result.invoke(entity.data)
            }

            override fun onFailure(code: Int, msg: String) {

            }
        }, isShowDialog = false)
    }

    /**
     * 获取促销弹窗数据借口
     */
    fun triggerDiscount(triggerType: Int?, result: (data: DiscountInfoEntity) -> Unit) {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.product_discount_triggerDiscount)
                if (triggerType != null) {
                    requestBody.add("triggerType", triggerType)
                }
                requestBody.add("buyType", BaseConfig.getInstance.getInt(SpName.buyType, -1))
                requestBody.add("userCode", BaseConfig.getInstance.getString(SpName.userCode, ""))
            }
        }, object : SDOkHttpResoutCallBack<BaseResultEntity<Int>>() {
            override fun onSuccess(entity: BaseResultEntity<Int>) {
                if (entity.data == 1) {
                    //促发成功
                    getDiscountPopData {
                        result.invoke(it)
                    }
                } else if (entity.data == 0) {
                    SDEventManager.post(EnumEventTag.CLOSE_DISCOUNT_POP.ordinal)
                }
            }

            override fun onFailure(code: Int, msg: String) {

            }
        }, isShowDialog = false)
    }

    /**
     * 现实促销弹窗
     */
    fun showDiscountPop(
        trigger: Boolean,
        isMember: Boolean = false,
        activity: Context,
        triggerType: Int? = 1,
        shwDown: ((data: DiscountInfoEntity) -> Unit?)? = null
    ) {
        if (trigger) {
            if (SDActivityManager.instance.lastActivity == null || SDActivityManager.instance.lastActivity.isDestroyed) {
                return
            }
            triggerDiscount(triggerType) {
                App.appInterface?.let { it1 ->
                    DiscountOfferDialog(
                        activity,
                        it
                    ).showPopupWindow()
                }
            }
        } else {
            getDiscountPopData(isMember) {
                when (it.havePop) {
                    DiscountInfoEntity.HavePop.NEED_POP.value -> {
                        App.appInterface?.let { it1 ->
                            DiscountOfferDialog(
                                activity,
                                it
                            ).showPopupWindow()
                        }
                    }

                    DiscountInfoEntity.HavePop.NOT_POP.value -> {
                        shwDown?.invoke(it)
                    }
                }
            }
        }
    }

    fun setOnClick(activity: Activity, viewDown: FrameLayout) {
        viewDown.setOnClickListener { MemberDialogShow.memberBuyShow(null, activity) }
    }

    fun convertDownTime(
        popOverTime: Long,
        countDownInterval: Long? = 1000,
        onTick: (hour: Long, minute: Long, second: Long) -> Unit,
        onFinish: () -> Unit
    ): MyCountDownTimer {
        val countDownTimer = MyCountDownTimer(
            popOverTime * 1000,
            countDownInterval ?: 1000
        ) { millisUntilFinished, isFinish ->
            if (!isFinish) {
                //正在倒计时
                val hours = millisUntilFinished / (1000 * 60 * 60)
                val minutes = millisUntilFinished % (1000 * 60 * 60) / (1000 * 60)
                val seconds = millisUntilFinished % (1000 * 60 * 60) % (1000 * 60) / 1000
                onTick.invoke(hours, minutes, seconds)
            } else {
                onFinish.invoke()
            }
        }
        countDownTimer.start()
        return countDownTimer
    }

    fun convertDownTimeStr(hour: Long, minute: Long, second: Long): String {
        var hourStr = ""
        var minuteStr = ""
        var secondStr = ""
        hourStr = if (hour.toString().length < 2) {
            "0${hour}"
        } else {
            hour.toString()
        }
        minuteStr = if (minute.toString().length < 2) {
            "0${minute}"
        } else {
            minute.toString()
        }
        secondStr = if (second.toString().length < 2) {
            "0${second}"
        } else {
            second.toString()
        }
        return if (hourStr.isNotEmpty()) {
            "$hourStr:$minuteStr:$secondStr"
        } else {
            "$minuteStr:$secondStr"
        }
    }

    class OnClickListener(val activity: Activity) : View.OnClickListener {
        override fun onClick(v: View?) {
            MemberDialogShow.memberBuyShow(null, activity)
        }

    }
}