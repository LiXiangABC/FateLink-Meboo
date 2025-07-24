package com.crush.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.crush.Constant
import com.crush.R
import com.crush.entity.BaseEntity
import com.crush.entity.MatchResultEntity
import com.crush.ui.index.helper.IndexHelper
import io.rong.imkit.entity.MemberSubscribeEntity
import io.rong.imkit.event.EnumEventTag
import com.crush.ui.index.match.MatchUserActivity
import com.crush.util.AnimatorUtils
import com.crush.util.DensityUtil
import com.crush.util.GlideUtil
import com.crush.util.MemberDialogShow
import com.crush.util.MyCountDownTimer
import com.crush.view.delay.DelayClickConstraintLayout
import com.custom.base.config.BaseConfig
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.crush.util.IntentUtil
import com.google.firebase.messaging.RemoteMessage
import com.sunday.eventbus.SDEventManager
import io.rong.imkit.IMCenter
import io.rong.imkit.SpName
import io.rong.imkit.dialog.MemberBuyDialog
import io.rong.imkit.entity.OrderCreateEntity
import io.rong.imkit.http.HttpRequest
import io.rong.imkit.pay.EmptySuccessCallBack
import io.rong.imkit.pay.PayUtils
import io.rong.imkit.utils.RouteUtils
import io.rong.imlib.IRongCallback
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message
import io.rong.message.TextMessage
import razerdp.basepopup.BasePopupWindow


/**
 * 消息推送弹窗
 */
class PushInsideDialog(var ctx: Context,var remoteMessage: RemoteMessage) :  BasePopupWindow(ctx) {
    init {
        setContentView(R.layout.dialog_push_inside)
        initView()

        val showAnim: Animation = AnimationUtils.loadAnimation(ctx, R.anim.anim_translation_down)
        val dismissAnim: Animation = AnimationUtils.loadAnimation(ctx, R.anim.anim_translation_up)
        showAnimation = showAnim
        dismissAnimation=dismissAnim

        Handler().postDelayed(Runnable {
            dismiss(true)
        },8000)
        setOutSideDismiss(false)
        isOutSideTouchable = true

    }
    private var mStartY: Int = 0
    private var mDiffY: Int = 0
    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {


        val pushContainer = findViewById<ConstraintLayout>(R.id.push_container)
        val pushMatchContainer = findViewById<ConstraintLayout>(R.id.push_match_container)

        pushContainer.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    mStartY = event.rawY.toInt()
                }
                MotionEvent.ACTION_MOVE -> {
                    mDiffY = event.rawY.toInt() - mStartY
                }
                MotionEvent.ACTION_UP -> {
                    if (kotlin.math.abs(mDiffY) < 100) {
                    } else {
                        dismiss()
                    }
                    mDiffY = 0

                }

