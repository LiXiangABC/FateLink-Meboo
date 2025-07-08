package io.rong.imkit.adapter

import android.app.Activity
import android.widget.TextView
import com.custom.base.base.BaseRecyclerAdapter
import com.github.jdsjlzx.util.SuperViewHolder
import io.rong.imkit.R

class ReportTextAdapter(
    listModel: ArrayList<String>,
    mActivity: Activity,
    var onCallBack: OnCallBack
) : BaseRecyclerAdapter<String>(listModel, mActivity) {
    override val layoutId: Int get() = R.layout.item_report_text

    override fun onBindItemHolder(
        position: Int,
        holder: SuperViewHolder,
        text: String,
        payloads: List<Any>?
    ) {
        val itemText = holder.getView<TextView>(R.id.item_text)
        itemText.text=text
        holder.setOnClickListener {
            onCallBack.callBack(text)
        }
    }

    interface OnCallBack{
        fun callBack(text:String)
    }
}