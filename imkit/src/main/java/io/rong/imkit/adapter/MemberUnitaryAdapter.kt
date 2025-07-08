package io.rong.imkit.adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.setPadding
import com.custom.base.base.BaseRecyclerContextAdapter
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.github.jdsjlzx.util.SuperViewHolder
import io.rong.imkit.R
import io.rong.imkit.entity.BuyMemberPageEntity
import io.rong.imkit.utils.DensityUtil
import io.rong.imkit.utils.TextUtils

class MemberUnitaryAdapter(
    listModel: ArrayList<BuyMemberPageEntity.ProductExt>,
    activity: Context,
    var productCategory: Int,
    var onListener: OnListener?
) : BaseRecyclerContextAdapter<BuyMemberPageEntity.ProductExt>(listModel, activity) {
    override val layoutId: Int
        get() = R.layout.item_member_unitary

    override fun onBindItemHolder(
        position: Int,
        holder: SuperViewHolder,
        model: BuyMemberPageEntity.ProductExt,
        payloads: List<Any>?
    ) {
        val itemContainer = holder.getView<ConstraintLayout>(R.id.item_container)
        val itemTitle = holder.getView<TextView>(R.id.item_title)
        val itemNumber = holder.getView<TextView>(R.id.item_number)
        val itemName = holder.getView<TextView>(R.id.item_name)
        val itemDiscountedPrice = holder.getView<TextView>(R.id.item_discounted_price)

        itemTitle.visibility=if (model.tip!=null && model.tip!="") View.VISIBLE else View.INVISIBLE
        itemTitle.text = model.tip
        itemNumber.text = model.benefitNum
        itemName.text = model.benefitUnit
        TextUtils.setSymbolText(itemDiscountedPrice,"${model.currencyType} ${model.price}",model.currencyType)

        val selectBg = when (productCategory) {
            2 -> R.mipmap.icon_fl_item_check_bg
            3 -> R.mipmap.icon_pp_item_check_bg
            4 -> R.mipmap.icon_pv_item_check_bg
            else -> 0

        }
        val unSelectBg = when (productCategory) {
            2 -> R.mipmap.icon_fl_item_uncheck_bg
            3 -> R.mipmap.icon_pp_item_uncheck_bg
            4 -> R.mipmap.icon_pv_item_uncheck_bg
            else -> 0
        }

        val selectTitleBg =when (productCategory) {
            2 -> R.drawable.shape_solid_radius_6_fl_select
            3 -> R.drawable.shape_solid_radius_6_pp_select
            4 -> R.drawable.shape_solid_radius_6_pv_select
            else -> 0
        }
        val unSelectTitleBg =when (productCategory) {
            2 -> R.drawable.shape_solid_radius_6_fl_unselect
            3 -> R.drawable.shape_solid_radius_6_pp_unselect
            4 -> R.drawable.shape_solid_radius_6_pv_unselect
            else -> 0
        }
        val selectColor=when (productCategory) {
            2,3,4 -> Color.WHITE
            else -> 0
        }
        val unSelectColor=when (productCategory) {
            2 -> Color.parseColor("#FF3B3B")
            3 -> Color.parseColor("#BE4CFF")
            4 ->  Color.parseColor("#23D183")
            else -> 0
        }
        itemContainer.setPadding(DensityUtil.dip2px(mActivity,if (model.check==true)10f else 7f))
        val layoutParams = itemContainer.layoutParams
        layoutParams.width=DensityUtil.dip2px(mActivity,if (model.check==true)133f else 90f)
        layoutParams.height=DensityUtil.dip2px(mActivity,if (model.check==true)190f else 122f)
        itemContainer.layoutParams=layoutParams
        if (model.check==true) {
            YoYo.with(Techniques.Pulse)
                .duration(300)
                .playOn(itemContainer)
//            YoYo.with(Techniques.Pulse)
//                .duration(300)
//                .playOn(itemTitle)
        }

        itemTitle.setBackgroundResource(selectTitleBg)
        itemContainer.setBackgroundResource(if (model.check == true)selectBg else unSelectBg)
        itemNumber.setTextColor(
            if (model.check == true) selectColor else unSelectColor
        )
        itemName.setTextColor(
            if (model.check == true) selectColor else unSelectColor
        )

        itemDiscountedPrice.setTextColor(
            if (model.check == true) selectColor else unSelectColor
        )

        itemNumber.textSize= if (model.check == true) 34f else 20f
        itemName.textSize= if (model.check == true) 22f else 14f
        itemDiscountedPrice.textSize= if (model.check == true) 20f else 14f


        itemContainer.setOnClickListener {
            onListener?.onListener(position)
        }

    }

    interface OnListener{
        fun onListener(position: Int)
    }
}