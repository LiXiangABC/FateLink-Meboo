package com.crush.adapter

import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.crush.Constant
import com.custom.base.base.BaseRecyclerAdapter
import com.crush.R
import com.crush.entity.BaseEntity
import com.crush.entity.IMMatchEntity
import io.rong.imkit.event.EnumEventTag
import io.rong.imkit.event.FirebaseEventTag
import io.rong.imkit.utils.FirebaseEventUtils
import com.crush.util.GlideUtil
import com.crush.util.SystemUtils
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.github.jdsjlzx.util.SuperViewHolder
import com.sunday.eventbus.SDEventManager
import io.rong.imkit.utils.RouteUtils
import io.rong.imlib.model.Conversation

class IMMatchedAdapter(
    listModel: ArrayList<IMMatchEntity.Data>,
    var activity: Activity
) : BaseRecyclerAdapter<IMMatchEntity.Data>(listModel, activity) {
    override val layoutId: Int get() = R.layout.item_im_horizontal_match

    override fun onBindItemHolder(
        position: Int,
        holder: SuperViewHolder,
        model: IMMatchEntity.Data,
        payloads: List<Any>?
    ) {
        val itemUserAvatar = holder.getView<ImageView>(R.id.item_user_avatar)
        val itemIsNew = holder.getView<ImageView>(R.id.item_is_new)
        val itemOnline = holder.getView<View>(R.id.item_online)
        val itemNearby = holder.getView<TextView>(R.id.item_nearby)
        GlideUtil.setImageView(model.avatarUrl,itemUserAvatar)
        itemIsNew.visibility=if (model.newFlag==1) View.VISIBLE else View.GONE
        itemOnline.visibility=if (model.online==1) View.VISIBLE else View.GONE
        itemNearby.visibility=if (model.nearby==1) View.VISIBLE else View.GONE

//        if (model.nearby==1){
//            if (model.lat!= null) {
//                val gpsJson = SystemUtils.getGPS(mActivity)
//                if (gpsJson!= null) {
//                    if (gpsJson.get("lat") != null) {
//                        itemNearby.text = SystemUtils.calculateDistance(
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

//        if (model.newFlag==1){
//            OkHttpManager.instance.requestInterface(object :OkHttpFromBoy{
//                override fun addBody(requestBody: OkHttpBodyEntity) {
//                    requestBody.setPost(Constant.user_add_new_flag_url)
//                    requestBody.add("userCodeFriend",model.userCodeFriend)
//                    requestBody.add("newFlag",0)
//                }
//
//            },object : SDOkHttpResoutCallBack<BaseEntity>() {
//                override fun onSuccess(entity: BaseEntity) {
//                    SDEventManager.post(EnumEventTag.INDEX_MATCH_REFRESH_DATA.ordinal)
//                }
//            })
//        }
        holder.setOnClickListener {
            FirebaseEventUtils.logEvent(FirebaseEventTag.IM_Newfriend.name)
            if (model.newFlag==1) {
                OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                    override fun addBody(requestBody: OkHttpBodyEntity) {
                        requestBody.setPost(Constant.user_add_new_flag_url)
                        requestBody.add("userCodeFriend", model.userCodeFriend)
                        requestBody.add("newFlag", 0)
                    }

                }, object : SDOkHttpResoutCallBack<BaseEntity>() {
                    override fun onSuccess(entity: BaseEntity) {
                        SDEventManager.post(EnumEventTag.INDEX_MATCH_REFRESH_DATA.ordinal)
                    }
                })
            }
            RouteUtils.routeToConversationActivity(
                activity,
                Conversation.ConversationType.PRIVATE,
                model.userCodeFriend,
                true
            )
        }
    }
}