                else -> {}
            }
            false
        }
        pushMatchContainer?.setOnTouchListener { _, event ->
            event.run {
                when (action) {
                    MotionEvent.ACTION_DOWN -> {
                        mStartY = rawY.toInt()
                    }
                    MotionEvent.ACTION_MOVE -> {
                        mDiffY = rawY.toInt() - mStartY
                    }
                    MotionEvent.ACTION_UP -> {
                        if (kotlin.math.abs(mDiffY) < 100) {
                        } else {
                            dismiss()
                        }
                        mDiffY = 0

                    }

                    else -> {}
                }
            }
            false
        }


        val outSideView = findViewById<View>(R.id.out_side_view)
        val dialogAvatar = findViewById<ImageView>(R.id.dialog_avatar)
        val dialogTitle = findViewById<TextView>(R.id.dialog_title)
        val dialogContent = findViewById<TextView>(R.id.dialog_content)
        val txtPushSayHi = findViewById<TextView>(R.id.txt_push_say_hi)
        val txtSayHi = findViewById<TextView>(R.id.txt_say_hi)
        val dialogAlbumsContainer = findViewById<ConstraintLayout>(R.id.dialog_albums_container)
        val dialogUserPhoto = findViewById<ImageView>(R.id.dialog_user_photo)
        val dialogPrivateLogo = findViewById<ImageView>(R.id.dialog_private_logo)
        val dialogVideoPlayLogo = findViewById<ImageView>(R.id.dialog_video_play_logo)
        val dialogVideoPlayPrivateLogo = findViewById<ImageView>(R.id.dialog_video_play_private_logo)
        val dialogPushLike = findViewById<ImageView>(R.id.dialog_push_like)

        val leftDialogAvatar = findViewById<ImageView>(R.id.dialog_left_avatar)
        val rightDialogAvatar = findViewById<ImageView>(R.id.dialog_right_avatar)
        val dialogMatchTitle = findViewById<TextView>(R.id.dialog_match_title)
        val dialogMatchContent = findViewById<TextView>(R.id.dialog_match_content)
        val dialogRightAvatarContent = findViewById<ConstraintLayout>(R.id.dialog_right_avatar_content)
        val dialogLeftAvatarContent = findViewById<ConstraintLayout>(R.id.dialog_left_avatar_content)
        val imgLightning = findViewById<ImageView>(R.id.img_lightning)
        val imgRedHeartSmall = findViewById<ImageView>(R.id.img_red_heart_small)
        val imgRedHeartBig = findViewById<ImageView>(R.id.img_red_heart_big)

        val pushMemberContainer = findViewById<DelayClickConstraintLayout>(R.id.push_member_container)
        val txtMemberTitle = findViewById<TextView>(R.id.txt_member_title)
        val txtMemberContent = findViewById<TextView>(R.id.txt_member_content)
        val txtMemberGetSpecialNow = findViewById<TextView>(R.id.txt_member_get_special_now)

        GlideUtil.setImageView(remoteMessage.notification?.imageUrl.toString(),dialogAvatar, vagueness = remoteMessage.data["member"].equals("false"))
        dialogTitle.text=remoteMessage.notification?.title
        dialogContent.text=remoteMessage.notification?.body


        val mText= arrayOf("Say hi \uD83D\uDE09","Say hi \uD83D\uDE08")
        val randomIndex = (mText.indices).random()
        val randomValue = mText[randomIndex]
        txtPushSayHi.text=if (BaseConfig.getInstance.getInt(SpName.trafficSource,0)==1)
            "Say hi" else randomValue
        txtSayHi.text= if (BaseConfig.getInstance.getInt(SpName.trafficSource,0)==1)
            "Say hi" else randomValue
        when(remoteMessage.data["type"]){
            "1"->{
                pushContainer.visibility=View.VISIBLE
                pushMatchContainer.visibility=View.GONE
                dialogAlbumsContainer.visibility=View.GONE
                dialogPushLike.visibility=View.GONE
                txtPushSayHi.visibility=View.GONE
                outSideView.setBackgroundResource(R.drawable.shape_purple_circle_stroke)
                pushContainer.setBackgroundResource(R.drawable.shape_push_bg)

            }
            "2"->{
                pushContainer.visibility=View.VISIBLE
                pushMatchContainer.visibility=View.GONE
                dialogAlbumsContainer.visibility=View.VISIBLE
                dialogUserPhoto.visibility=View.VISIBLE
                dialogPrivateLogo.visibility=View.GONE
                dialogVideoPlayLogo.visibility=View.GONE
                dialogVideoPlayPrivateLogo.visibility=View.GONE
                dialogPushLike.visibility=View.GONE
                txtPushSayHi.visibility=View.GONE
                outSideView.setBackgroundResource(R.drawable.shape_purple_circle_stroke)
                pushContainer.setBackgroundResource(R.drawable.shape_push_bg)

                GlideUtil.setImageView(remoteMessage.data["imageUrl"].toString(),dialogUserPhoto)
            }
            "3"->{
                pushContainer.visibility=View.VISIBLE
                pushMatchContainer.visibility=View.GONE
                dialogAlbumsContainer.visibility=View.VISIBLE
                dialogUserPhoto.visibility=View.VISIBLE
                dialogPrivateLogo.visibility=View.VISIBLE
                dialogVideoPlayLogo.visibility=View.GONE
                dialogVideoPlayPrivateLogo.visibility=View.GONE
                dialogPushLike.visibility=View.GONE
                txtPushSayHi.visibility=View.GONE
                outSideView.setBackgroundResource(R.drawable.shape_purple_circle_stroke)
                pushContainer.setBackgroundResource(R.drawable.shape_push_bg)

                GlideUtil.setImageView(remoteMessage.data["imageUrl"].toString(),dialogUserPhoto,true)
            }
            "4"->{
                pushContainer.visibility=View.VISIBLE
                pushMatchContainer.visibility=View.GONE
                dialogAlbumsContainer.visibility=View.VISIBLE
                dialogUserPhoto.visibility=View.VISIBLE
                dialogPrivateLogo.visibility=View.GONE
                dialogVideoPlayLogo.visibility=View.VISIBLE
                dialogVideoPlayPrivateLogo.visibility=View.GONE
                dialogPushLike.visibility=View.GONE
                txtPushSayHi.visibility=View.GONE
                outSideView.setBackgroundResource(R.drawable.shape_purple_circle_stroke)
                pushContainer.setBackgroundResource(R.drawable.shape_push_bg)

                GlideUtil.setImageView(remoteMessage.data["imageUrl"].toString(),dialogUserPhoto)
            }
            "5"->{
                pushContainer.visibility=View.VISIBLE
                pushMatchContainer.visibility=View.GONE
                dialogAlbumsContainer.visibility=View.VISIBLE
                dialogUserPhoto.visibility=View.VISIBLE
                dialogPrivateLogo.visibility=View.VISIBLE
                dialogVideoPlayLogo.visibility=View.GONE
                dialogVideoPlayPrivateLogo.visibility=View.VISIBLE
                dialogPushLike.visibility=View.GONE
                txtPushSayHi.visibility=View.GONE
                outSideView.setBackgroundResource(R.drawable.shape_purple_circle_stroke)
                pushContainer.setBackgroundResource(R.drawable.shape_push_bg)

                GlideUtil.setImageView(remoteMessage.data["imageUrl"].toString(),dialogUserPhoto,true)
            }
            "6"->{
                pushContainer.visibility=View.VISIBLE
                pushMatchContainer.visibility=View.GONE
                dialogAlbumsContainer.visibility=View.GONE
                dialogPushLike.visibility=View.VISIBLE
                txtPushSayHi.visibility=View.VISIBLE
                SDEventManager.post(EnumEventTag.WLM_REFRESH.ordinal)
                outSideView.setBackgroundResource(R.drawable.shape_red_circle_stroke_push_like)
                pushContainer.setBackgroundResource(R.drawable.shape_push_bg)
            }
            "8"->{
                pushMatchContainer.visibility=View.VISIBLE
                pushContainer.visibility=View.GONE

                dialogMatchTitle.text=remoteMessage.notification?.title
                dialogMatchContent.text=remoteMessage.notification?.body
                GlideUtil.setImageView(remoteMessage.notification?.imageUrl.toString(),leftDialogAvatar)
                GlideUtil.setImageView(remoteMessage.data["imageUrl"].toString(),rightDialogAvatar)

                AnimatorUtils.playBounceAnimX(dialogLeftAvatarContent,0.0f, DensityUtil.dp2pxF(ctx,15f),500,200)
                AnimatorUtils.playBounceAnimX(dialogRightAvatarContent,DensityUtil.dp2pxF(ctx,72f), DensityUtil.dp2pxF(ctx,57f),500,200)

                Handler(Looper.getMainLooper()).postDelayed(Runnable {
                    imgLightning.visibility=View.VISIBLE
                    imgRedHeartSmall.visibility=View.VISIBLE
                    imgRedHeartBig.visibility=View.VISIBLE
                },500)


                pushMatchContainer.setOnClickListener {
                    HttpRequest.getTrySendMessage(remoteMessage.data["targetId"].toString(),context,object :HttpRequest.RequestCallBack{
                        override fun onSuccess() {
                            val conversationType: Conversation.ConversationType = Conversation.ConversationType.PRIVATE
                            val messageContent = TextMessage.obtain("hi")

                            val message: Message = Message.obtain(remoteMessage.data["targetId"].toString(), conversationType, messageContent)
                            IMCenter.getInstance()
                                .sendMessage(message, null, null, object : IRongCallback.ISendMessageCallback {
                                    override fun onAttached(message: Message?) {}
                                    override fun onSuccess(message: Message?) {
                                    }
                                    override fun onError(message: Message?, errorCode: RongIMClient.ErrorCode?) {}
                                })

                            RouteUtils.routeToConversationActivity(
                                ctx,
                                Conversation.ConversationType.PRIVATE,
                                remoteMessage.data["targetId"].toString(),
                                false
                            )
                            dismiss()

                        }

                        override fun onFailure(code: Int, msg: String) {

                        }

                    })
                }


            }
            "18"->{
                pushMatchContainer.visibility=View.GONE
                pushContainer.visibility=View.GONE
                pushMemberContainer.visibility=View.VISIBLE

                txtMemberTitle.text=remoteMessage.notification?.title
                txtMemberContent.text=remoteMessage.notification?.body


                remoteMessage.data["overTime"]?.let {
                    val downTime = MyCountDownTimer(
                        it.toLong()*1000,
                        1000,
                        downTime = { millisUntilFinished, isFinish ->
                            if (!isFinish) {
                                //正在倒计时
                                Handler().post(Runnable {
                                    val hours = millisUntilFinished / (1000 * 60 * 60)
                                    val minutes = millisUntilFinished % (1000 * 60 * 60) / (1000 * 60)
                                    val seconds = millisUntilFinished % (1000 * 60 * 60) % (1000 * 60) / 1000

                                    txtMemberGetSpecialNow?.text =
                                        "${remoteMessage.data["overTimeContent"].toString()} ${
                                            IndexHelper.convertDownTimeStr(hours, minutes, seconds)
                                        }"
                                })

                            } else {
                                dismiss()
                            }
                        })

                    downTime.start()
                    Handler().postDelayed(Runnable {//弹窗8秒消失，倒计时控件在7.5秒时清除
                        downTime.cancel()
                    },7500)
                }


                pushMemberContainer.setOnClickListener {
                    dismiss()
                    MemberDialogShow.memberBuyShow(null, context)
                }

            }
        }

