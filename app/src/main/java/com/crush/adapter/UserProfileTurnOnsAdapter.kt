package com.crush.adapter

import android.app.Activity
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.crush.R
import com.crush.bean.TurnOnsListBean
import com.crush.util.GlideUtil

import com.custom.base.base.BaseRecyclerAdapter
import com.github.jdsjlzx.util.SuperViewHolder

class UserProfileTurnOnsAdapter(
    listModel: ArrayList<TurnOnsListBean>,
    mActivity: Activity,
    var listener:OnListener
) : BaseRecyclerAdapter<TurnOnsListBean>(listModel, mActivity) {
    override val layoutId: Int get() = R.layout.item_user_profile_turn_ons

    override fun onBindItemHolder(position: Int, holder: SuperViewHolder, model: TurnOnsListBean, payloads: List<Any>?) {
        val imgTurnOns = holder.getView<ImageView>(R.id.img_turn_ons)
        val turnOnsTitle = holder.getView<TextView>(R.id.turn_ons_title)
        val turnOnsContent = holder.getView<TextView>(R.id.turn_ons_content)
        val turnOnsContainer = holder.getView<ConstraintLayout>(R.id.turn_ons_container)
        GlideUtil.setImageView(model.imageUrl, imgTurnOns, placeholderImageId = 0)

        turnOnsTitle.text=model.title
        turnOnsContent.text=model.content
        turnOnsContainer.setBackgroundResource(if (model.selected==0)R.drawable.shape_solid_gray_radius_12 else R.drawable.shape_solid_yellow_radius_12)

        holder.setOnClickListener {
            listener.onListener(position)
        }

    }
    interface OnListener{
        fun onListener(position: Int)
    }
}