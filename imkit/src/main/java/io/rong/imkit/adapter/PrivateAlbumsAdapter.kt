package io.rong.imkit.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.rong.imkit.R
import io.rong.imkit.entity.ImagesBean

import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.rong.common.FileUtils
import io.rong.imkit.picture.tools.DateUtils

class PrivateAlbumsAdapter(
    var context: Context,
    var mUrls: ArrayList<ImagesBean>,
    var listener: OnCallBack
) :
    RecyclerView.Adapter<PrivateAlbumsAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_private_albums, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val url = mUrls[position].imageUrl
        holder.progressBar.visibility = View.VISIBLE
        holder.imageView.setOnClickListener {
            listener.callback(position)
        }
        val suffix = FileUtils.getSuffix(url)
        if (suffix.equals("mp4", true) || suffix.equals("3gp", true) || suffix.equals("mov", true)) {
            holder.videoTimes.visibility = View.VISIBLE
            holder.videoTimes.text = mUrls[position].videoLength?.let {
                DateUtils.timeConversion(it / 1000)
            }
        }
        Glide.with(context)
            .load(url)
            .error(R.drawable.rc_send_thumb_image_broken)
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

    override fun getItemCount(): Int {
        return mUrls.size
    }

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView
        var progressBar: ProgressBar
        var videoTimes: TextView

        init {
            imageView = itemView.findViewById(R.id.image_view)
            progressBar = itemView.findViewById(R.id.progress_bar)
            videoTimes = itemView.findViewById(R.id.video_times)
        }
    }

    interface OnCallBack {
        fun callback(position: Int)
    }
}