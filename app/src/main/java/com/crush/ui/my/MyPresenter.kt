package com.crush.ui.my

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.crush.Constant
import com.crush.R
import com.crush.dialog.GiftPackBuySuccessDialog
import io.rong.imkit.dialog.MemberBuyDialog
import com.crush.dialog.PrivateAlbumDialog
import com.crush.entity.BaseIntEntity
import io.rong.imkit.entity.BuyMemberPageEntity
import io.rong.imkit.entity.MemberSubscribeEntity
import com.crush.entity.PrivateAlbumEntity
import com.crush.entity.QueryBenefitsEntity
import com.crush.entity.UserProfileEntity
import com.crush.ui.member.MemberSubscriptionActivity
import com.crush.ui.member.album.PrivateAlbumActivity
import com.crush.ui.member.album.PrivateAlbumViewActivity
import com.crush.util.CollectionUtils
import com.crush.util.DensityUtil
import com.crush.util.GlideUtil
import com.custom.base.config.BaseConfig
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.crush.mvp.BasePresenterImpl
import com.crush.util.IntentUtil
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import io.rong.imkit.RongIM
import io.rong.imkit.SpName
import io.rong.imkit.activity.Activities
import io.rong.imkit.dialog.MemberUnitaryBuyNewDialog
import io.rong.imkit.entity.OrderCreateEntity
import io.rong.imkit.event.FirebaseEventTag
import io.rong.imkit.pay.EmptySuccessCallBack
import io.rong.imkit.pay.PayUtils
import io.rong.imkit.utils.FirebaseEventUtils
import io.rong.imkit.utils.RongUtils
import io.rong.imlib.model.UserInfo
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


/**
 * 作者：
 * 时间：
 * 描述：我的
 */
class MyPresenter : BasePresenterImpl<MyContract.View>(), MyContract.Presenter {
    val scope = MainScope()
    var entity: UserProfileEntity?=null

