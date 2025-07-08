package io.rong.imkit.adapter

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import io.rong.imkit.entity.MemberSubscribeEntity
import com.custom.base.base.BaseRecyclerContextAdapter
import com.github.jdsjlzx.util.SuperViewHolder
import io.rong.imkit.R

class MemberSubscriptionAdapter(
    listModel: ArrayList<MemberSubscribeEntity.Data.Subscriptions>,
    activity: Context
) : BaseRecyclerContextAdapter<MemberSubscribeEntity.Data.Subscriptions>(listModel, activity){
    override val layoutId: Int
        get() = R.layout.item_member_subscription

    override fun onBindItemHolder(position: Int, holder: SuperViewHolder, model: MemberSubscribeEntity.Data.Subscriptions, payloads: List<Any>?) {
        val itemLogo = holder.getView<ImageView>(R.id.item_logo)
        val itemTitle = holder.getView<TextView>(R.id.item_title)
        val itemContent = holder.getView<TextView>(R.id.item_content)
        Glide.with(mActivity)
            .load(model.icon)
            .error(R.drawable.image_error)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(itemLogo)
        itemTitle.text=model.tip
        itemContent.text=model.content
    }
}