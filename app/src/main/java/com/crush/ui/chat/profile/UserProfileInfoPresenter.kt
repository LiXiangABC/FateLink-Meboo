package com.crush.ui.chat.profile

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ImageSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.crush.Constant
import com.crush.R
import com.crush.adapter.PageLoaderStringAdapter
import com.crush.adapter.UserProfileTurnOnsAdapter
import com.crush.callback.EmptyCallBack
import com.crush.dialog.UserProfileOperationDialog
import com.crush.entity.BaseEntity
import com.crush.entity.MatchResultEntity
import com.crush.entity.UserProfileInfoEntity
import com.crush.ui.index.flash.FlashChatActivity
import com.crush.ui.index.match.MatchUserActivity
import com.crush.util.CollectionUtils
import com.crush.util.DateUtils
import com.crush.util.DensityUtil
import com.crush.view.Loading.LoadingDialog
import com.custom.base.config.BaseConfig
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.crush.mvp.BasePresenterImpl
import com.crush.util.IntentUtil
import com.sunday.eventbus.SDEventManager
import com.youth.banner.config.IndicatorConfig
import com.youth.banner.indicator.CircleIndicator
import com.youth.banner.util.BannerUtils
import io.rong.imkit.IMCenter
import io.rong.imkit.SpName
import io.rong.imkit.dialog.MemberBuyDialog
import io.rong.imkit.dialog.UserProfileOperationReportDialog
import io.rong.imkit.entity.MemberSubscribeEntity
import io.rong.imkit.entity.OrderCreateEntity
import io.rong.imkit.event.EnumEventTag
import io.rong.imkit.event.FirebaseEventTag
import io.rong.imkit.pay.EmptySuccessCallBack
import io.rong.imkit.pay.PayUtils
import io.rong.imkit.utils.FirebaseEventUtils
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.stream.Collectors


/**
 * 作者：
 * 时间：
 * 描述：
 */