//        dialogPushLike.setOnClickListener {
//            benefitsReduceWLM(remoteMessage.data["targetId"].toString(),1,remoteMessage.data["imageUrl"].toString())
//            dismiss()
//        }
        pushContainer.setOnClickListener {
            benefitsReduceWLM(remoteMessage.data["targetId"].toString(),1,remoteMessage.data["imageUrl"].toString())
            dismiss()
        }

        pushContainer.setOnClickListener {
            if (remoteMessage.data["type"]!="6"){
                RouteUtils.routeToConversationActivity(
                    ctx,
                    Conversation.ConversationType.PRIVATE,
                    remoteMessage.data["targetId"].toString(),
                    false
                )
            }else{
                SDEventManager.post(EnumEventTag.INDEX_TO_WLM.ordinal)
            }

            dismiss()
        }
        setBackgroundColor(Color.TRANSPARENT)

    }

    fun benefitsReduceWLM(friendUserCode:String,type:Int,avatarUrl:String){
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.user_benefits_reduceWLM_url)
                requestBody.add("likeType", type)
                requestBody.add("userCodeFriend", friendUserCode)
            }
        }, object : SDOkHttpResoutCallBack<BaseEntity>() {
            override fun onSuccess(entity: BaseEntity) {
                OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                    override fun addBody(requestBody: OkHttpBodyEntity) {
                        requestBody.setPost(Constant.user_add_wlm_url)
                        requestBody.add("likeType", type)
                        requestBody.add("userCodeFriend",friendUserCode)
                        requestBody.add("source",3)
                    }
                }, object : SDOkHttpResoutCallBack<MatchResultEntity>() {
                    override fun onSuccess(entity: MatchResultEntity) {
                        if (entity.data.matched) {
                            val bundle = Bundle()
                            bundle.putString("userCodeFriend", friendUserCode)
                            bundle.putString(
                                "avatarUrl",
                                avatarUrl
                            )
                            IntentUtil.startActivity(MatchUserActivity::class.java, bundle)
                        }
                    }

                    override fun onFailure(code: Int, msg: String) {

                    }
                }, isShowDialog = false)
            }

            override fun onFailure(code: Int, msg: String) {
                when (code) {
                    2003 -> {
                        MemberBuyDialog(ctx,
                            0,
                            object : MemberBuyDialog.ChangeMembershipListener {
                                override fun onListener(bean: MemberSubscribeEntity.Data.ProductDescriptions) {
                                    OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                                        override fun addBody(requestBody: OkHttpBodyEntity) {
                                            requestBody.setPost(Constant.user_create_order_url)
                                            requestBody.add("productCode", bean.productCode)
                                            requestBody.add("productCategory", 1)
                                        }

                                    }, object : SDOkHttpResoutCallBack<OrderCreateEntity>() {
                                        override fun onSuccess(entity: OrderCreateEntity) {
                                            PayUtils.instance.start(entity,ctx,object : EmptySuccessCallBack {
                                                override fun OnSuccessListener() {
                                                    BaseConfig.getInstance.setBoolean(SpName.isMember, true)
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
        }, isShowDialog = false)

    }

}