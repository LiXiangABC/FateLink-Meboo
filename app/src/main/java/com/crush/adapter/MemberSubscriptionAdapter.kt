package com.crush.adapter

import android.app.Activity
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.crush.R
import io.rong.imkit.entity.MemberSubscribeEntity
import com.custom.base.base.BaseRecyclerAdapter
import com.github.jdsjlzx.util.SuperViewHolder
import io.rong.imkit.utils.RongUtils

class MemberSubscriptionAdapter(
    listModel: ArrayList<MemberSubscribeEntity.Data.Subscriptions>,
    activity: Activity
) : BaseRecyclerAdapter<MemberSubscribeEntity.Data.Subscriptions>(listModel, activity){
    override val layoutId: Int
        get() = R.layout.item_member_subscription

    override fun onBindItemHolder(position: Int, holder: SuperViewHolder, model: MemberSubscribeEntity.Data.Subscriptions, payloads: List<Any>?) {
        val itemLogo = holder.getView<ImageView>(R.id.item_logo)
        val itemTitle = holder.getView<TextView>(R.id.item_title)
        val itemContent = holder.getView<TextView>(R.id.item_content)
        if (!RongUtils.isDestroy(mActivity)) {
            Glide.with(mActivity)
                .load(model.icon)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(itemLogo)
        }
        itemTitle.text=model.tip
        itemContent.text=model.content
    }
}