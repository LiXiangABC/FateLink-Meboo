package io.rong.imkit.adapter

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.custom.base.util.SDViewUtil
import com.youth.banner.adapter.BannerAdapter
import io.rong.imkit.R
import io.rong.imkit.entity.MemberSubscribeEntity

class PageBuyMemberAdapter(
    val mActivity: Context,
    val data: List<MemberSubscribeEntity.Data.Subscriptions>
) : BannerAdapter<MemberSubscribeEntity.Data.Subscriptions, PageBuyMemberAdapter.BannerViewHolder>(data) {
    //创建ViewHolder，可以用viewType这个字段来区分不同的ViewHolder
    override fun onCreateHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = SDViewUtil.getRId(parent.context, R.layout.item_buy_member_page)
        //注意，必须设置为match_parent，这个是viewpager2强制要求的
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return BannerViewHolder(view)
    }

    override fun onBindView(holder: BannerViewHolder?, bean: MemberSubscribeEntity.Data.Subscriptions, position: Int, size: Int) {
        holder?.apply {
            val imgTop = view.findViewById<ImageView>(R.id.img_top)
            val itemTitle = view.findViewById<TextView>(R.id.item_title)
            val itemContent = view.findViewById<TextView>(R.id.item_content)

            when(position){
                0->{
                    imgTop.setImageResource(R.drawable.icon_dialog_unlimited_match)
                }
                1 ->{
                    imgTop.setImageResource(R.drawable.icon_dialog_see_who_likes_you)
                }
                2 ->{
                    imgTop.setImageResource(R.drawable.icon_dialog_flash_chat)
                }
                3->{
                    imgTop.setImageResource(R.drawable.icon_dialog_private_photos)
                }
                4->{
                    imgTop.setImageResource(R.drawable.icon_dialog_private_videos)
                }
                5->{
                    imgTop.setImageResource(R.drawable.icon_dialog_release_private_album)
                }
            }

            itemTitle.text=bean.tip
            itemContent.text=bean.content
        }
    }

    inner class BannerViewHolder(var view: View) : RecyclerView.ViewHolder(
        view
    )
}