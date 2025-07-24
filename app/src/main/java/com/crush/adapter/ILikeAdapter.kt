package com.crush.adapter

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.crush.R
import io.rong.imkit.entity.WLMListBean
import com.crush.ui.chat.UpSourceEnum
import com.crush.ui.chat.profile.UserProfileInfoActivity
import com.crush.ui.index.flash.FlashChatActivity
import com.crush.util.GlideUtil

import com.crush.view.TagCloudView
import com.crush.view.delay.DelayClickImageView
import com.custom.base.base.BaseRecyclerAdapter
import com.crush.util.IntentUtil
import com.github.jdsjlzx.util.SuperViewHolder

class ILikeAdapter(
    listModel: ArrayList<WLMListBean>,
    mActivity: Activity,
) : BaseRecyclerAdapter<WLMListBean>(listModel, mActivity) {
    override val layoutId: Int get() = R.layout.item_i_like
    override fun onBindItemHolder(
        position: Int,
        holder: SuperViewHolder,
        model: WLMListBean,
        payloads: List<Any>?
    ) {
        val itemUserBg = holder.getView<ImageView>(R.id.item_user_bg)
        val itemUserIsNew = holder.getView<ImageView>(R.id.item_user_is_new)
        val itemOnline = holder.getView<TextView>(R.id.item_online)
        val itemPositioning = holder.getView<TextView>(R.id.item_positioning)
        val itemName = holder.getView<TextView>(R.id.item_name)
        val itemTagCloud = holder.getView<TagCloudView>(R.id.item_tag_cloud)
        val itemUserChat = holder.getView<DelayClickImageView>(R.id.item_user_chat)
        val turnOnsContainer = holder.getView<LinearLayout>(R.id.turn_ons_container)
        val txtTurnOnsSize = holder.getView<TextView>(R.id.txt_turn_ons_size)
        GlideUtil.setImageView(
            model.avatarUrl,
            itemUserBg
        )

        if (model.turnOnsListSize != null && model.turnOnsListSize!! > 0) {
            turnOnsContainer.visibility = View.VISIBLE
            txtTurnOnsSize.text =
                "${model.turnOnsListSize} ${mActivity.getString(R.string.turns_ons)}"
            itemTagCloud.visibility=View.GONE
        } else {
            turnOnsContainer.visibility = View.GONE
            itemTagCloud.visibility = View.VISIBLE
        }

        itemUserIsNew.visibility = if (model.newFlag == 1) View.VISIBLE else View.GONE
        itemOnline.visibility = if (model.online == 1) View.VISIBLE else View.GONE
        itemPositioning.visibility = if (model.nearby == 1) View.VISIBLE else View.GONE


        itemTagCloud.setTagBeans(model.interests,false)
        itemName.text = "${model.nickName}, ${model.age}"
        itemUserChat.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("userName",model.nickName)
            bundle.putString("userCode",model.userCode)
            bundle.putString("avatar", model.avatarUrl)
            bundle.putInt("position", position)
            bundle.putString(UpSourceEnum.SOURCE.name, UpSourceEnum.HOME.name)
            IntentUtil.startActivity(FlashChatActivity::class.java, bundle)
        }

        turnOnsContainer.setOnClickListener {
                val bundle = Bundle()
                bundle.putInt("selectPosition", position)
                bundle.putString("userCodeFriend", model.userCode)
                bundle.putBoolean("turnOpen", true)
                bundle.putBoolean("isILike", true)
                IntentUtil.startActivity(UserProfileInfoActivity::class.java, bundle)
        }

        holder.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("isILike", true)
            bundle.putInt("selectPosition", position)
            bundle.putString("userCodeFriend", model.userCode)
            IntentUtil.startActivity(UserProfileInfoActivity::class.java, bundle)
        }

    }
}