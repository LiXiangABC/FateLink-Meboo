package io.rong.imkit.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.custom.base.manager.SDActivityManager
import io.rong.imkit.R
import io.rong.imkit.utils.BlurTransformation
import io.rong.imkit.utils.RouteUtils
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message
import io.rong.imlib.model.UserInfo
import io.rong.message.ImageMessage
import io.rong.message.SightMessage
import io.rong.message.TextMessage
import razerdp.basepopup.BasePopupWindow


/**
 * 消息推送弹窗
 */
class RecoverDialog(var ctx: Activity, var userInfo: UserInfo, var message: Message) :
    BasePopupWindow(ctx) {
    private val Text_Type: String = "RC:TxtMsg"
    private val Sight_Type: String = "RC:SightMsg"
    private val Img_Type: String = "RC:ImgMsg"

    init {
        setContentView(R.layout.dialog_recover)
        initView()

        val showAnim: Animation = AnimationUtils.loadAnimation(ctx, R.anim.anim_translation_down)
        val dismissAnim: Animation = AnimationUtils.loadAnimation(ctx, R.anim.anim_translation_up)
        showAnimation = showAnim
        dismissAnimation = dismissAnim

        //弹窗展示3秒后自动关闭
        Handler().postDelayed(Runnable {
            dismiss(true)
        }, 3000)
        setOutSideDismiss(false)
        isOutSideTouchable = true

    }

    private var mStartY: Int = 0
    private var mDiffY: Int = 0

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        val pushContainer = findViewById<ConstraintLayout>(R.id.push_container)
        //滑动关闭弹窗
        pushContainer.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    mStartY = event.rawY.toInt()
                }

                MotionEvent.ACTION_MOVE -> {
                    mDiffY = event.rawY.toInt() - mStartY
                }

                MotionEvent.ACTION_UP -> {
                    if (kotlin.math.abs(mDiffY) >= 100) {
                        dismiss()
                    }
                    mDiffY = 0
                }

                else -> {}
            }
            false
        }


        val outSideView = findViewById<View>(R.id.out_side_view)
        val dialogAvatar = findViewById<ImageView>(R.id.dialog_avatar)
        val dialogTitle = findViewById<TextView>(R.id.dialog_title)
        val dialogContent = findViewById<TextView>(R.id.dialog_content)
        val dialogAlbumsContainer = findViewById<ConstraintLayout>(R.id.dialog_albums_container)
        val dialogUserPhoto = findViewById<ImageView>(R.id.dialog_user_photo)
        val dialogPrivateLogo = findViewById<ImageView>(R.id.dialog_private_logo)
        val dialogVideoPlayLogo = findViewById<ImageView>(R.id.dialog_video_play_logo)
        val dialogVideoPlayPrivateLogo =
            findViewById<ImageView>(R.id.dialog_video_play_private_logo)

        //用户头像
        Glide.with(context).load(userInfo.portraitUri.toString())
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .into(dialogAvatar)
        //用户的昵称
        dialogTitle.text = userInfo.name
        //用户发送的内容：如果是文本则显示用户发送的内容，如果是图片/视频，根据是否是PP/PV展示不同的文案
        dialogContent.text =
            if (message.objectName == Text_Type) (message.content as TextMessage).content else if (message.objectName == Img_Type) if (hasPrivate()) "Sent you a private photo." else "Sent you a photo." else if (message.objectName == Sight_Type) if (hasPrivate()) "Sent you a private video." else "Sent you a video." else ""

        //背景设置
        pushContainer.visibility = View.VISIBLE
        pushContainer.setBackgroundResource(R.drawable.shape_push_bg)
        outSideView.setBackgroundResource(R.drawable.shape_purple_circle_stroke)


        //以下为PP/PV打码操作
        val isPrivate = if (message.objectName == Text_Type) false else hasPrivate()
        dialogPrivateLogo.isVisible = isPrivate
        dialogVideoPlayLogo.isVisible =
            message.objectName == Sight_Type && message.expansion.isNullOrEmpty()
        dialogVideoPlayPrivateLogo.isVisible = message.objectName == Sight_Type && hasPrivate()
        if (message.objectName != Text_Type) {
            val imageUrl =
                if (message.objectName == Sight_Type) (message.content as SightMessage).mediaUrl else if (message.objectName == Img_Type) (message.content as ImageMessage).mediaUrl else ""
            setImageView(
                imageUrl.toString(),
                dialogUserPhoto,
                activity = context,
                vagueness = hasPrivate(),
                dialogAlbumsContainer = dialogAlbumsContainer
            )
        }
        //点击弹窗，跳转到对应的聊天界面
        pushContainer.setOnClickListener {
            if (ctx== null || ctx.isDestroyed || ctx.isFinishing){
                return@setOnClickListener
            }
            RouteUtils.routeToConversationActivity(
                ctx,
                Conversation.ConversationType.PRIVATE,
                message.targetId,
                false
            )
            dismiss()
        }


        setBackgroundColor(Color.TRANSPARENT)

    }

    /**
     * 判断是否有扩展字段，则是否包含isPrivate，isPrivate为true
     */
    private fun hasPrivate() =
        !message.expansion.isNullOrEmpty() && message.expansion.containsKey("isPrivate") && message.expansion["isPrivate"] == "true"

    /**
     * @param url
     * @param imageView
     * @param vagueness 是否模糊图片
     * @param radius 模糊半径
     * @param sampling 取样
     * @param errorImageId 加载失败图片
     * @param placeholderImageId 占位图片
     * @param cache 是否缓存
     */
    fun setImageView(
        url: String?,
        imageView: ImageView,
        vagueness: Boolean = false,
        radius: Int = 5,
        sampling: Int = 5,
        thumbnail: Float = 1f,
        errorImageId: Int = R.drawable.image_error,
        cache: Boolean = true,
        activity: Activity? = null,
        dialogAlbumsContainer:ConstraintLayout
    ) {
        val mActivity: Activity = activity ?: SDActivityManager.instance.lastActivity
        if (mActivity.isDestroyed) {
            return
        }
        val options = RequestOptions()
        options.error(errorImageId)

        options.diskCacheStrategy(if (cache) DiskCacheStrategy.ALL else DiskCacheStrategy.NONE)

        if (vagueness) {
            Glide.with(mActivity).load(url)
                .format(DecodeFormat.PREFER_RGB_565) // 设置图片格式为RGB_565
                .apply(
                    RequestOptions.bitmapTransform(BlurTransformation(radius, sampling))
                )
                .override(45,60)
                .addListener(object :RequestListener<Drawable>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        //如果不是文本消息，则不展示图片/视频UI
                        dialogAlbumsContainer.isVisible = message.objectName != Text_Type
                        return false
                    }

                })
                .into(imageView)
        } else {
            Glide.with(imageView).load(url)
                .format(DecodeFormat.PREFER_RGB_565) // 设置图片格式为RGB_565
                .apply(options)
                .override(45,60)
                .thumbnail(thumbnail)
                .addListener(object :RequestListener<Drawable>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        //如果不是文本消息，则不展示图片/视频UI
                        dialogAlbumsContainer.isVisible = message.objectName != Text_Type
                        return false
                    }

                })
                .into(imageView)
        }
    }
}