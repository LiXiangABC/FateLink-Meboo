package com.crush.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.crush.R
import com.crush.bean.ImagesBean
import com.crush.ui.look.LookImagesActivity
import com.crush.ui.look.MyPictureVideoPlayActivity
import com.crush.util.DateUtils
import com.crush.util.IntentUtil
import io.rong.imkit.activity.Activities
import io.rong.imkit.utils.RongUtils


class PrivatePhotoAdapter(
    var context: Context,
    var mUrls: ArrayList<ImagesBean>,
    var albumsCode: String,
    var listener: OnCallBack
) :
    RecyclerView.Adapter<PrivatePhotoAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_private_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val url = mUrls[position].imageUrl
        if (url == "") {
            if (mUrls[position].loading) {
                holder.progressBar.visibility = View.VISIBLE
                holder.imageView.setImageResource(R.color.color_202323)
            } else {
                holder.privateAlbumAdd.visibility = View.VISIBLE
                holder.imageView.visibility = View.GONE
                holder.progressBar.visibility = View.GONE
                holder.privateAlbumAdd.setOnClickListener {
                    listener.callback()
                }
            }
        } else {
            holder.progressBar.visibility = View.VISIBLE
            holder.imageView.setOnClickListener {
                val bundle = Bundle()
                bundle.putStringArrayList(
                    "images",
                    arrayListOf(url)
                )
                bundle.putInt("index", 0)
                bundle.putInt(
                    "type",
                    if (url.contains("mp4", true) || url.contains(
                            "3gp",
                            true
                        ) || url.contains("mov", true)
                    ) 1 else 0
                )//0照片 1视频
                bundle.putInt("position", position)
                bundle.putString("imageCode", mUrls[position].imageCode)
                bundle.putString("albumCode", albumsCode)
                if (url.contains("mp4", true) || url.contains("3gp", true) || url.contains(
                        "mov",
                        true
                    )
                ) {
                    val intent = Intent(context, MyPictureVideoPlayActivity::class.java)
                    val b = Bundle()
                    b.putString("video_path", url)
                    b.putInt("position", position)
                    b.putString("imageCode", mUrls[position].imageCode)
                    b.putString("albumCode", albumsCode)
                    intent.putExtras(b)
                    context.startActivity(intent)
                } else {
                    IntentUtil.startActivity(LookImagesActivity::class.java, bundle)
                }

            }
            holder.privateAlbumAdd.visibility = View.GONE
            holder.imageView.visibility = View.VISIBLE
            if (url.contains("mp4", true) || url.contains("3gp", true) || url.contains(
                    "mov",
                    true
                )
            ) {
                holder.videoTimes.visibility = View.VISIBLE
                holder.videoTimes.text = mUrls[position].videoLength?.let {
                    DateUtils.timeConversion(
                        it / 1000
                    )
                }
            } else {
                holder.videoTimes.visibility = View.GONE
            }
            if (!RongUtils.isDestroy(Activities.get().top)){
                Activities.get().top?.let {
                    Glide.with(it)
                        .load(url)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                holder.progressBar.visibility = View.GONE
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                holder.progressBar.visibility = View.GONE;
                                return false
                            }

                        })
                        .into(holder.imageView)
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return mUrls.size
    }

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView
        var progressBar: ProgressBar
        var videoTimes: TextView
        var privateAlbumAdd: RelativeLayout

        init {
            imageView = itemView.findViewById(R.id.image_view)
            progressBar = itemView.findViewById(R.id.progress_bar)
            videoTimes = itemView.findViewById(R.id.video_times)
            privateAlbumAdd = itemView.findViewById(R.id.private_album_add)
        }
    }

    interface OnCallBack {
        fun callback()
    }
}