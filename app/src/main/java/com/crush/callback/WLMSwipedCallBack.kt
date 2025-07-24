package com.crush.callback

import io.rong.imkit.entity.WLMListBean

interface WLMSwipedCallBack {
   fun swipedCallback(direction:Int,wlmListBean: WLMListBean)
   fun swipedRefresh()
}