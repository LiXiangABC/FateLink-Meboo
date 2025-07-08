package com.crush.adapter

import android.app.Activity
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.custom.base.base.BaseRecyclerAdapter
import com.crush.R
import com.crush.bean.PhotoBean
import com.github.jdsjlzx.util.SuperViewHolder
import com.luck.picture.lib.utils.ActivityCompatHelper

class AddPhotoSelectAdapter(
    listModel: ArrayList<PhotoBean>,
    mActivity: Activity,
    var onCallBack: OnCallBack?
) : BaseRecyclerAdapter<PhotoBean>(listModel, mActivity) {
    override val layoutId: Int get() = R.layout.item_add_photo

    override fun onBindItemHolder(position: Int, holder: SuperViewHolder, bean: PhotoBean, payloads: List<Any>?) {
        val itemPhotoContainer = holder.getView<ConstraintLayout>(R.id.item_photo_container)
        val itemPhotoClose = holder.getView<ImageView>(R.id.item_photo_close)
        val itemPhoto = holder.getView<ImageView>(R.id.item_photo)
        val itemPhotoAdd = holder.getView<ImageView>(R.id.item_photo_add)
        val itemTxtPhotoCover = holder.getView<TextView>(R.id.item_txt_photo_cover)
        val progressBar = holder.getView<ProgressBar>(R.id.progress_bar)
        itemTxtPhotoCover.visibility=if (position==0)View.VISIBLE else View.GONE
        if (bean.imageUrl!=""){
            itemPhotoContainer.setBackgroundResource(R.drawable.shape_solid_black_alpha_40_radius_12)
            progressBar.visibility = View.VISIBLE
            itemPhotoAdd.visibility=View.GONE
            if (!ActivityCompatHelper.isDestroy(mActivity)){
                Glide.with(mActivity)
                    .load(bean.imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            progressBar.visibility = View.GONE
                            itemPhotoClose.visibility=if (position==0) View.GONE else View.VISIBLE
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
                            itemPhotoClose.visibility=if (position==0) View.GONE else View.VISIBLE
                            return false
                        }
                    })
                    .into(itemPhoto)
            }

        }else{
            itemPhotoContainer.setBackgroundResource(if (position==0)R.drawable.shape_solid_purple_radius_12 else R.drawable.shape_solid_black_alpha_40_radius_12)
            if (bean.loading){
                itemPhotoAdd.visibility=View.GONE
                itemPhotoClose.visibility=View.GONE
                progressBar.visibility = View.VISIBLE
            }else{
                itemPhotoAdd.setImageResource(if (position==0) R.mipmap.icon_image_add_puple else R.mipmap.icon_image_add)
                itemPhotoAdd.visibility=View.VISIBLE
                itemPhotoClose.visibility=View.GONE
                progressBar.visibility = View.GONE
            }
        }
        progressBar.visibility = if (bean.loading)View.VISIBLE else View.GONE

        itemPhotoClose.setOnClickListener {
            onCallBack?.delPhoto(bean.imageUrl,position)
        }
        holder.setOnClickListener {
            onCallBack?.selectPhoto(bean.imageUrl,position)
        }
    }

    interface OnCallBack{
        fun selectPhoto(url:String,position:Int)
        fun delPhoto(url:String,position:Int)
    }
}