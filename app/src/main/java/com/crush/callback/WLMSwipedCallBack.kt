package com.crush.callback

import com.crush.bean.WLMListBean

interface WLMSwipedCallBack {
   fun swipedCallback(direction:Int,wlmListBean: WLMListBean)
   fun swipedRefresh()
}