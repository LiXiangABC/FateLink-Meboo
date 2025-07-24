package com.crush.ui.chat.ktl

import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import io.rong.imkit.entity.WLMListBean
import com.crush.callback.WLMSwipedCallBack
import com.crush.ui.chat.profile.UserProfileInfoActivity
import com.crush.util.IntentUtil

object WlmClick {

    fun itemClick(
        isMember: Boolean,
        position: Int,
        model: WLMListBean,
        wLMSwipedCallBack: WLMSwipedCallBack?=null
    ) {
        if (isMember) {
            val bundle = Bundle()
            bundle.putBoolean("isWlm", true)
            bundle.putInt("selectPosition", position)
            bundle.putString("userCodeFriend", model.userCodeFriend)
            IntentUtil.startActivity(UserProfileInfoActivity::class.java, bundle)
        } else {
            wLMSwipedCallBack?.swipedCallback(ItemTouchHelper.END, model)
        }
    }
}