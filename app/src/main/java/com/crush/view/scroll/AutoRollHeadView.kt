package com.crush.view.scroll

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.crush.R
import com.crush.adapter.TriggerDiscountAdapter

/**
 * @Author ct
 * @Date 2024/4/17 17:36
 * 将主动触发折扣页面的三行滚动的头像封装成view
 */
class AutoRollHeadView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private var view: View? = null
    private var rvTriggerDiscountListOne: SocllRecyclerView? = null
    private var rvTriggerDiscountListTwo: SocllRecyclerView? = null
    private var rvTriggerDiscountListThree: SocllRecyclerView? = null

    init {
        initView()
    }

    private fun initView() {
        view = LayoutInflater.from(context).inflate(R.layout.layout_auto_roll_view, this)
        rvTriggerDiscountListOne =
            view?.findViewById(R.id.rvTriggerDiscountListOne)
        rvTriggerDiscountListTwo =
            view?.findViewById(R.id.rvTriggerDiscountListTwo)
        rvTriggerDiscountListThree =
            view?.findViewById(R.id.rvTriggerDiscountListThree)
    }

    fun setRollData(data:List<String>) {
        val avatarAdapter = TriggerDiscountAdapter(data)
        rvTriggerDiscountListOne?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvTriggerDiscountListOne?.addItemDecoration(CustomItemLeftDecoration(context))
        rvTriggerDiscountListOne?.adapter = avatarAdapter
        rvTriggerDiscountListOne?.start(2)

        val avatarTwoAdapter = TriggerDiscountAdapter(data)
        rvTriggerDiscountListTwo?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true)
        rvTriggerDiscountListTwo?.addItemDecoration(CustomItemRightDecoration(context))
        rvTriggerDiscountListTwo?.adapter = avatarTwoAdapter
        rvTriggerDiscountListTwo?.start(-2)

        val avatarThreeAdapter = TriggerDiscountAdapter(data)
        rvTriggerDiscountListThree?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvTriggerDiscountListThree?.addItemDecoration(CustomItemLeftDecoration(context))
        rvTriggerDiscountListThree?.adapter = avatarThreeAdapter
        rvTriggerDiscountListThree?.start(2)
    }

    fun releaseData(){
        rvTriggerDiscountListOne?.stop()
        rvTriggerDiscountListTwo?.stop()
        rvTriggerDiscountListThree?.stop()
    }
}