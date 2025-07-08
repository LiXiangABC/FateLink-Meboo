package io.rong.imkit.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.sunday.eventbus.SDEventManager
import io.rong.imkit.R
import io.rong.imkit.event.EnumEventTag
import io.rong.imkit.pay.PayUtils

/**
 * 订单确认页面
 */
class OrderConfirmActivity : AppCompatActivity() {
    private val type by lazy { intent.getStringExtra(ORDER_CONFIRM_TYPE)!! }
    private var conOrderConfirmTop: ConstraintLayout? = null
    private var txtOrderConfirmTopProjectVal: TextView? = null
    private var txtOrderConfirmTopBenefitsVal: TextView? = null
    private var txtOrderConfirmTopPeriodVal: TextView? = null
    private var txtOrderConfirmTopUse: TextView? = null
    private var txtOrderConfirmTopTotalVal: TextView? = null
    private var txtOrderConfirmBottomTitle: TextView? = null
    private var rvOrderConfirmMember: RecyclerView? = null
    private var imgOrderConfirmPpPv: ImageView? = null
    private var imgOrderConfirmFc: ImageView? = null
    private var txtOrderConfirmProtocol: TextView? = null
    private var txtOrderConfirmMemberBtn: TextView? = null
    private var txtOrderConfirmPPBtn: TextView? = null
    private var txtOrderConfirmPVBtn: TextView? = null
    private var txtOrderConfirmFCBtn: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_order_confirm)
        initView()
    }

    private fun initView() {
        conOrderConfirmTop = findViewById(R.id.conOrderConfirmTop)
        txtOrderConfirmTopProjectVal = findViewById(R.id.txtOrderConfirmTopProjectVal)
        txtOrderConfirmTopBenefitsVal = findViewById(R.id.txtOrderConfirmTopBenefitsVal)
        txtOrderConfirmTopPeriodVal = findViewById(R.id.txtOrderConfirmTopPeriodVal)
        txtOrderConfirmTopUse = findViewById(R.id.txtOrderConfirmTopUse)
        txtOrderConfirmTopTotalVal = findViewById(R.id.txtOrderConfirmTopTotalVal)
        txtOrderConfirmBottomTitle = findViewById(R.id.txtOrderConfirmBottomTitle)
        rvOrderConfirmMember = findViewById(R.id.rvOrderConfirmMember)
        imgOrderConfirmPpPv = findViewById(R.id.imgOrderConfirmPpPv)
        imgOrderConfirmFc = findViewById(R.id.imgOrderConfirmFc)
        txtOrderConfirmProtocol = findViewById(R.id.txtOrderConfirmProtocol)
        txtOrderConfirmMemberBtn = findViewById(R.id.txtOrderConfirmMemberBtn)
        txtOrderConfirmPPBtn = findViewById(R.id.txtOrderConfirmPPBtn)
        txtOrderConfirmPVBtn = findViewById(R.id.txtOrderConfirmPVBtn)
        txtOrderConfirmFCBtn = findViewById(R.id.txtOrderConfirmFCBtn)
        setData()
        txtOrderConfirmMemberBtn?.setOnClickListener {
            //PayUtils.instance.launchGooglePay()
        }
        txtOrderConfirmPPBtn?.setOnClickListener {
            //PayUtils.instance.launchGooglePay()
        }
        txtOrderConfirmPVBtn?.setOnClickListener {
            //PayUtils.instance.launchGooglePay()
        }
        txtOrderConfirmFCBtn?.setOnClickListener {
            //PayUtils.instance.launchGooglePay()
        }
        //关闭购买弹窗
        SDEventManager.post(EnumEventTag.CLOSE_PAY_BUY_DIALOG.ordinal)
    }

    //根据传入不同的类型显示不同的布局
    @SuppressLint("SetTextI18n")
    private fun setData() {
        if (type.contains("pp")) {
            conOrderConfirmTop?.setBackgroundResource(R.mipmap.img_order_confirm_pp_bg)
            txtOrderConfirmTopUse?.visibility = View.VISIBLE
            txtOrderConfirmBottomTitle?.setBackgroundResource(R.mipmap.img_order_confirm_pp_bottom_bg)
            txtOrderConfirmBottomTitle?.text = "How to Use"
            txtOrderConfirmBottomTitle?.setTextColor(Color.parseColor("#FFFFFF"))
            rvOrderConfirmMember?.visibility = View.GONE
            imgOrderConfirmPpPv?.visibility = View.VISIBLE
            imgOrderConfirmPpPv?.setImageResource(R.mipmap.img_order_confirm_pp)
            imgOrderConfirmFc?.visibility = View.GONE
            txtOrderConfirmPPBtn?.visibility = View.VISIBLE
            txtOrderConfirmPVBtn?.visibility = View.GONE
            txtOrderConfirmFCBtn?.visibility = View.GONE
        } else if (type.contains("pv")) {
            conOrderConfirmTop?.setBackgroundResource(R.mipmap.img_order_confirm_pv_bg)
            txtOrderConfirmTopUse?.visibility = View.VISIBLE
            txtOrderConfirmBottomTitle?.setBackgroundResource(R.mipmap.img_order_confirm_pv_bottom_bg)
            txtOrderConfirmBottomTitle?.text = "How to Use"
            txtOrderConfirmBottomTitle?.setTextColor(Color.parseColor("#FFFFFF"))
            rvOrderConfirmMember?.visibility = View.GONE
            imgOrderConfirmPpPv?.visibility = View.VISIBLE
            imgOrderConfirmPpPv?.setImageResource(R.mipmap.img_order_confirm_pv)
            imgOrderConfirmFc?.visibility = View.GONE
            txtOrderConfirmPPBtn?.visibility = View.GONE
            txtOrderConfirmPVBtn?.visibility = View.VISIBLE
            txtOrderConfirmFCBtn?.visibility = View.GONE
        } else if (type.contains("fc")) {
            conOrderConfirmTop?.setBackgroundResource(R.mipmap.img_order_confirm_fc_bg)
            txtOrderConfirmTopUse?.visibility = View.VISIBLE
            txtOrderConfirmBottomTitle?.setBackgroundResource(R.mipmap.img_order_confirm_fc_bottom_bg)
            txtOrderConfirmBottomTitle?.text = "How to Use"
            txtOrderConfirmBottomTitle?.setTextColor(Color.parseColor("#FFFFFF"))
            rvOrderConfirmMember?.visibility = View.GONE
            imgOrderConfirmPpPv?.visibility = View.GONE
            imgOrderConfirmFc?.visibility = View.VISIBLE
            txtOrderConfirmPPBtn?.visibility = View.GONE
            txtOrderConfirmPVBtn?.visibility = View.GONE
            txtOrderConfirmFCBtn?.visibility = View.VISIBLE
        } else {
            conOrderConfirmTop?.setBackgroundResource(R.mipmap.img_order_confirm_member_bg)
            txtOrderConfirmTopUse?.visibility = View.GONE
            txtOrderConfirmBottomTitle?.setBackgroundResource(R.mipmap.img_order_confirm_member_bottom_bg)
            txtOrderConfirmBottomTitle?.text = "Exclusive Premium Picks"
            txtOrderConfirmBottomTitle?.setTextColor(Color.parseColor("#FFFFFF"))
            rvOrderConfirmMember?.visibility = View.VISIBLE
            imgOrderConfirmPpPv?.visibility = View.GONE
            imgOrderConfirmFc?.visibility = View.GONE
            txtOrderConfirmPPBtn?.visibility = View.GONE
            txtOrderConfirmPVBtn?.visibility = View.GONE
            txtOrderConfirmFCBtn?.visibility = View.GONE
        }
    }

    companion object {
        private const val ORDER_CONFIRM_TYPE = "ORDER_CONFIRM_TYPE"
        fun start(context: Context, type: String) {
            context.startActivity(Intent(context, OrderConfirmActivity::class.java).apply {
                putExtra(ORDER_CONFIRM_TYPE, type)
            })
        }
    }

}