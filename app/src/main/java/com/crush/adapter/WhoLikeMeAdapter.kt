package com.crush.adapter

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import com.crush.R
import com.crush.bean.WLMListBean
import com.crush.callback.WLMSwipedCallBack
import com.crush.ui.chat.ktl.WlmClick
import com.crush.ui.chat.profile.UserProfileInfoActivity
import com.crush.util.GlideUtil

import com.crush.view.TagCloudView
import com.crush.view.delay.DelayClickImageView
import com.custom.base.base.BaseRecyclerAdapter
import com.crush.util.IntentUtil
import com.github.jdsjlzx.util.SuperViewHolder
import io.rong.imkit.event.FirebaseEventTag
import io.rong.imkit.utils.FirebaseEventUtils

class WhoLikeMeAdapter(
    listModel: ArrayList<WLMListBean>,
    mActivity: Activity,

    var wLMSwipedCallBack: WLMSwipedCallBack
) : BaseRecyclerAdapter<WLMListBean>(listModel, mActivity) {
    override val layoutId: Int get() = R.layout.item_who_like_me
    private var isMember: Boolean = false

    fun setMember(member: Boolean) {
        this.isMember = member
    }

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
        val itemUserDislike = holder.getView<DelayClickImageView>(R.id.item_user_dislike)
        val itemUserLike = holder.getView<DelayClickImageView>(R.id.item_user_like)
        val itemNoMember = holder.getView<ImageView>(R.id.item_no_member)
        val turnOnsContainer = holder.getView<LinearLayout>(R.id.turn_ons_container)
        val txtTurnOnsSize = holder.getView<TextView>(R.id.txt_turn_ons_size)
        GlideUtil.setImageView(
            model.avatarUrl,
            itemUserBg,
            !isMember,
        )
        itemNoMember.visibility = if (isMember) View.GONE else View.VISIBLE

        if (model.turnOnsListSize != null && model.turnOnsListSize > 0) {
            turnOnsContainer.visibility = View.VISIBLE
            txtTurnOnsSize.text =
                "${model.turnOnsListSize} ${mActivity.getString(R.string.turns_ons)}"
            itemTagCloud.visibility=View.GONE
        } else {
            turnOnsContainer.visibility = View.GONE
            itemTagCloud.visibility = View.VISIBLE
        }

        itemUserDislike.visibility = if (isMember) View.VISIBLE else View.GONE
        itemUserLike.visibility = if (isMember) View.VISIBLE else View.GONE

        itemUserIsNew.visibility = if (model.newFlag == 1) View.VISIBLE else View.GONE
        itemOnline.visibility = if (model.online == 1) View.VISIBLE else View.GONE
        itemPositioning.visibility = if (model.nearby == 1) View.VISIBLE else View.GONE
//        if (model.nearby==1){
//            if (model.lat!= null) {
//                val gpsJson = SystemUtils.getGPS(mActivity)
//                if(gpsJson!=null) {
//                    if (gpsJson.get("lat") != null) {
//                        itemPositioning.text = SystemUtils.calculateDistance(
//                            mActivity,
//                            gpsJson.get("lat").asDouble,
//                            gpsJson.get("lng").asDouble,
//                            model.lat.toDouble(),
//                            model.lng.toDouble()
//                        )
//                    }
//                }
//            }
//        }

        itemTagCloud.setTagBeans(model.interests,false)
        itemName.text = "${model.nickName}, ${model.age}"
        itemUserDislike.setOnClickListener {
            FirebaseEventUtils.logEvent(FirebaseEventTag.WLM_Pass.name)
            wLMSwipedCallBack.swipedCallback(ItemTouchHelper.START, model)
        }
        itemUserLike.setOnClickListener {
            FirebaseEventUtils.logEvent(FirebaseEventTag.WLM_Like.name)
            wLMSwipedCallBack.swipedCallback(ItemTouchHelper.END, model)
        }
        holder.getView<View>(R.id.item_right_overlay).alpha = 0f
        holder.getView<View>(R.id.item_left_overlay).alpha = 0f

        (holder.getView<View>(R.id.item_user_dislike) as ImageView).setImageResource(
            R.drawable.selector_user_dislike_click_status_img_wlm
        )
        holder.getView<View>(R.id.item_user_dislike)
            .setBackgroundResource(R.drawable.selector_user_dislike_click_status_transfer)

        (holder.getView<View>(R.id.item_user_like) as ImageView).setImageResource(
            R.drawable.selector_user_like_click_status_img
        )
        holder.getView<View>(R.id.item_user_like)
            .setBackgroundResource(R.drawable.selector_user_like_click_status_transfer)


        turnOnsContainer.setOnClickListener {
            if (isMember) {
                val bundle = Bundle()
                bundle.putBoolean("isWlm", true)
                bundle.putInt("selectPosition", position)
                bundle.putString("userCodeFriend", model.userCodeFriend)
                bundle.putBoolean("turnOpen", true)
                IntentUtil.startActivity(UserProfileInfoActivity::class.java, bundle)
            }
        }

        holder.setOnClickListener {
            if (isMember)
                FirebaseEventUtils.logEvent(FirebaseEventTag.WLM_Profile.name)
            WlmClick.itemClick(isMember,position,model,wLMSwipedCallBack)
        }

    }
}