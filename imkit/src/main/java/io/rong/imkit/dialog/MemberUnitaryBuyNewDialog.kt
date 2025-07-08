package io.rong.imkit.dialog

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.custom.base.config.BaseConfig
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.sunday.eventbus.SDBaseEvent
import com.sunday.eventbus.SDEventManager
import com.sunday.eventbus.SDEventObserver
import io.rong.imkit.API
import io.rong.imkit.R
import io.rong.imkit.SpName
import io.rong.imkit.activity.RongWebviewActivity
import io.rong.imkit.adapter.MemberUnitaryAdapter
import io.rong.imkit.entity.AgreementEntity
import io.rong.imkit.entity.BuyMemberPageEntity
import io.rong.imkit.event.EnumEventTag
import io.rong.imkit.pay.PayOrderLogUtils
import razerdp.basepopup.BasePopupWindow

class MemberUnitaryBuyNewDialog(
    var ctx: Context,
    var productCategory: Int,
    var listener: MemberUnitaryBuyListener
) : BasePopupWindow(ctx),SDEventObserver {
    init {
        setContentView(R.layout.dialog_member_buy_unitary_new)
        initView()
    }

    private fun initView() {
        val dialogPurchaseNow = findViewById<TextView>(R.id.dialog_purchase_now)
        val dialogCancel = findViewById<ImageView>(R.id.dialog_close)
        val imgTop = findViewById<ImageView>(R.id.img_top)
        val itemTitle = findViewById<TextView>(R.id.item_title)
        val itemContent = findViewById<TextView>(R.id.item_content)
        val itemList = findViewById<RecyclerView>(R.id.item_list)

        val txtBuySingleTips = findViewById<TextView>(R.id.txt_buy_single_tips)

        val memberDialog = findViewById<ConstraintLayout>(R.id.father_member_dialog)


        imgTop.visibility = View.GONE
        itemTitle.visibility = View.GONE
        itemContent.visibility = View.GONE
        itemList.visibility = View.GONE
        txtBuySingleTips.visibility = View.GONE
        val layoutParams = memberDialog.layoutParams
        layoutParams.height = dipToPx(500f).toInt()
        memberDialog.layoutParams = layoutParams

        var model: BuyMemberPageEntity.ProductExt? = null
        var position = 0

        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(API.user_member_product_category_url)
                requestBody.add("productCategory", productCategory)
            }

        }, object : SDOkHttpResoutCallBack<BuyMemberPageEntity>() {
            override fun onSuccess(entity: BuyMemberPageEntity) {

                imgTop.visibility = View.VISIBLE
                itemTitle.visibility = View.VISIBLE
                itemContent.visibility = View.VISIBLE
                itemList.visibility = View.VISIBLE
                txtBuySingleTips.visibility = View.VISIBLE
                val layoutParams2 = memberDialog.layoutParams
                layoutParams2.height = WRAP_CONTENT
                memberDialog.layoutParams = layoutParams2


                dialogPurchaseNow.visibility = View.VISIBLE
                itemTitle.text = entity.data.subscriptions[0].tip
                itemContent.text = entity.data.subscriptions[0].content

                when (productCategory) {
                    2 -> {
                        setViewInfo(
                            imgTop,
                            memberDialog,
                            dialogPurchaseNow,
                            txtBuySingleTips,
                            if (BaseConfig.getInstance.getInt(SpName.trafficSource,0)==1)R.drawable.icon_white_single_flash_chat else R.drawable.icon_single_flash_chat,
                            R.mipmap.bg_dialog_bu_member_flash_chat,
                            R.drawable.selector_buy_fc_btn,
                            Color.parseColor("#60FF3B3B")
                        )
                    }

                    3 -> {
                        setViewInfo(
                            imgTop,
                            memberDialog,
                            dialogPurchaseNow,
                            txtBuySingleTips,
                            R.drawable.icon_single_pp,
                            R.mipmap.bg_dialog_bu_member_pp,
                            R.drawable.selector_buy_pp_btn,
                            Color.parseColor("#BE4CFF")
                        )
                    }

                    4 -> {
                        setViewInfo(
                            imgTop,
                            memberDialog,
                            dialogPurchaseNow,
                            txtBuySingleTips,
                            R.drawable.icon_single_pv,
                            R.mipmap.bg_dialog_bu_member_pv,
                            R.drawable.selector_buy_pv_btn,
                            Color.parseColor("#23D183")

                        )
                    }
                }
                val listModel = entity.data.productDescriptions
                for (i in 0 until listModel.size){
                    if (listModel[i].check == true){
                        model = listModel[i]
                        position = i
                    }
                }
                itemList.layoutManager= LinearLayoutManager(ctx, RecyclerView.HORIZONTAL,true)
                val memberUnitaryAdapter = MemberUnitaryAdapter(listModel,ctx,productCategory,object :MemberUnitaryAdapter.OnListener{
                    override fun onListener(position: Int) {
                        if (listModel[position].check != true){
                            for (i in 0 until listModel.size){
                                listModel[i].check = false
                            }
                            listModel[position].check=true
                            model = listModel[position]
                            itemList.adapter?.notifyDataSetChanged()
                        }
                        itemList.scrollToPosition(if (position > 1)listModel.size-1 else 0)
                    }

                })
                itemList.adapter=memberUnitaryAdapter

                itemList.scrollToPosition(if (position > 1)listModel.size-1 else 0)


            }
        })

        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(API.user_config_url)
                requestBody.add("code", 2)
            }
        }, object : SDOkHttpResoutCallBack<AgreementEntity>() {
            override fun onSuccess(entity: AgreementEntity) {
                val style = SpannableStringBuilder()
                style.append(ctx.getString(R.string.buy_member_unitary_tips))

                style.setSpan(object : ClickableSpan() {
                    override fun onClick(p0: View) {
                        val intent = Intent(context,RongWebviewActivity::class.java)
                        val b = Bundle()
                        b.putString("url", entity.data.privacyPolicy)
                        b.putString(
                            "title",
                            ctx.resources.getString(R.string.privacy_policy)
                        )
                        b.apply {
                            intent.putExtras(this)
                        }
                        context.startActivity(intent)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        ds.isUnderlineText = true
                        ds.clearShadowLayer()
                    }
                }, 32, 46, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                style.setSpan(object : ClickableSpan() {
                    override fun onClick(p0: View) {
                        val intent = Intent(context,RongWebviewActivity::class.java)
                        val b = Bundle()
                        b.putString("url", entity.data.terms)
                        b.putString(
                            "title",
                            ctx.resources.getString(R.string.terms)
                        )
                        b.apply {
                            intent.putExtras(this)
                        }

                        context.startActivity(intent)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        ds.isUnderlineText = true
                        ds.clearShadowLayer()
                    }
                }, 51, 56, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                txtBuySingleTips.text = style
                txtBuySingleTips.movementMethod = LinkMovementMethod.getInstance()
            }
        })

        showPopupWindow()
        dialogPurchaseNow.setOnClickListener {
            if(dialogPurchaseNow.isEnabled){
                dialogPurchaseNow.isEnabled  = false
                PayOrderLogUtils.requestOrderLog("","","",PayOrderLogUtils.CLICK_BUY_BUTTON)
                model?.let { it1 -> listener.onListener(it1) }
                Handler(Looper.getMainLooper()).postDelayed({
                    dialogPurchaseNow.isEnabled  = true
                }, 2000)
            }
            //dismiss()
        }
        dialogCancel.setOnClickListener {
            dismiss()
        }


        setOutSideDismiss(true)
        PayOrderLogUtils.requestOrderLog("","","", PayOrderLogUtils.TO_PRODUCT_DETAIL)
    }

    /**
     * 设置控件颜色和背景
     */
    private fun setViewInfo(
        imgTop: ImageView,
        memberDialog: ConstraintLayout,
        dialogPurchaseNow: TextView,
        txtBuySingleTips: TextView,
        topImg: Int,
        dialogBg: Int,
        dialogTitleBg: Int,
        linkColor: Int
        ) {
        imgTop.setBackgroundResource(topImg)
        memberDialog.setBackgroundResource(dialogBg)
        dialogPurchaseNow.setBackgroundResource(dialogTitleBg)
        txtBuySingleTips.setTextColor(linkColor)
    }

    interface MemberUnitaryBuyListener {
        fun onListener(bean: BuyMemberPageEntity.ProductExt)
    }

    override fun onShowing() {
        super.onShowing()
        SDEventManager.register(this)
    }
    override fun onDismiss() {
        super.onDismiss()
        BaseConfig.getInstance.setString(SpName.orderEventId,"")
        SDEventManager.unregister(this)
    }
    //EventBus事件监听
    override fun onEvent(p0: SDBaseEvent?) = Unit
    override fun onEventMainThread(event: SDBaseEvent?) {
        Log.i("MemberBuyDialog", "onEventMainThread")
        event?.let {
            when (EnumEventTag.valueOf(it.tagInt)) {
                EnumEventTag.CLOSE_PAY_BUY_DIALOG -> {
                    dismiss()
                }

                else -> Unit
            }
        }
    }
    override fun onEventBackgroundThread(p0: SDBaseEvent?) = Unit
    override fun onEventAsync(p0: SDBaseEvent?) = Unit

}