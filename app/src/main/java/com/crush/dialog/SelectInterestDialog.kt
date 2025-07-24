package com.crush.dialog

import android.app.Activity
import android.widget.ImageView
import android.widget.TextView
import com.crush.Constant
import com.crush.R
import io.rong.imkit.entity.IMTagBean
import com.crush.entity.InterestsInfoEntity
import com.crush.view.TagCloudView
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.custom.base.util.ToastUtil
import razerdp.basepopup.BasePopupWindow


class SelectInterestDialog(
    var ctx: Activity,
    var interestsTags: MutableList<String>,
    var listener: onCallBack
) : BasePopupWindow(ctx) {
    init {
        setContentView(R.layout.dialog_select_interest)
        initView()
    }

    private fun initView() {
        val dialogClose = findViewById<ImageView>(R.id.dialog_close)
        val addInterestsTagCloud = findViewById<TagCloudView>(R.id.add_interests_tag_cloud)
        var tags: MutableList<IMTagBean> = ArrayList()

        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.user_interests_url)
            }
        }, object : SDOkHttpResoutCallBack<InterestsInfoEntity>() {
            override fun onSuccess(entity: InterestsInfoEntity) {
                tags = entity.data
                repeat(tags.size) {
                    for (i in 0 until interestsTags.size) {
                        if (tags[it].interest == interestsTags[i]) {
                            tags[it].check = true
                            break
                        }
                    }
                }
                addInterestsTagCloud.setTagBeans(tags,true)
                addInterestsTagCloud.setOnTagClickListener { position ->
                    if (!tags[position].check) {
                        var count = 0
                        for (i in 0 until tags.size) {
                            if (tags[i].check) {
                                count++
                            }
                        }
                        if (count >= 8) {
                            ToastUtil.toast(ctx.getString(R.string.interest_more_tip))
                            return@setOnTagClickListener
                        }
                    }

                    tags[position].check = !tags[position].check
                    addInterestsTagCloud.getTags(
                        position,
                        if (tags[position].check) R.drawable.shape_interest_select_bg else R.drawable.shape_interest_unselect_bg
                    )
                }
                if (!ctx.isDestroyed) {
                    showPopupWindow()
                }
            }

            override fun onFailure(code: Int, msg: String) {
            }
        })


        val dialogSave = findViewById<TextView>(R.id.dialog_save)

        dialogSave.setOnClickListener {
            listener.onCallback(tags)
            dismiss()
        }
        dialogClose.setOnClickListener {
            dismiss()
        }

        setOutSideDismiss(true)
    }

    interface onCallBack {
        fun onCallback(tags: MutableList<IMTagBean>)
    }
}