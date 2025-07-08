package io.rong.imkit.adapter

import android.app.Activity
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

import com.custom.base.base.BaseRecyclerAdapter
import com.github.jdsjlzx.util.SuperViewHolder
import io.rong.imkit.R
import io.rong.imkit.entity.TurnOnsListBean
import io.rong.imkit.utils.RongUtils

class ChatTurnOnsAdapter(
    listModel: ArrayList<TurnOnsListBean>,
    mActivity: Activity,
) : BaseRecyclerAdapter<TurnOnsListBean>(listModel, mActivity) {
    override val layoutId: Int get() = R.layout.item_chat_turn_ons

    override fun onBindItemHolder(position: Int, holder: SuperViewHolder, model: TurnOnsListBean, payloads: List<Any>?) {
        val imgTurnOns = holder.getView<ImageView>(R.id.img_turn_ons)
        val turnOnsTitle = holder.getView<TextView>(R.id.turn_ons_title)
        if (!RongUtils.isDestroy(mActivity)){
            Glide.with(mActivity)
                .load(model.imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgTurnOns)
        }

        turnOnsTitle.text=model.name
    }
}