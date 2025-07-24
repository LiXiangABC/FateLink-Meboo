package io.rong.imkit.adapter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.custom.base.util.SDViewUtil
import com.makeramen.roundedimageview.RoundedImageView
import com.youth.banner.adapter.BannerAdapter
import de.hdodenhof.circleimageview.CircleImageView
import io.rong.imkit.R
import io.rong.imkit.entity.WLMListBean
import io.rong.imkit.utils.GlideUtil
import io.rong.imkit.utils.RongDateUtils

class ViewChatHeaderPageLoaderAdapter(
    val mActivity: Activity,
    var isMember: Boolean,
    val data: ArrayList<WLMListBean>,
) : BannerAdapter<WLMListBean, ViewChatHeaderPageLoaderAdapter.BannerViewHolder>(data) {
    //创建ViewHolder，可以用viewType这个字段来区分不同的ViewHolder

    override fun onCreateHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = SDViewUtil.getRId(parent.context, R.layout.item_view_chat_header_wlm)
        //注意，必须设置为match_parent，这个是viewpager2强制要求的
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return BannerViewHolder(view)
    }

    override fun onBindView(
        holder: BannerViewHolder?,
        bean: WLMListBean,
        position: Int,
        size: Int
    ) {
        holder?.apply {
            val iv = view.findViewById<CircleImageView>(R.id.ivHeadImg)
            GlideUtil.setImageView(
                bean.avatarUrl,
                iv,
                !isMember,
            )
            val tvWlmContent = view.findViewById<TextView>(R.id.tvWlmContent)
            val tvWlmTime = view.findViewById<TextView>(R.id.tvWlmTime)
            tvWlmContent.text=bean.greetingContent
            tvWlmTime.text= bean.wlmtime?.let { RongDateUtils.getConversationListFormatDate(it, mActivity) }

        }
    }
    class BannerViewHolder(var view: View) : RecyclerView.ViewHolder(
        view
    )
}