package com.crush.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.crush.R
import com.crush.bean.MatchIndexBean
import com.crush.dot.DotLogEventName
import com.crush.dot.DotLogUtil
import com.crush.ui.chat.profile.UserProfileInfoActivity
import com.crush.ui.my.turnons.ChooseTurnOnsActivity
import com.crush.util.AnimUtil
import com.crush.util.CollectionUtils
import com.crush.view.LinearLineIndicator
import com.crush.view.TagCloudView
import com.custom.base.config.BaseConfig
import com.crush.util.IntentUtil
import com.sunday.eventbus.SDEventManager
import com.youth.banner.Banner
import com.youth.banner.config.IndicatorConfig
import com.youth.banner.util.BannerUtils
import io.rong.imkit.SpName
import io.rong.imkit.activity.Activities
import io.rong.imkit.event.EnumEventTag
import io.rong.imkit.event.FirebaseEventTag
import io.rong.imkit.utils.FirebaseEventUtils
import io.rong.imkit.utils.JsonUtils
import org.json.JSONObject

class CardStackAdapter(
    private var spots: List<MatchIndexBean> = emptyList(),
    private var turnOns: Boolean,
    private var turnOnsLimit: Int,
    private var newUserFlag: Boolean,
) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {
//    private var showFeedback = false
    private var showLocation = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_spot, parent, false))
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.indexLocationContainer.visibility=View.GONE
//            holder.feedbackContainer.visibility=View.GONE
        if (!turnOns && position == turnOnsLimit) {
                turnOnsView(position, holder)
            } else {
                //取locationLimit的倍数进行计算，余数为0
                if (showLocation) {
                    requestLocation(position, holder)
//                } else if (showFeedback){
//                    feedBackView(holder)
                }else {
                    BaseConfig.getInstance.setBoolean(SpName.userCard+position,true)
                    holder.userContainer.visibility = View.VISIBLE
                    holder.indexTurnOnsContainer.visibility = View.GONE

                    val spot = spots[position]
                    holder.name.text = "${spot.nickName} · ${spot.age}"
                    holder.itemMemberLogo.visibility=if (spot.userType==2)View.VISIBLE else View.GONE

                    holder.online.visibility = if (spot.online == 1) View.VISIBLE else View.GONE
                    holder.positioning.visibility = if (spot.nearby == 1) View.VISIBLE else View.GONE


                    Activities.get().top?.let {
                        val linearLineIndicator = LinearLineIndicator(it)
                        if (CollectionUtils.isNotEmpty(spot.images)) {
                            if (spot.images.size > 1) {
                                holder.userBanner.setUserInputEnabled(false)
                                linearLineIndicator.setIndicatorViewGravity(1)
                                holder.userBanner.indicator = linearLineIndicator
                                holder.userBanner.setIndicatorMargins(
                                    IndicatorConfig.Margins(
                                        0, BannerUtils.dp2px(10f).toInt(), 0, 0
                                    )
                                )
                            }
                            holder.userBanner.adapter = PageLoaderAdapter(it, spot.images)
                            holder.userBanner.viewPager2.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                                override fun onPageSelected(position: Int) {
                                    linearLineIndicator.onPageSelected(position)
                                    super.onPageSelected(position)
                                }
                            })
                        }
                    }

                    holder.leftClick.setOnClickListener {
                        if (CollectionUtils.isNotEmpty(spot.images)) {
                            if (spot.images.size > 1) {
                                if (holder.userBanner.currentItem != 0) {
                                    holder.userBanner.currentItem =
                                        holder.userBanner.currentItem - 1
                                } else {
                                    AnimUtil().shake(holder.itemView)
                                }
                                FirebaseEventUtils.logEvent(FirebaseEventTag.Home_Viewphotos.name)
                            } else {
                                AnimUtil().shake(holder.itemView)
                            }
                        } else {
                            AnimUtil().shake(holder.itemView)
                        }

                    }
                    holder.rightClick.setOnClickListener {
                        if (CollectionUtils.isNotEmpty(spot.images)) {
                            if (spot.images.size > 1) {
                                if (holder.userBanner.currentItem != (spot.images.size - 1)) {
                                    holder.userBanner.currentItem =
                                        holder.userBanner.currentItem + 1
                                } else {
                                    AnimUtil().shake(holder.itemView)

                                }
                                FirebaseEventUtils.logEvent(FirebaseEventTag.Home_Viewphotos.name)
                            } else {
                                AnimUtil().shake(holder.itemView)

                            }
                        } else {
                            AnimUtil().shake(holder.itemView)

                        }
                    }
                    holder.bottomClick.setOnClickListener {
                        FirebaseEventUtils.logEvent(FirebaseEventTag.Home_Profile.name)
                        val bundle = Bundle()
                        bundle.putString("userCode", spot.userCode)
                        bundle.putString("userCodeFriend", spot.friendUserCode)
                        bundle.putBoolean("isIndex", true)
                        IntentUtil.startActivity(
                            UserProfileInfoActivity::class.java,
                            bundle
                        )
                    }
                    if (spot.turnOnsListSize != null && spot.turnOnsListSize > 0) {
                        holder.turnOnsContainer.visibility = View.VISIBLE
                        holder.tagCloud.visibility = View.GONE
                        holder.txtTurnOnsSize.text =
                            "${spot.turnOnsListSize} ${Activities.get().top?.getString(R.string.turns_ons)}"
                        holder.turnOnsContainer.setOnClickListener {
                            val bundle = Bundle()
                            bundle.putString("userCode", spot.userCode)
                            bundle.putString("userCodeFriend", spot.friendUserCode)
                            bundle.putBoolean("turnOpen", true)
                            bundle.putBoolean("isIndex", true)
                            IntentUtil.startActivity(
                                UserProfileInfoActivity::class.java,
                                bundle
                            )
                        }
                    } else {
                        holder.turnOnsContainer.visibility = View.GONE
                        holder.tagCloud.visibility = View.VISIBLE
                        val tags = arrayListOf<String>()
                        if (CollectionUtils.isNotEmpty(spot.interests)) {
                            repeat(spot.interests.size) {
                                tags.add(spot.interests[it].interest)
                            }
                        }
                        holder.tagCloud.setTags(tags)
                    }
                }
            }
    }

    /**
     * turnOns卡片展示
     */
    private fun turnOnsView(position: Int, holder: ViewHolder) {
        BaseConfig.getInstance.setBoolean(SpName.userCard + position, false)
        holder.userContainer.visibility = View.GONE
        holder.indexTurnOnsContainer.visibility = View.VISIBLE
        holder.indexTurnOnsContainer.setOnClickListener {

        }
        holder.itemGoChooseTurnOns.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("move", true)
            IntentUtil.startActivity(ChooseTurnOnsActivity::class.java, bundle)
        }
    }

    /**
     * 展示定位权限请求卡片
     */
    private fun requestLocation(position: Int, holder: ViewHolder) {
        showLocation=false
        BaseConfig.getInstance.setBoolean(SpName.userCard + position, false)
        holder.userContainer.visibility = View.GONE
        holder.indexTurnOnsContainer.visibility = View.GONE
        holder.indexLocationContainer.visibility = View.VISIBLE

        holder.indexLocationContainer.setOnClickListener {

        }

        val channel = BaseConfig.getInstance.getString(SpName.channel, "")
        val drawableId = if (channel != "" && JsonUtils.isJSON(channel)) {
            val jsonObject = JSONObject(channel)
            if (jsonObject.has("af_status")) {
                val afStatus = jsonObject.getString("af_status")
                if (!afStatus.equals("Organic", true)) {
                    R.mipmap.shape_location
                } else {
                    R.mipmap.shape_location_no_adid
                }
            } else {
                R.mipmap.shape_location_no_adid
            }
        } else {
            R.mipmap.shape_location_no_adid
        }
        holder.indexLocationContainerBg.setImageResource(drawableId)

        holder.itemTurnOnLocation.setOnClickListener {
            SDEventManager.post(true, EnumEventTag.INDEX_LOCATION_SWIPED.ordinal)
        }
        DotLogUtil.setEventName(DotLogEventName.LOCATION_PERMISSION_GRANT_PAGE_WITH_5_PASS)
            .commit(Activities.get().top)
    }

    /**
     * 展示用户反馈卡片
     */
    private fun feedBackView(holder: ViewHolder) {
//        showFeedback=false

//        holder.feedbackContainer.isVisible=true
//        holder.rgFeedbackList.removeAllViews()
//        val listOf = arrayListOf(
//            "\uD83C\uDF37  Age",
//            "\uD83D\uDC45  Skin color",
//            "\uD83C\uDF51  Body shape",
//            "\uD83C\uDF52  Hair color",
//            "Others"
//        )
//        for (i in 0 until listOf.size) {
//            val radioButton = CheckBox(mActivity)
//            val layoutParams = RadioGroup.LayoutParams(
//                RadioGroup.LayoutParams.MATCH_PARENT,
//                DensityUtil.dp2px(mActivity, 43f)
//            )
//
//            radioButton.setOnClickListener {
//                for (item in 0 until holder.rgFeedbackList.childCount) {
//                    if ((holder.rgFeedbackList.getChildAt(item) as CheckBox).isChecked) {
//                        holder.txtContinuousPassFeedback.isEnabled = true
//                        break
//                    }
//                    holder.txtContinuousPassFeedback.isEnabled = false
//                }
//
//            }
//            layoutParams.topMargin = DensityUtil.dp2px(mActivity, 10f)
//            layoutParams.leftMargin = DensityUtil.dp2px(mActivity, 29f)
//            layoutParams.rightMargin = DensityUtil.dp2px(mActivity, 29f)
//            radioButton.gravity = Gravity.CENTER
//            radioButton.layoutParams = layoutParams
//            radioButton.setBackgroundResource(R.drawable.selector_continuous_check_bg)
//            radioButton.setTextColor(mActivity.resources.getColor(R.color.color_C53FE6))
//
//            radioButton.isChecked = false
//            radioButton.buttonDrawable = null
//            radioButton.text = listOf[i]
//            radioButton.textSize = 18f
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                radioButton.typeface = mActivity.resources.getFont(R.font.intermedium)
//            }
//            holder.rgFeedbackList.addView(radioButton)
//
//        }
//        holder.txtContinuousPassSkip.setOnClickListener {
//            SDEventManager.post(EnumEventTag.INDEX_DISLIKE_SWIPED.ordinal)
//
//        }
//        holder.txtContinuousPassFeedback.setOnClickListener {
//            SDEventManager.post(EnumEventTag.INDEX_RELOAD_DATA.ordinal)
//        }
    }

    override fun getItemCount(): Int {
        return spots.size
    }

    fun setSpots(spots: List<MatchIndexBean>) {
        this.spots = spots
        if (this.spots.isEmpty()){
            notifyDataSetChanged()
        }
    }

    fun getSpots(): List<MatchIndexBean> {
        return spots
    }

    fun setTurnOns(turnOns:Boolean){
        this.turnOns=turnOns
    }
    fun setTurnOnsLimit(turnOnsLimit:Int){
        this.turnOnsLimit=turnOnsLimit
    }

    fun setNewUserFlag(newUserFlag:Boolean){
        this.newUserFlag = newUserFlag
    }

    fun setShowLocation(showLocation:Boolean,topPosition: Int){
        this.showLocation=showLocation
        notifyItemChanged(topPosition)
    }
    fun setShowFeedback(showFeedback:Boolean,topPosition: Int){
//        this.showFeedback=showFeedback
        notifyItemChanged(topPosition)
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        if (holder.userBanner!= null) {
            holder.userBanner.destroy()
        }
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.item_name)
        var online: TextView = view.findViewById(R.id.item_online)
        var positioning: TextView = view.findViewById(R.id.item_positioning)
        var leftClick: View = view.findViewById(R.id.left_click)
        var rightClick: View = view.findViewById(R.id.right_click)
        var bottomClick: View = view.findViewById(R.id.bottom_click)
        var userBanner:  Banner<*, *> = view.findViewById(R.id.item_user_banner)
        var tagCloud: TagCloudView = view.findViewById(R.id.item_tag_cloud)
        var turnOnsContainer: ConstraintLayout = view.findViewById(R.id.turn_ons_container)
        var txtTurnOnsSize: TextView = view.findViewById(R.id.txt_turn_ons_size)
        var indexTurnOnsContainer: ConstraintLayout = view.findViewById(R.id.index_turn_ons_container)
        var itemGoChooseTurnOns: ConstraintLayout = view.findViewById(R.id.item_go_choose_turn_ons)
        var userContainer: FrameLayout = view.findViewById(R.id.user_container)
        var indexLocationContainer: ConstraintLayout = view.findViewById(R.id.index_location_container)
        var itemTurnOnLocation: ConstraintLayout = view.findViewById(R.id.item_turn_on_location)
        var itemMemberLogo: ImageView = view.findViewById(R.id.item_member_logo)
        var indexLocationContainerBg: ImageView = view.findViewById(R.id.index_location_container_bg)
//        var rgFeedbackList: LinearLayout = view.findViewById(R.id.rg_feedback_list)
//        var txtContinuousPassSkip: TextView = view.findViewById(R.id.txt_continuous_pass_skip)
//        var txtContinuousPassFeedback: TextView = view.findViewById(R.id.txt_continuous_pass_feedback)
//        var feedbackContainer: ConstraintLayout = view.findViewById(R.id.feedback_container)
    }

}