    fun setClick(){
        mView?.apply {
            privateAlbumContainer.setOnClickListener {
                entity?.apply {
                    if (this.data.privateAlbums != null && this.data.isMember) {
                        IntentUtil.startActivity(PrivateAlbumActivity::class.java)
                    } else {
                        Activities.get().top?.let { it1 ->
                            PrivateAlbumDialog(
                                it1,
                                this.data.isMember,
                                object : PrivateAlbumDialog.onCallBack {
                                    override fun onCallback() {
                                        Activities.get().top?.let { it2 ->
                                            MemberBuyDialog(
                                                it2,
                                                5,
                                                object : MemberBuyDialog.ChangeMembershipListener {
                                                    override fun onListener(bean: MemberSubscribeEntity.Data.ProductDescriptions) {
                                                        OkHttpManager.instance.requestInterface(
                                                            object : OkHttpFromBoy {
                                                                override fun addBody(requestBody: OkHttpBodyEntity) {
                                                                    requestBody.setPost(Constant.user_create_order_url)
                                                                    requestBody.add(
                                                                        "productCode",
                                                                        bean.productCode
                                                                    )
                                                                    requestBody.add(
                                                                        "productCategory",
                                                                        1
                                                                    )
                                                                }

                                                            },
                                                            object :
                                                                SDOkHttpResoutCallBack<OrderCreateEntity>() {
                                                                override fun onSuccess(entity: OrderCreateEntity) {
                                                                    Activities.get().top?.let { it3 ->
                                                                        PayUtils.instance.start(
                                                                            entity,
                                                                            it3,
                                                                            object :
                                                                                EmptySuccessCallBack {
                                                                                override fun OnSuccessListener() {
                                                                                    getData()
                                                                                    BaseConfig.getInstance.setBoolean(
                                                                                        SpName.isMember,
                                                                                        true
                                                                                    )
                                                                                }

                                                                            })
                                                                    }
                                                                }
                                                            })
                                                    }

                                                    override fun closeListener(refreshTime: Long) {

                                                    }

                                                })
                                        }
                                    }

                                }).showPopupWindow()
                        }
                    }
                }

            }
//            //点击购买礼包
//            conMyGiftPackBox.setOnClickListener {
//                NewBieGiftPackDialog.Builder(mActivity)
//                    .setCallback(object : NewBieGiftPackDialog.Callback {
//                        override fun onBackClick() {
//
//                        }
//                    },false).show()
//            }
//            //点击口令兑换
//            conMyPwdExchangeBox.setOnClickListener {
//                startActivity(PasswordExchangeActivity::class.java)
//            }
            boostFlashChat.setOnClickListener {
                FirebaseEventUtils.logEvent(FirebaseEventTag.Me_Flashchat_Buy.name)
                Activities.get().top?.let { it1 ->
                    MemberUnitaryBuyNewDialog(
                        it1,
                        2,
                        object : MemberUnitaryBuyNewDialog.MemberUnitaryBuyListener {
                            override fun onListener(bean: BuyMemberPageEntity.ProductExt) {
                                OkHttpManager.instance.requestInterface(object :
                                    OkHttpFromBoy {
                                    override fun addBody(requestBody: OkHttpBodyEntity) {
                                        requestBody.setPost(Constant.user_create_order_url)
                                        requestBody.add("productCode", bean.productCode)
                                        requestBody.add("productCategory", 2)
                                    }

                                }, object : SDOkHttpResoutCallBack<OrderCreateEntity>() {
                                    override fun onSuccess(entity: OrderCreateEntity) {
                                        entity.data.benefitNum = bean.benefitNum
                                        entity.data.productCategory = 2
                                        Activities.get().top?.let { it2 ->
                                            PayUtils.instance.start(
                                                entity,
                                                it2,
                                                object : EmptySuccessCallBack {
                                                    override fun OnSuccessListener() {
                                                        FirebaseEventUtils.logEvent(
                                                            FirebaseEventTag.Me_Flashchat_Buysuccess.name
                                                        )
                                                        getData()
                                                    }

                                                })
                                        }
                                    }
                                })

                            }

                        })
                }
//                        } else {
//                            FirebaseEventUtils.logEvent(FirebaseEventTag.Me_FC_Sub.name)
//
//                            MemberBuyDialog(
//                                mActivity,
//                                2,
//                                object : MemberBuyDialog.ChangeMembershipListener {
//                                    override fun onListener(bean: MemberSubscribeEntity.Data.ProductDescriptions) {
//                                        OkHttpManager.instance.requestInterface(
//                                            object : OkHttpFromBoy {
//                                                override fun addBody(requestBody: OkHttpBodyEntity) {
//                                                    requestBody.setPost(Constant.user_create_order_url)
//                                                    requestBody.add(
//                                                        "productCode",
//                                                        bean.productCode
//                                                    )
//                                                    requestBody.add(
//                                                        "productCategory",
//                                                        1
//                                                    )
//                                                }
//
//                                            },
//                                            object :
//                                                SDOkHttpResoutCallBack<OrderCreateEntity>() {
//                                                override fun onSuccess(entity: OrderCreateEntity) {
//                                                    PayUtils.instance.start(
//                                                        entity,
//                                                        mActivity,
//                                                        object :
//                                                            EmptySuccessCallBack {
//                                                            override fun OnSuccessListener() {
//                                                                getData()
//                                                                BaseConfig.getInstance.setBoolean(
//                                                                    SpName.isMember,
//                                                                    true
//                                                                )
//                                                                FirebaseEventUtils.logEvent(FirebaseEventTag.Me_FC_Subsuccess.name)
//
//                                                            }
//
//                                                        })
//                                                }
//                                            })
//                                    }
//
//                                    override fun closeListener(refreshTime: Long) {
//
//                                    }
//                                })
//                        }

            }
            boostPrivatePhoto.setOnClickListener {
                entity?.apply {
                    if (this.data.isMember) {
                        FirebaseEventUtils.logEvent(FirebaseEventTag.Me_PP_Buy.name)

                        Activities.get().top?.let { it1 ->
                            MemberUnitaryBuyNewDialog(
                                it1,
                                3,
                                object : MemberUnitaryBuyNewDialog.MemberUnitaryBuyListener {
                                    override fun onListener(bean: BuyMemberPageEntity.ProductExt) {
                                        OkHttpManager.instance.requestInterface(object :
                                            OkHttpFromBoy {
                                            override fun addBody(requestBody: OkHttpBodyEntity) {
                                                requestBody.setPost(Constant.user_create_order_url)
                                                requestBody.add("productCode", bean.productCode)
                                                requestBody.add("productCategory", 3)
                                            }

                                        }, object : SDOkHttpResoutCallBack<OrderCreateEntity>() {
                                            override fun onSuccess(entity: OrderCreateEntity) {
                                                entity.data.benefitNum = bean.benefitNum
                                                entity.data.productCategory = 3
                                                Activities.get().top?.let { it2 ->
                                                    PayUtils.instance.start(
                                                        entity,
                                                        it2,
                                                        object : EmptySuccessCallBack {
                                                            override fun OnSuccessListener() {
                                                                FirebaseEventUtils.logEvent(
                                                                    FirebaseEventTag.Me_PP_Buysuccess.name
                                                                )
                                                                getData()
                                                            }

                                                        })
                                                }
                                            }
                                        })
                                    }

                                })
                        }
                    } else {
                        FirebaseEventUtils.logEvent(FirebaseEventTag.Me_PP_Sub.name)
                        Activities.get().top?.let { it1 ->
                            MemberBuyDialog(
                                it1,
                                3,
                                object : MemberBuyDialog.ChangeMembershipListener {
                                    override fun onListener(bean: MemberSubscribeEntity.Data.ProductDescriptions) {
                                        OkHttpManager.instance.requestInterface(
                                            object : OkHttpFromBoy {
                                                override fun addBody(requestBody: OkHttpBodyEntity) {
                                                    requestBody.setPost(Constant.user_create_order_url)
                                                    requestBody.add(
                                                        "productCode",
                                                        bean.productCode
                                                    )
                                                    requestBody.add(
                                                        "productCategory",
                                                        1
                                                    )
                                                }

                                            },
                                            object :
                                                SDOkHttpResoutCallBack<OrderCreateEntity>() {
                                                override fun onSuccess(entity: OrderCreateEntity) {
                                                    Activities.get().top?.let { it2 ->
                                                        PayUtils.instance.start(
                                                            entity,
                                                            it2,
                                                            object :
                                                                EmptySuccessCallBack {
                                                                override fun OnSuccessListener() {
                                                                    getData()
                                                                    BaseConfig.getInstance.setBoolean(
                                                                        SpName.isMember,
                                                                        true
                                                                    )
                                                                    FirebaseEventUtils.logEvent(
                                                                        FirebaseEventTag.Me_PP_Subsuccess.name
                                                                    )

                                                                }

                                                            })
                                                    }
                                                }
                                            })
                                    }

                                    override fun closeListener(refreshTime: Long) {

                                    }

                                })
                        }
                    }
                }

            }
            boostPrivateVideo.setOnClickListener {
                entity?.apply {
                    if (this.data.isMember) {
                        FirebaseEventUtils.logEvent(FirebaseEventTag.Me_PV_Buy.name)

                        Activities.get().top?.let { it1 ->
                            MemberUnitaryBuyNewDialog(
                                it1,
                                4,
                                object : MemberUnitaryBuyNewDialog.MemberUnitaryBuyListener {
                                    override fun onListener(bean: BuyMemberPageEntity.ProductExt) {
                                        OkHttpManager.instance.requestInterface(object :
                                            OkHttpFromBoy {
                                            override fun addBody(requestBody: OkHttpBodyEntity) {
                                                requestBody.setPost(Constant.user_create_order_url)
                                                requestBody.add("productCode", bean.productCode)
                                                requestBody.add("productCategory", 4)
                                            }

                                        }, object : SDOkHttpResoutCallBack<OrderCreateEntity>() {
                                            override fun onSuccess(entity: OrderCreateEntity) {
                                                entity.data.benefitNum = bean.benefitNum
                                                entity.data.productCategory = 4
                                                Activities.get().top?.let { it2 ->
                                                    PayUtils.instance.start(
                                                        entity,
                                                        it2,
                                                        object : EmptySuccessCallBack {
                                                            override fun OnSuccessListener() {
                                                                FirebaseEventUtils.logEvent(
                                                                    FirebaseEventTag.Me_PV_Buysuccess.name
                                                                )
                                                                getData()
                                                            }

                                                        })
                                                }
                                            }
                                        })
                                    }

                                })
                        }
                    } else {
                        FirebaseEventUtils.logEvent(FirebaseEventTag.Me_PV_Sub.name)

                        Activities.get().top?.let { it1 ->
                            MemberBuyDialog(
                                it1,
                                4,
                                object : MemberBuyDialog.ChangeMembershipListener {
                                    override fun onListener(bean: MemberSubscribeEntity.Data.ProductDescriptions) {
                                        OkHttpManager.instance.requestInterface(
                                            object : OkHttpFromBoy {
                                                override fun addBody(requestBody: OkHttpBodyEntity) {
                                                    requestBody.setPost(Constant.user_create_order_url)
                                                    requestBody.add(
                                                        "productCode",
                                                        bean.productCode
                                                    )
                                                    requestBody.add(
                                                        "productCategory",
                                                        1
                                                    )
                                                }

                                            },
                                            object :
                                                SDOkHttpResoutCallBack<OrderCreateEntity>() {
                                                override fun onSuccess(entity: OrderCreateEntity) {
                                                    Activities.get().top?.let { it2 ->
                                                        PayUtils.instance.start(
                                                            entity,
                                                            it2,
                                                            object :
                                                                EmptySuccessCallBack {
                                                                override fun OnSuccessListener() {
                                                                    getData()
                                                                    BaseConfig.getInstance.setBoolean(
                                                                        SpName.isMember,
                                                                        true
                                                                    )
                                                                    FirebaseEventUtils.logEvent(
                                                                        FirebaseEventTag.Me_PV_Subsuccess.name
                                                                    )

                                                                }

                                                            })
                                                    }
                                                }
                                            })
                                    }

                                    override fun closeListener(refreshTime: Long) {

                                    }

                                })
                        }
                    }
                }
            }
            clMemberContainer.setOnClickListener {
                entity?.apply {
                    val bundle = Bundle()
                    bundle.putBoolean("isMember",this.data.isMember)
                    startActivity(MemberSubscriptionActivity::class.java, bundle)
                }
            }


        }
    }

