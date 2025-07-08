package com.crush.dialog

import android.animation.ObjectAnimator
import android.app.Activity
import android.graphics.Paint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.crush.R
import com.crush.view.WheelView
import razerdp.basepopup.BasePopupWindow


class UpdateHeightDialog(var ctx: Activity,var currentHeight: String,var hideHeight:Boolean ,var listener: onCallBack) :  BasePopupWindow(ctx) {
    private val localArrayList = arrayListOf("3\'07\"","3\'08\"","3\'09\"","3\'10\"","3\'11\"","3\'12\""
        ,"4\'00\"","4\'01\"","4\'02\"","4\'03\"","4\'04\"","4\'05\"","4\'06\"","4\'07\"","4\'08\"","4\'09\"","4\'10\"","4\'11\"","4\'12\"",
        "5\'00\"","5\'01\"","5\'02\"","5\'03\"","5\'04\"","5\'05\"","5\'06\"","5\'07\"","5\'08\"","5\'09\"","5\'10\"","5\'11\"","5\'12\"",
        "6\'00\"","6\'01\"","6\'02\"","6\'03\"","6\'04\"","6\'05\"","6\'06\"","6\'07\"","6\'08\"","6\'09\"","6\'10\"","6\'11\"","6\'12\"",
        "7\'00\"","7\'01\"","7\'02\"","7\'03\"","7\'04\"","7\'05\"","7\'06\"","7\'07\"","7\'08\"","7\'09\"","7\'10\"")
    private var halfPosition=30
    init {
        setContentView(R.layout.dialog_modify_height)
        initView()
    }

    private fun initView() {
        val outsideView = findViewById<View>(R.id.outside_view)
        val dialogClose = findViewById<ImageView>(R.id.dialog_close)
        val heightList = findViewById<WheelView>(R.id.height_list)
        val txtHeightSave = findViewById<TextView>(R.id.txt_height_save)
        val txtHideHeight = findViewById<TextView>(R.id.txt_hide_height)
        val imgHeightLeg = findViewById<ImageView>(R.id.img_height_leg)
        txtHideHeight.paint.flags = Paint.UNDERLINE_TEXT_FLAG
        for (i in 0 until localArrayList.size){
            if (currentHeight== localArrayList[i]){
                halfPosition=i
                break
            }
        }
        heightList.items = localArrayList
        heightList.selectIndex(halfPosition)

        txtHideHeight.text=if (hideHeight)ctx.getString(R.string.unhide_height) else ctx.getString(R.string.hide_height)
        val width =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED)
        val height =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED)
        imgHeightLeg.measure(width,height)
        val viewWidth=imgHeightLeg.measuredWidth.toFloat()/4
        val moveI =viewWidth-(viewWidth / (localArrayList.size.toFloat()/(halfPosition+1)))
        ObjectAnimator.ofFloat(imgHeightLeg,"translationX",0f, -moveI).setDuration(0).start()

        heightList.setOnWheelItemSelectedListener(object :WheelView.OnWheelItemSelectedListener{
            override fun onWheelItemChanged(wheelView: WheelView?, position: Int) {
                val moveI =viewWidth-(viewWidth / (localArrayList.size.toFloat()/(position+1)))

                ObjectAnimator.ofFloat(imgHeightLeg,"translationX",0f,-moveI).setDuration(0).start()
                halfPosition=position
            }

            override fun onWheelItemSelected(wheelView: WheelView?, position: Int) {

            }

        })


        txtHeightSave.setOnClickListener {
            listener.onCallback(localArrayList[halfPosition],hideHeight)
            dismiss()
        }
        txtHideHeight.setOnClickListener {
            listener.onCallback("0",!hideHeight)
            dismiss()
        }

        dialogClose.setOnClickListener {
            dismiss()
        }
        outsideView.setOnClickListener {
            dismiss()
        }

        setOutSideDismiss(true)
    }
    interface onCallBack{
       fun onCallback(value:String,hideHeight: Boolean)
    }

}