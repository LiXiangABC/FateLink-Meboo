package io.rong.imkit.dialog

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.sunday.eventbus.SDEventManager
import io.rong.imkit.R
import io.rong.imkit.event.EnumEventTag
import razerdp.basepopup.BasePopupWindow

class MemberBuySingleSuccessDialog(var ctx: Context,var productCategory:Int,var times:String ,var listener : ChangeMembershipListener) :  BasePopupWindow(ctx) {
    init {
        setContentView(R.layout.dialog_member_buy_single_success)
        initView()
    }

    private fun initView() {
        val dialogConfirm = findViewById<TextView>(R.id.dialog_confirm)
        val dialogTitle = findViewById<TextView>(R.id.dialog_title)
        val dialogContent = findViewById<TextView>(R.id.dialog_content)
        val topLogo = findViewById<ImageView>(R.id.top_logo)
        val dialogClose = findViewById<ImageView>(R.id.dialog_close)
        val contentContainer = findViewById<ConstraintLayout>(R.id.content_container)

        when(productCategory){
            2->{
                dialogTitle.setTextColor(ContextCompat.getColor(ctx,R.color.color_FF5344))
                dialogContent.setTextColor(ContextCompat.getColor(ctx,R.color.color_FF5344))
                dialogConfirm.setBackgroundResource(R.drawable.shape_member_fc_buy_button_bg)
                contentContainer.setBackgroundResource(R.drawable.icon_member_buy_fc_success_bg)

                dialogTitle.text=ctx.getString(R.string.flash_chat_title)
                dialogContent.text="You have successfully purchased Flash Chat $times  ${if (times != "1")"times" else "time"}. Go ahead and start using it!"
                topLogo.setImageResource(R.drawable.icon_dialog_flash_chat_dialog)
                SDEventManager.post(times,EnumEventTag.FLASH_CHAT_END_NUM_ADD.ordinal)
            }
            3->{
                dialogTitle.setTextColor(ContextCompat.getColor(ctx,R.color.color_B736FF))
                dialogContent.setTextColor(ContextCompat.getColor(ctx,R.color.color_B736FF))
                dialogConfirm.setBackgroundResource(R.drawable.shape_member_pp_buy_button_bg)
                contentContainer.setBackgroundResource(R.drawable.icon_member_buy_pp_success_bg)

                dialogTitle.text=ctx.getString(R.string.unlock_private_photos_title)
                dialogContent.text="You have successfully purchased Private Photo $times  ${if (times != "1")"times" else "time"}. Go ahead and start using it!"
                topLogo.setImageResource(R.drawable.icon_dialog_private_photos_dialog)
            }
            4->{
                dialogTitle.setTextColor(ContextCompat.getColor(ctx,R.color.color_00A45A))
                dialogContent.setTextColor(ContextCompat.getColor(ctx,R.color.color_00A45A))
                dialogConfirm.setBackgroundResource(R.drawable.shape_member_pv_buy_button_bg)
                contentContainer.setBackgroundResource(R.drawable.icon_member_buy_pv_success_bg)

                dialogTitle.text=ctx.getString(R.string.unlock_private_videos_title)
                dialogContent.text="You have successfully purchased Private Video $times  ${if (times != "1")"times" else "time"}. Go ahead and start using it!"
                topLogo.setImageResource(R.drawable.icon_dialog_private_videos_dialog)
            }
        }
        dialogClose.setOnClickListener {
            listener.onListener()
            dismiss()
        }
        dialogConfirm.setOnClickListener {
            listener.onListener()
            dismiss()
        }

        setOutSideDismiss(true)
    }

    interface ChangeMembershipListener{
        fun onListener()
    }

}