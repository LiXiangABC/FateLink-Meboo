package com.crush.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crush.R
import com.crush.bean.UserWantOrYouAcceptBean
import com.custom.base.base.BaseRecyclerAdapter
import com.github.jdsjlzx.util.SuperViewHolder
import io.rong.imkit.dialog.CustomDialog
import java.util.stream.Collectors

class CustomSelectorDialog {
    private var activity: Activity? = null
    private var newDataList = ArrayList<UserWantOrYouAcceptBean>()
    private var onClickListener: OnClickListener? = null
    private var choice: Boolean? = null
    private var title: String? = null
    fun buildConfig(activity: Activity): CustomSelectorDialog {
        this.activity = activity
        return this
    }

    fun setTitle(title: String?): CustomSelectorDialog {
        this.title = title
        return this
    }

    fun setDatas(dataList: MutableList<UserWantOrYouAcceptBean>?): CustomSelectorDialog {
        this.newDataList.clear()
        dataList?.stream()?.forEach {
            val userWantOrYouAcceptBean = UserWantOrYouAcceptBean(it.type,it.code,it.value,it.iconUrl,it.selected,it.extendInfo)
            this.newDataList.add(userWantOrYouAcceptBean)
        }
        return this
    }

    fun setOnClickListener(onClickListener: OnClickListener): CustomSelectorDialog {
        this.onClickListener = onClickListener
        return this
    }

    /**
     * @param choice 默认单选
     */
    fun setMultipleChoice(choice: Boolean = false): CustomSelectorDialog {
        this.choice = choice
        return this
    }


    interface OnClickListener {
        /**
         * @param allDataList 所有数据
         * @param selectDataList 选中数据
         */
        fun save(dialog :CustomSelectorDialog,allDataList: MutableList<UserWantOrYouAcceptBean>?,selectDataList: MutableList<UserWantOrYouAcceptBean>?)
        fun close()
        fun selected()
    }

    fun show(): CustomSelectorDialog {
        activity?.let {
            CustomDialog(it)
                .setLayoutId(R.layout.dialog_custom_selector)
                .setControllerListener { dialog ->
                    val tvTitle = dialog.findViewById<TextView>(R.id.tv_title)
                    tvTitle.text = title
                    val recyclerview = dialog.findViewById<RecyclerView>(R.id.recyclerview)
                    val layoutManager = LinearLayoutManager(it)
                    recyclerview.layoutManager = layoutManager
                    val adapter = Adapter(it, newDataList, choice, onClickListener)
                    recyclerview.adapter = adapter
                    dialog.findViewById<View>(R.id.iv_close).setOnClickListener {
                        dialog.dismiss()
                        onClickListener?.close()
                    }
                    dialog.findViewById<View>(R.id.tv_save).setOnClickListener {
                        onClickListener?.save(this@CustomSelectorDialog,adapter.dataList.toMutableList(),
                            adapter.dataList.stream().filter { v -> v.selected == 1 }?.collect(Collectors.toList())
                        )
                        dialog.dismiss()
                    }
                }.show()
                .setPopupGravity(Gravity.BOTTOM)

        }
        return this
    }

    class Adapter(
        val activity: Activity,
        val listModel: ArrayList<UserWantOrYouAcceptBean>,
        val choice: Boolean?,
        val onClickListener: OnClickListener?
    ) :
        BaseRecyclerAdapter<UserWantOrYouAcceptBean>(listModel, activity) {
        override val layoutId: Int
            get() = R.layout.item_custom_selector

        override fun onBindItemHolder(
            position: Int,
            holder: SuperViewHolder,
            bean: UserWantOrYouAcceptBean,
            payloads: List<Any>?
        ) {

            val itemText = holder.getView<TextView>(R.id.item_text)
            itemText.text = bean.value
            itemText.setTextColor(if (bean.selected == 1) Color.WHITE else Color.parseColor("#202323"))
            itemText.setBackgroundResource(if (bean.selected == 1) R.drawable.shape_custom_selector_select else R.drawable.shape_custom_selector_unselect)
            itemText.setOnClickListener {
                upDateStatus(position)
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        fun upDateStatus(position: Int): Boolean {
            if (choice == true) {
                //多选
                var rSelected = 0
                listModel.forEach {
                    if (it.selected == 1) {
                        rSelected++
                    }
                }
                val selected = listModel.get(position).selected
                if (!(rSelected < 2 && selected == 1)) {
                    listModel.get(position).selected = if (selected == 0) 1 else 0
                    notifyDataSetChanged()
                    return true
                } else {
                    return false
                }
            } else {
                //单选
                listModel.stream().forEach {
                    it.selected = 0
                }
                listModel.get(position).selected = 1
                notifyDataSetChanged()
                return true
            }
        }

    }

}