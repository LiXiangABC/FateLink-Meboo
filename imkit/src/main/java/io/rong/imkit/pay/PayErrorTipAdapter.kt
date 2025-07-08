package io.rong.imkit.pay

import android.app.Activity
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.custom.base.base.BaseRecyclerAdapter
import com.github.jdsjlzx.util.SuperViewHolder
import io.rong.imkit.R
import io.rong.imkit.entity.PayErrorBean

/**
 * 支付报错提示
 */
class PayErrorTipAdapter(
    listModel: ArrayList<PayErrorBean>,
    mActivity: Activity,
) : BaseRecyclerAdapter<PayErrorBean>(listModel, mActivity) {
    override val layoutId: Int get() = R.layout.item_pay_error_tip

    override fun onBindItemHolder(position: Int, holder: SuperViewHolder, model: PayErrorBean, payloads: List<Any>?) {

        val imgItemPayErrorTip = holder.getView<ImageView>(R.id.imgItemPayErrorTip)
        val txtItemPayErrorTipTitle = holder.getView<TextView>(R.id.txtItemPayErrorTipTitle)
        val txtItemPayErrorTipContent = holder.getView<TextView>(R.id.txtItemPayErrorTipContent)

        model.icon?.let { imgItemPayErrorTip.setImageResource(it) }
        txtItemPayErrorTipTitle.visibility = if(TextUtils.isEmpty(model.title)) View.GONE else View.VISIBLE
        txtItemPayErrorTipTitle.text = model.title
        txtItemPayErrorTipContent.text = model.content
    }
}