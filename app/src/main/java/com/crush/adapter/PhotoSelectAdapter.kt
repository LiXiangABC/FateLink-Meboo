package com.crush.adapter

import android.app.Activity
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.custom.base.base.BaseRecyclerAdapter
import com.crush.R
import com.crush.bean.PhotoBean
import com.crush.bean.UserPhotoBean
import com.crush.util.GlideUtil
import com.github.jdsjlzx.util.SuperViewHolder
import io.rong.imkit.utils.RongUtils

class PhotoSelectAdapter(
    listModel: ArrayList<UserPhotoBean>,
    mActivity: Activity,
    var onCallBack: OnCallBack?
) : BaseRecyclerAdapter<UserPhotoBean>(listModel, mActivity) {
    override val layoutId: Int get() = R.layout.item_select_phone

    override fun onBindItemHolder(position: Int, holder: SuperViewHolder, bean: UserPhotoBean, payloads: List<Any>?) {
        val itemPhotoContainer = holder.getView<ConstraintLayout>(R.id.item_photo_container)
        val itemPhotoClose = holder.getView<ImageView>(R.id.item_photo_close)
        val itemPhoto = holder.getView<ImageView>(R.id.item_photo)
        val itemPhotoAdd = holder.getView<ImageView>(R.id.item_photo_add)
        val itemTxtPhotoCover = holder.getView<TextView>(R.id.item_txt_photo_cover)
        val progressBar = holder.getView<ProgressBar>(R.id.progress_bar)
        itemTxtPhotoCover.isVisible= position==0
        if (bean.imageUrl!=""){
            itemPhotoContainer.setBackgroundResource(R.drawable.shape_stroke_black_radius_12)
            progressBar.visibility = View.VISIBLE
            itemPhotoAdd.visibility=View.GONE
            if (!RongUtils.isDestroy(mActivity)) {
                Glide.with(mActivity)
                    .load(bean.imageLoadUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            progressBar.visibility = View.GONE
                            itemPhotoClose.visibility =
                                if (position == 0) View.GONE else View.VISIBLE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            progressBar.visibility = View.GONE
                            itemPhotoClose.visibility =
                                if (position == 0) View.GONE else View.VISIBLE
                            return false
                        }
                    })
                    .into(itemPhoto)
            }
        }else{
            itemPhotoContainer.setBackgroundResource(R.drawable.shape_stroke_black_radius_12)
            itemPhoto.setImageResource(R.drawable.shape_dotted_stroke_gray)
            if (bean.loading){
                itemPhotoAdd.visibility=View.GONE
                itemPhotoClose.visibility=View.GONE
                progressBar.visibility = View.VISIBLE
            }else{
                itemPhotoAdd.visibility=View.VISIBLE
                itemPhotoClose.visibility=View.GONE
                progressBar.visibility = View.GONE
            }
        }
        progressBar.visibility = if (bean.loading)View.VISIBLE else View.GONE

        itemPhotoClose.setOnClickListener {
            onCallBack?.delPhoto(bean,position)
        }
        holder.setOnClickListener {
            onCallBack?.selectPhoto(bean,position)
        }
    }

    interface OnCallBack{
        fun selectPhoto(bean:UserPhotoBean,position:Int)
        fun delPhoto(bean:UserPhotoBean,position:Int)
    }
}