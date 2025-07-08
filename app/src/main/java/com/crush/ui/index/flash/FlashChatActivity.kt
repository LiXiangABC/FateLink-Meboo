package com.crush.ui.index.flash

import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.airbnb.lottie.LottieAnimationView
import com.crush.R
import com.custom.base.config.BaseConfig
import com.crush.mvp.MVPBaseActivity
import com.makeramen.roundedimageview.RoundedImageView
import io.rong.imkit.SpName


class FlashChatActivity : MVPBaseActivity<FlashChatContract.View, FlashChatPresenter>(), FlashChatContract.View {

    override fun setFullScreen(): Boolean {
        return true
    }
    override fun bindLayout(): Int {
        return R.layout.act_flash_chat
    }

    override fun initView() {
        findViewById<ConstraintLayout>(R.id.fl_out_container).setBackgroundResource(if (BaseConfig.getInstance.getInt(SpName.trafficSource,0)==1)R.mipmap.icon_white_flash_chat_bg else R.mipmap.icon_flash_chat_bg)
        loLightningLove.isVisible=BaseConfig.getInstance.getInt(SpName.trafficSource,0)!=1
        if (loLightningLove.isVisible){
            loLightningLove.playAnimation()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        loLightningLove.cancelAnimation()
    }

    override val flashChatUserTitle: TextView
        get() = findViewById(R.id.flash_chat_user_title)
    override val flashChatUserAvatar: RoundedImageView
        get() = findViewById(R.id.flash_chat_user_avatar)
    override val loLightningLove: LottieAnimationView
        get() = findViewById(R.id.lo_lightning_love)
    override val startFlashChatContainer: LinearLayout
        get() = findViewById(R.id.start_flash_chat_container)
    override val imgClose: ImageView
        get() = findViewById(R.id.img_close)

}