    private fun showGiftPackBuySuccess(){
        Activities.get().top?.let {
            GiftPackBuySuccessDialog.Builder(it)
                .setCallback(object : GiftPackBuySuccessDialog.Callback {
                    override fun onBackClick() {}
                }).show()
        }
    }


    fun getData() {
        mView?.apply {
            OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                override fun addBody(requestBody: OkHttpBodyEntity) {
                    requestBody.setPost(Constant.user_info_url)
                }
            }, object : SDOkHttpResoutCallBack<UserProfileEntity>(false) {
                override fun onSuccess(entity: UserProfileEntity) {
                    this@MyPresenter.entity=entity
                    RongIM.getInstance().refreshUserInfoCache(
                        UserInfo(
                            entity.data.userCode,
                            entity.data.nickName,
                            Uri.parse(entity.data.avatarUrl)
                        )
                    )
                    privateAlbumContainer.visibility = if (BaseConfig.getInstance.getBoolean(
                            SpName.privatePhotoShowUnableFlag,
                            false
                        ) || BaseConfig.getInstance.getInt(SpName.trafficSource,0)==1
                    ) View.GONE else View.VISIBLE

                    specialMemberTip.text=entity.data.topTag?:""
                    boostPrivatePhoto.visibility = if (BaseConfig.getInstance.getBoolean(
                            SpName.privatePhotoShowUnableFlag,
                            false
                        )|| BaseConfig.getInstance.getInt(SpName.trafficSource,0)==1
                    ) View.GONE else View.VISIBLE
                    boostPrivateVideo.visibility = if (BaseConfig.getInstance.getBoolean(
                            SpName.privatePhotoShowUnableFlag,
                            false
                        )|| BaseConfig.getInstance.getInt(SpName.trafficSource,0)==1
                    ) View.GONE else View.VISIBLE

                    imgMyAvatar.borderWidth = if (entity.data.isMember)DensityUtil.dp2pxF(Activities.get().top,3f) else 0f
                    scope.launch {
                        GlideUtil.setImageView(entity.data.avatarUrl, imgMyAvatar)
                        BaseConfig.getInstance.setString(SpName.userCode, entity.data.userCode)
                        if (CollectionUtils.isNotEmpty(entity.data.turnOnsList)) {
                            for (i in 0 until entity.data.turnOnsList.size) {
                                Activities.get().top?.let {
                                    if (it.isDestroyed||it.isFinishing) {
                                        return@let
                                    }
                                    Glide.with(it)
                                        .load(entity.data.turnOnsList[i].imageUrl)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .preload()
                                }

                            }
                        }
                        if (entity.data.isMember) {
                            getPrivate()
                        }
                    }


                    txtUserName.text = entity.data.nickName
                    txtUserName.visibility = View.VISIBLE

                    txtGetPremium?.visibility = if (entity.data.isMember) View.GONE else View.VISIBLE
                    txtMemberInfo.visibility = if (entity.data.isMember) View.VISIBLE else View.GONE
                    imgMyCrown.visibility = if (entity.data.isMember) View.VISIBLE else View.GONE
                    if (entity.data.isMember) {
                        txtMemberInfo.text = if (entity.data.autoRenew == 1) Activities.get().top?.getString(R.string.auto_subscription) else "Until: ${entity.data.expiryDate}"
                    }
                }
            })
            queryBenefits()


        }
    }

    fun getPrivate() {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.user_user_albums_url)
                requestBody.add("type", 3)
            }

        }, object : SDOkHttpResoutCallBack<PrivateAlbumEntity>(false) {
            override fun onSuccess(entity: PrivateAlbumEntity) {
                for (i in 0 until entity.data.images.size) {
                    Activities.get().top?.let {
                        Glide.with(it)
                            .load(entity.data.images[i].imageUrl)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .preload()
                    }
                }
            }

        })
    }

    fun getILikeCount() {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setGet(Constant.user_like_count_url)
            }

        }, object : SDOkHttpResoutCallBack<BaseIntEntity>() {
            override fun onSuccess(entity: BaseIntEntity) {
                mView?.apply {
                    txtMeILikeSize.text = "${entity.data}"
                }
            }

        })
    }

    fun queryBenefits() {
        mView?.apply {
            OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                override fun addBody(requestBody: OkHttpBodyEntity) {
                    requestBody.setPost(Constant.user_member_query_benefits_url)
                }
            }, object : SDOkHttpResoutCallBack<QueryBenefitsEntity>(false) {
                override fun onSuccess(entity: QueryBenefitsEntity) {
                    if (entity.data != null) {
                        if (entity.data.size > 2) {
                            txtFlashChat.text = "${entity.data[0].maxUses} Flash Chat"
                            txtPrivatePhotos.text = "${entity.data[1].maxUses} Private Photos"
                            txtPrivateVideos.text = "${entity.data[2].maxUses} Private Videos"
                        }
                    }
                }
            })
        }

    }

    fun getMemberData() {
        mView?.apply {
            OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                override fun addBody(requestBody: OkHttpBodyEntity) {
                    requestBody.setPost(Constant.user_member_product_subscription_url)
                }

            }, object : SDOkHttpResoutCallBack<MemberSubscribeEntity>(false) {
                override fun onSuccess(entity: MemberSubscribeEntity) {
                    txtMemberTitle.text = entity.data.memberDescription.tip
                    txtMemberContent.text = entity.data.memberDescription.content
                }

            })
        }

    }


    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    val selectList = PictureSelector.obtainSelectorList(data)
                    val intent = Intent(Activities.get().top, PrivateAlbumViewActivity::class.java)
                    intent.putParcelableArrayListExtra(
                        PictureConfig.EXTRA_RESULT_SELECTION,
                        selectList
                    )
                    Activities.get().top?.startActivityForResult(intent, PictureConfig.CONFIRM_CHOOSE)
                }

                PictureConfig.CONFIRM_CHOOSE -> {
                    val bundle = Bundle()
                    val selectList = PictureSelector.obtainSelectorList(data)
                    bundle.putParcelableArrayList("selectList", selectList)
                    IntentUtil.startActivity(PrivateAlbumActivity::class.java, bundle)
                }
            }
        }
    }

}