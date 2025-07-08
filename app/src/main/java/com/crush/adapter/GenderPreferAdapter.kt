package com.crush.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.util.DisplayMetrics
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.crush.R
import com.crush.bean.GenderPreferListBean
import com.crush.bean.TurnOnsListBean
import com.crush.util.GlideUtil

import com.custom.base.base.BaseRecyclerAdapter
import com.github.jdsjlzx.util.SuperViewHolder
import com.makeramen.roundedimageview.RoundedImageView
import io.rong.imkit.widget.refresh.util.DesignUtil

class GenderPreferAdapter(
    var ctx:Activity,
    listModel: ArrayList<GenderPreferListBean>,
    var listener:OnListener
) : BaseRecyclerAdapter<GenderPreferListBean>(listModel, ctx) {
    override val layoutId: Int get() = R.layout.item_gender_prefer
    // 扩展函数，将 dp 转换为像素值
    fun Int.dpToPx(displayMetrics: DisplayMetrics): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), displayMetrics).toInt()
    }
    override fun onBindItemHolder(position: Int, holder: SuperViewHolder, model: GenderPreferListBean, payloads: List<Any>?) {
        val itemLogoOut = holder.getView<CardView>(R.id.item_logo_out)
        val itemLogo = holder.getView<ImageView>(R.id.item_logo)
        val itemText = holder.getView<TextView>(R.id.item_text)
        GlideUtil.setImageView(model.iconUrl, itemLogo, placeholderImageId = 0)

        itemLogoOut.setCardBackgroundColor(if (model.selected==0) Color.TRANSPARENT else ContextCompat.getColor(ctx,R.color.color_44F3C4))
        itemText.setTextColor(if (model.selected==0) ContextCompat.getColor(ctx,R.color.color_44F3C4) else ContextCompat.getColor(ctx,R.color.color_001912))
        itemText.text=model.value


        holder.setOnClickListener {
            listener.onListener(position)
        }

    }
    interface OnListener{
        fun onListener(position: Int)
    }
}