package com.crush.dialog

import android.content.Context
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.crush.Constant
import com.crush.R
import com.crush.entity.BaseEntity
import com.crush.entity.GoogleReviewEntity
import com.crush.view.StarBarView
import com.custom.base.config.BaseConfig
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import io.rong.imkit.SpName
import io.rong.imkit.conversation.messgelist.provider.TextMessageItemProvider
import io.rong.imkit.utils.RouteUtils
import razerdp.basepopup.BasePopupWindow

class GoogleEvaluateDialog(
    context: Context,
    val entity: GoogleReviewEntity.Data,
    val callback: () -> Unit
): BasePopupWindow(context) {
    var fiveStarClick = false
    init {
        setContentView(R.layout.dialog_google_evaluate)
        initView()
    }

    fun initView(){
        setOverlayMask(true)
        val dialogEvaluateStar = findViewById<StarBarView>(R.id.dialog_evaluate_star)
        val dialogConfirm = findViewById<TextView>(R.id.dialog_confirm)
        val dialogCancel = findViewById<ImageView>(R.id.dialog_cancel)
        val dialogInput = findViewById<EditText>(R.id.dialog_input)
        val dialogInputSize = findViewById<TextView>(R.id.dialog_input_size)
        val dialogNoRemind = findViewById<TextView>(R.id.dialog_no_remind)
        val dialogEvaluateTip = findViewById<TextView>(R.id.dialog_evaluate_tip)
        dialogEvaluateTip.text= entity.content
        dialogConfirm.text= entity.buttonStr
        dialogInput.addTextChangedListener {
            dialogInputSize.text = "${dialogInput.text.length}/500"
        }
        dialogNoRemind.isVisible = BaseConfig.getInstance.getBoolean("GoogleEvaluateShow"+BaseConfig.getInstance.getString(SpName.token,""),false)
        dialogNoRemind.setOnClickListener {
            GoogleNoRemindDialog(context){
                BaseConfig.getInstance.setBoolean("GoogleEvaluateNoRemind"+BaseConfig.getInstance.getString(SpName.token,""),true)
                dismiss()
            }.showPopupWindow()
        }
        dialogCancel.setOnClickListener {
            dismiss()
        }
        dialogEvaluateStar.setOnStarStarListener(object :StarBarView.OnStarStarListener{
            override fun onListener() {
                dialogConfirm.isEnabled = dialogEvaluateStar.getStarRating() >= 1f
            }
        })
        dialogConfirm.setOnClickListener {
            if (dialogEvaluateStar.getStarRating() == 5f){
                googleEvaluate(dialogInput.text.toString(),dialogEvaluateStar.getStarRating().toInt())
                RouteUtils.routeToWebActivity(
                    context,
                    TextMessageItemProvider.GoogleUrl
                )
                callback.invoke()
                dismiss()
                return@setOnClickListener
            }
            if (!fiveStarClick){
                dialogInput.isVisible = true
                dialogInputSize.isVisible = true
                fiveStarClick = true
                return@setOnClickListener
            }
            googleEvaluate(dialogInput.text.toString(),dialogEvaluateStar.getStarRating().toInt())
            dismiss()
        }

        BaseConfig.getInstance.setBoolean("GoogleEvaluateShow"+BaseConfig.getInstance.getString(SpName.token,""),true)

    }

    fun googleEvaluate(commentDetail:String,ratingStar:Int){
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.review_addReview_url)
                requestBody.add("commentDetail",commentDetail)
                requestBody.add("ratingStar",ratingStar)
            }
        }, object : SDOkHttpResoutCallBack<BaseEntity>() {
            override fun onSuccess(entity: BaseEntity) {

            }

            override fun onFailure(code: Int, msg: String) {
            }
        })
    }
}