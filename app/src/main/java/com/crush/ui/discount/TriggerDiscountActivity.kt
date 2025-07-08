package com.crush.ui.discount

import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import com.crush.R
import com.crush.view.scroll.AutoRollHeadView
import com.crush.mvp.MVPBaseActivity
import com.yalantis.ucrop.util.DensityUtil


/**
 * @Author ct
 * @Date 2024/4/12 16:17
 * app主动触发折扣页面
 */
class TriggerDiscountActivity :
    MVPBaseActivity<TriggerDiscountContact.View, TrriggerDiscountPresenter>(),
    TriggerDiscountContact.View {

    private lateinit var conTriggerDiscountMain:ConstraintLayout
    private lateinit var layoutAutoRollView: AutoRollHeadView
    override fun bindLayout(): Int {
        return R.layout.act_trigger_discount
    }

    override fun setFullScreen(): Boolean {
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        conTriggerDiscountMain = findViewById(R.id.conTriggerDiscountContainer)
        layoutAutoRollView = findViewById(R.id.layoutAutoRollView)
        conTriggerDiscountMain.setPadding(
            0,
            DensityUtil.getStatusBarHeight(this),
            0,
            0
        )
        initMarqueeViewOne()
    }

    //第一行 从右向左无限循环自动滚动
    private fun initMarqueeViewOne() {
        val data: MutableList<String> = ArrayList()
        data.add("https://img1.baidu.com/it/u=3227500530,2626385610&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=666")
        data.add("https://img2.baidu.com/it/u=54151015,210753294&fm=253&fmt=auto&app=120&f=JPEG?w=501&h=500")
        data.add("https://img2.baidu.com/it/u=2955298602,2265234608&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500")
        data.add("https://img1.baidu.com/it/u=32271518,2422269068&fm=253&fmt=auto&app=120&f=JPEG?w=500&h=500")
        data.add("https://img1.baidu.com/it/u=4115153720,3555391873&fm=253&fmt=auto&app=120&f=JPEG?w=508&h=500")
        data.add("https://img2.baidu.com/it/u=1777485367,2542994700&fm=253&fmt=auto&app=120&f=JPEG?w=500&h=500")
        data.add("https://img2.baidu.com/it/u=2543260500,2324568568&fm=253&fmt=auto&app=120&f=JPEG?w=500&h=500")
        data.add("https://img0.baidu.com/it/u=1799335169,3957877179&fm=253&fmt=auto&app=120&f=JPEG?w=508&h=500")
        data.add("https://img1.baidu.com/it/u=3329997993,3269402176&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500")
        data.add("https://img1.baidu.com/it/u=3368153730,3760679186&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=532")
        layoutAutoRollView.setRollData(data)
    }

    override fun onDestroy() {
        super.onDestroy()
        layoutAutoRollView.releaseData()
    }
}