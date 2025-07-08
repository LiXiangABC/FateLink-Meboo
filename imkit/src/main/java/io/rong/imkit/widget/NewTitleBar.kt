package io.rong.imkit.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import io.rong.imkit.R

class NewTitleBar : FrameLayout {

    var mContext: Context? = null
    var viewBack:ImageView?=null
    var viewMore:ImageView?=null
    var avatar:ImageView?=null
    var imgMember:ImageView?=null
    var tvTitle: TextView?=null
    var titleContainer: ConstraintLayout?=null
    var newTitleBar: ConstraintLayout?=null
    var tvLocation: TextView?=null

    constructor(context: Context) : this(context = context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        this.mContext = context
        initView(attrs)
    }

    @SuppressLint("MissingInflatedId", "CutPasteId")
    private fun initView(attrs: AttributeSet?) {
       val view = LayoutInflater.from(mContext).inflate(R.layout.view_new_title_bar,this)
        newTitleBar = view.findViewById(R.id.new_title_bar)
        viewBack = view.findViewById(R.id.viewBack)
        viewMore = view.findViewById(R.id.viewMore)
        tvTitle = view.findViewById(R.id.tvTitle)
        avatar = view.findViewById(R.id.avatar)
        imgMember = view.findViewById(R.id.img_member)
        tvLocation = view.findViewById(R.id.tvLocation)
        titleContainer = view.findViewById(R.id.title_container)
    }

}