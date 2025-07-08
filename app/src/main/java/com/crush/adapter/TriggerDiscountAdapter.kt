package com.crush.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.crush.R
import com.crush.util.GlideUtil
import com.makeramen.roundedimageview.RoundedImageView

/**
 * @Author ct
 * @Date 2024/4/17 11:11
 */
class TriggerDiscountAdapter(private val avatarList: List<String>) :
    RecyclerView.Adapter<TriggerDiscountAdapter.AvatarViewHolder>() {

    class AvatarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: RoundedImageView = itemView.findViewById(R.id.imgItemTriggerDiscountHead)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trigger_discount_head, parent, false)
        return AvatarViewHolder(view)
    }

    override fun onBindViewHolder(holder: AvatarViewHolder, position: Int) {
        GlideUtil.setImageView(avatarList[position % avatarList.size], holder.imageView)
    }

    override fun getItemCount(): Int {
        return Integer.MAX_VALUE
    }

}