class UserProfileInfoPresenter : BasePresenterImpl<UserProfileInfoContract.View>(),
    UserProfileInfoContract.Presenter {

    fun addIconsToText(text: String, icon1: Drawable?, icon2: Drawable): SpannableStringBuilder {
        val spannableStringBuilder = SpannableStringBuilder(text)

        // 设置图标的宽高
        val iconWidth = DensityUtil.dp2px(mActivity,23f)
        val iconHeight = DensityUtil.dp2px(mActivity,23f)
        if (icon1!= null) {
            icon1.setBounds(0, 0, iconWidth, iconHeight)
            val imageSpan1 = ImageSpan(icon1, ImageSpan.ALIGN_BASELINE)
            spannableStringBuilder.append("  ")
            spannableStringBuilder.setSpan(imageSpan1, spannableStringBuilder.length - 1, spannableStringBuilder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        icon2.setBounds(0, 0, iconWidth, iconHeight)
        val imageSpan2 = ImageSpan(icon2, ImageSpan.ALIGN_BASELINE)
        spannableStringBuilder.append("  ")
        spannableStringBuilder.setSpan(imageSpan2, spannableStringBuilder.length - 1, spannableStringBuilder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        return spannableStringBuilder
    }

    override fun initBundle(bundle: Bundle) {
        mView?.apply {
            userProfileLike.visibility =
                if (bundle.getBoolean("isWlm") || bundle.getBoolean("isIndex")) View.VISIBLE else View.INVISIBLE
            userProfileChat.visibility =
                if (bundle.getBoolean("isIndex")|| bundle.getBoolean("isILike")) View.VISIBLE else View.INVISIBLE
            userProfileDislike.visibility =
                if (bundle.getBoolean("isWlm") || bundle.getBoolean("isIndex")) View.VISIBLE else View.INVISIBLE

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    LoadingDialog.showLoading(mActivity)
                    OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                        override fun addBody(requestBody: OkHttpBodyEntity) {
                            requestBody.setPost(Constant.user_user_info_url)
                            requestBody.add("userCode", bundle.getString("userCodeFriend", ""))
                        }
                    }, object : SDOkHttpResoutCallBack<UserProfileInfoEntity>() {
                        override fun onSuccess(entity: UserProfileInfoEntity) {
                            LoadingDialog.dismissLoading(mActivity)
                            val indicatorConfig = IndicatorConfig()
                            indicatorConfig.selectedWidth = 10
                            indicatorConfig.normalWidth=10
                            indicatorConfig.selectedColor=Color.RED
                            val circleIndicator = CircleIndicator(mActivity)
                            banner.indicator = circleIndicator

                            banner.setIndicatorMargins(
                                IndicatorConfig.Margins(
                                    0, 0,0, BannerUtils.dp2px(17f).toInt()
                                )
                            )
                            banner.adapter = PageLoaderStringAdapter(mActivity, entity.data.imagesV2)
                            userProfileOnline.visibility = if (entity.data.online == 1) View.VISIBLE else View.GONE
                            userProfilePositioning.visibility = if (entity.data.nearby == 1) View.VISIBLE else View.GONE

                            setStarSign(entity)


                            val drawable = ContextCompat.getDrawable(mActivity, R.mipmap.icon_user_member_logo_index)
                            val drawable1 =ContextCompat.getDrawable(mActivity,  if (entity.data.gender == 1) R.mipmap.icon_gender_male_small else if (entity.data.gender == 2) R.mipmap.icon_gender_female_small else R.mipmap.icon_gender_unknown_small)
                            userProfileName.text = addIconsToText("${entity.data.nickName} · ${entity.data.age}",if (entity.data.userType ==2) drawable else null,drawable1!!)

                            userProfileLocation.visibility=if (entity.data.city==null) View.GONE else View.VISIBLE
                            if (entity.data.city!= null) {
                                userProfileLocation.text = "\uD83D\uDCCD ${entity.data.city}, ${entity.data.state}, \uD83C\uDDFA\uD83C\uDDF8"
                            }



                            userProfileIntroduction.text = entity.data.aboutMe

                            userProfileHeight.visibility=if (entity.data.inchHeight != null) View.VISIBLE else View.GONE
                            userProfileHeight.text = entity.data.inchHeight


//                    if (entity.data.socialConnections != null) {
//                        userProfilePurpose.text = entity.data.socialConnections
//                    }
                            val userWant =
                                entity.data.userWant?.stream()?.filter { v -> v.selected == 1 }?.collect(
                                    Collectors.toList()
                                )
                            if (userWant != null && userWant.size > 0) {
                                userProfileUserWant.text = userWant[0].value
                            }

                            userProfileTagCloud.visibility =
                                if (CollectionUtils.isEmpty(entity.data.interests)) View.GONE else View.VISIBLE
                            if (CollectionUtils.isNotEmpty(entity.data.interests)) {
                                userProfileTagCloud.setTags(entity.data.interests)
                            }

                            turnOnsList.visibility = if (CollectionUtils.isEmpty(entity.data.turnOnsList)) View.GONE else View.VISIBLE
                            userProfileTurnOnsTitle.visibility = if (CollectionUtils.isEmpty(entity.data.turnOnsList)) View.GONE else View.VISIBLE
                            if (CollectionUtils.isNotEmpty(entity.data.turnOnsList)) {
                                turnOnsList.layoutManager = GridLayoutManager(mActivity, 2)
                                (turnOnsList.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
                                val turnOnsAdapter = UserProfileTurnOnsAdapter(entity.data.turnOnsList, mActivity,
                                    object : UserProfileTurnOnsAdapter.OnListener {
                                        override fun onListener(position: Int) {
                                        }
                                    })
                                turnOnsList.adapter = turnOnsAdapter
                                if (bundle.getBoolean("turnOpen")) {
                                    Handler().postDelayed({
                                        val scrollToY: Int = userProfileTurnOnsTitle.top
                                        scrollView.smoothScrollBy(0, scrollToY)
                                    },100)
                                }
                            }



                            userProfileChat.setOnClickListener {
                                val bun = Bundle()
                                bun.putString("userName", entity.data.nickName)
                                bun.putString("userCode", entity.data.userCode)
                                bun.putString("avatar", entity.data.avatarUrl)
                                IntentUtil.startActivity(FlashChatActivity::class.java, bun)
                                FirebaseEventUtils.logEvent(FirebaseEventTag.Home_Profile_Flashchat.name)
                            }

                            userProfileLike.setOnClickListener {
                                if (bundle.getBoolean("isWlm")) {
                                    FirebaseEventUtils.logEvent(FirebaseEventTag.WLM_Profile_Like.name)
                                    benefitsReduceWLM(
                                        bundle.getString("userCodeFriend", ""),
                                        1,
                                        object : EmptyCallBack {
                                            override fun OnSuccessListener() {
                                                OkHttpManager.instance.requestInterface(object :
                                                    OkHttpFromBoy {
                                                    override fun addBody(requestBody: OkHttpBodyEntity) {
                                                        requestBody.setPost(Constant.user_add_wlm_url)
                                                        requestBody.add("likeType", 1)
                                                        requestBody.add(
                                                            "userCodeFriend",
                                                            bundle.getString("userCodeFriend", "")
                                                        )
                                                        requestBody.add("source", 2)
                                                    }
                                                }, object : SDOkHttpResoutCallBack<MatchResultEntity>() {
                                                    override fun onSuccess(entityMatch: MatchResultEntity) {
                                                        SDEventManager.post(
                                                            bundle.getInt("selectPosition"),
                                                            EnumEventTag.WLM_LIKE_SWIPED.ordinal
                                                        )
                                                        if (entityMatch.data.matched) {
                                                            val userCodeFriend = bundle.getString(
                                                                "userCodeFriend",
                                                                ""
                                                            )
                                                            val newBundle = Bundle()
                                                            newBundle.putString(
                                                                "userCodeFriend",
                                                                userCodeFriend
                                                            )
                                                            newBundle.putString(
                                                                "avatarUrl",
                                                                entity.data.avatarUrl
                                                            )
                                                            IntentUtil.startActivity(
                                                                MatchUserActivity::class.java,
                                                                newBundle
                                                            )
                                                        }
                                                        finish()
                                                    }

                                                    override fun onFailure(code: Int, msg: String) {
                                                    }
                                                }, isShowDialog = false)
                                            }

                                            override fun OnFailListener() {

                                            }

                                        })

                                } else {
                                    FirebaseEventUtils.logEvent(FirebaseEventTag.Home_Profile_Like.name)
                                    SDEventManager.post(EnumEventTag.INDEX_LIKE_SWIPED.ordinal)
                                    finish()
                                }
                            }
                        }


                        override fun onFailure(code: Int, msg: String) {
                            LoadingDialog.dismissLoading(mActivity)
                        }
                    })

                } catch (e: Exception) {
                    // 处理异常
                    e.printStackTrace()
                }
            }



            containerMoreOperation.setOnClickListener {
                FirebaseEventUtils.logEvent(if (!bundle.getBoolean("isWlm")) FirebaseEventTag.Home_Profile_More.name else FirebaseEventTag.WLM_Profile_More.name)
                UserProfileOperationDialog(
                    mActivity,
                    bundle,
                    object : UserProfileOperationDialog.UserActionListener {
                        override fun userReport() {
                            FirebaseEventUtils.logEvent(if (!bundle.getBoolean("isWlm")) FirebaseEventTag.Home_Profile_Report.name else FirebaseEventTag.WLM_Profile_Report.name)
                            Handler().postDelayed({
                                UserProfileOperationReportDialog(
                                    mActivity,
                                    bundle.getString("userCodeFriend", ""),
                                    bundle.getBoolean("isWlm"),
                                    object : UserProfileOperationReportDialog.OnListener {
                                        override fun userReport() {
                                            FirebaseEventUtils.logEvent(if (!bundle.getBoolean("isWlm")) FirebaseEventTag.Home_Profile_Reportsuccess.name else FirebaseEventTag.WLM_Profile_Reportsuccess.name)
                                            if (bundle.getBoolean("isIndex")) {
                                                Handler().postDelayed({
                                                    SDEventManager.post(EnumEventTag.INDEX_DISLIKE_SWIPED.ordinal)
                                                    finish()
                                                }, 500)

                                            }
                                        }

                                    }).showPopupWindow()
                            }, 200)
                        }

                        override fun userBlack() {
                            FirebaseEventUtils.logEvent(if (!bundle.getBoolean("isWlm")) FirebaseEventTag.Home_Profile_Block.name else FirebaseEventTag.WLM_Profile_Block.name)
                            if (bundle.getBoolean("isWlm")) {
                                SDEventManager.post(
                                    bundle.getInt("selectPosition"),
                                    EnumEventTag.WLM_DISLIKE_SWIPED.ordinal
                                )
                            } else {
                                if (bundle.getBoolean("isIndex")) {
                                    SDEventManager.post(EnumEventTag.INDEX_DISLIKE_SWIPED.ordinal)
                                }
                            }
                            IMCenter.getInstance()
                                .removeConversation(
                                    Conversation.ConversationType.PRIVATE,
                                    bundle.getString("userCodeFriend", ""),
                                    null
                                )

                            RongIMClient.getInstance().deleteMessages(
                                Conversation.ConversationType.PRIVATE,
                                bundle.getString("userCodeFriend", ""),
                                null
                            )
                            mActivity.finish()
                        }

                    }).showPopupWindow()
            }

            userProfileDislike.setOnClickListener {
                if (bundle.getBoolean("isWlm")){
                    benefitsReduceWLM(bundle.getString("userCodeFriend",""),1,object :EmptyCallBack{
                        override fun OnSuccessListener() {
                            OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                                override fun addBody(requestBody: OkHttpBodyEntity) {
                                    requestBody.setPost(Constant.user_add_wlm_url)
                                    requestBody.add("likeType", 2)
                                    requestBody.add("userCodeFriend", bundle.getString("userCodeFriend",""))
                                }
                            }, object : SDOkHttpResoutCallBack<MatchResultEntity>() {
                                override fun onSuccess(entity: MatchResultEntity) {
                                    FirebaseEventUtils.logEvent(FirebaseEventTag.WLM_Profile_Pass.name)

                                    SDEventManager.post(bundle.getInt("selectPosition"),EnumEventTag.WLM_DISLIKE_SWIPED.ordinal)
                                    finish()
                                }
                                override fun onFailure(code: Int, msg: String) {
                                }
                            }, isShowDialog = false)
                        }

                        override fun OnFailListener() {

                        }

                    })
                }else{
                    FirebaseEventUtils.logEvent(FirebaseEventTag.Home_Profile_Pass.name)
                    SDEventManager.post(EnumEventTag.INDEX_DISLIKE_SWIPED.ordinal)
                    finish()
                }
            }


        }
    }

    /**
     * 设置星座
     */
    private fun setStarSign(entity: UserProfileInfoEntity) {
        mView?.apply {
            entity.data.birthday?.apply {
                val month = Integer.parseInt(DateUtils.getDateString(entity.data.birthday, "MM"))
                val day = Integer.parseInt(DateUtils.getDateString(entity.data.birthday, "dd"))
                var drawable: Drawable? = null
                if (month == 1 && day >= 20 || month == 2 && day <= 18) {
                    userProfileStarSign.text = mActivity.getString(R.string.star_sign_1)
                    drawable = ContextCompat.getDrawable(mActivity, R.mipmap.icon_star_sign_1)
                } else if (month == 2 && day >= 19 || month == 3 && day <= 20) {
                    userProfileStarSign.text = mActivity.getString(R.string.star_sign_2)

                    drawable = ContextCompat.getDrawable(mActivity, R.mipmap.icon_star_sign_2)
                } else if (month == 3 && day >= 21 || month == 4 && day <= 19) {
                    userProfileStarSign.text = mActivity.getString(R.string.star_sign_3)

                    drawable = ContextCompat.getDrawable(mActivity, R.mipmap.icon_star_sign_3)
                } else if (month == 4 && day >= 20 || month == 5 && day <= 20) {
                    userProfileStarSign.text = mActivity.getString(R.string.star_sign_4)

                    drawable = ContextCompat.getDrawable(mActivity, R.mipmap.icon_star_sign_4)
                } else if (month == 5 && day >= 21 || month == 6 && day <= 21) {
                    userProfileStarSign.text = mActivity.getString(R.string.star_sign_5)

                    drawable = ContextCompat.getDrawable(mActivity, R.mipmap.icon_star_sign_5)
                } else if (month == 6 && day >= 22 || month == 7 && day <= 22) {
                    userProfileStarSign.text = mActivity.getString(R.string.star_sign_6)

                    drawable = ContextCompat.getDrawable(mActivity, R.mipmap.icon_star_sign_6)
                } else if (month == 7 && day >= 23 || month == 8 && day <= 22) {
                    userProfileStarSign.text = mActivity.getString(R.string.star_sign_7)

                    drawable = ContextCompat.getDrawable(mActivity, R.mipmap.icon_star_sign_7)
                } else if (month == 8 && day >= 23 || month == 9 && day <= 22) {
                    userProfileStarSign.text = mActivity.getString(R.string.star_sign_8)

                    drawable = ContextCompat.getDrawable(mActivity, R.mipmap.icon_star_sign_8)
                } else if (month == 9 && day >= 23 || month == 10 && day <= 22) {
                    userProfileStarSign.text = mActivity.getString(R.string.star_sign_9)

                    drawable = ContextCompat.getDrawable(mActivity, R.mipmap.icon_star_sign_9)
                } else if (month == 10 && day >= 23 || month == 11 && day <= 21) {
                    userProfileStarSign.text = mActivity.getString(R.string.star_sign_10)

                    drawable = ContextCompat.getDrawable(mActivity, R.mipmap.icon_star_sign_10)
                } else if (month == 11 && day >= 22 || month == 12 && day <= 21) {
                    userProfileStarSign.text = mActivity.getString(R.string.star_sign_11)

                    drawable = ContextCompat.getDrawable(mActivity, R.mipmap.icon_star_sign_11)
                } else if (month == 12 && day >= 22 || month == 1 && day <= 19) {
                    userProfileStarSign.text = mActivity.getString(R.string.star_sign_12)
                    drawable = ContextCompat.getDrawable(mActivity, R.mipmap.icon_star_sign_12)
                }

                val drawableWidth = 15
                val drawableHeight = 15
                drawable!!.setBounds(0, 0, drawableWidth, drawableHeight)
                userProfileStarSign.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            }
        }
    }


    fun benefitsReduceWLM(friendUserCode: String, type: Int, callBack: EmptyCallBack) {
        return OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.user_benefits_reduceWLM_url)
                requestBody.add("likeType", type)
                requestBody.add("userCodeFriend", friendUserCode)
            }
        }, object : SDOkHttpResoutCallBack<BaseEntity>() {
            override fun onSuccess(entity: BaseEntity) {
                callBack.OnSuccessListener()
            }

            override fun onFailure(code: Int, msg: String) {
                when (code) {
                    2003 -> {
                        if (!mActivity.isDestroyed) {
                            MemberBuyDialog(
                                mActivity,
                                0,
                                object : MemberBuyDialog.ChangeMembershipListener {
                                    override fun onListener(bean: MemberSubscribeEntity.Data.ProductDescriptions) {
                                        OkHttpManager.instance.requestInterface(object :
                                            OkHttpFromBoy {
                                            override fun addBody(requestBody: OkHttpBodyEntity) {
                                                requestBody.setPost(Constant.user_create_order_url)
                                                requestBody.add("productCode", bean.productCode)
                                                requestBody.add("productCategory", 1)
                                            }

                                        }, object : SDOkHttpResoutCallBack<OrderCreateEntity>() {
                                            override fun onSuccess(entity: OrderCreateEntity) {
                                                PayUtils.instance.start(
                                                    entity,
                                                    mActivity,
                                                    object : EmptySuccessCallBack {
                                                        override fun OnSuccessListener() {
                                                            BaseConfig.getInstance.setBoolean(
                                                                SpName.isMember,
                                                                true
                                                            )

                                                        }

                                                    })
                                            }
                                        })
                                    }

                                    override fun closeListener(refreshTime: Long) {

                                    }

                                })
                        }
                    }
                }
            }
        })
    }
}