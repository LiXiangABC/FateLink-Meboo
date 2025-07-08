package com.crush.ui.look

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import com.crush.Constant
import com.crush.R
import com.crush.entity.BaseEntity
import io.rong.imkit.event.EnumEventTag
import com.gyf.immersionbar.ImmersionBar
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.crush.mvp.MVPBaseActivity
import com.custom.base.type.StatusBarType
import com.sunday.eventbus.SDEventManager


/**
 * 作者：
 * 时间：2021-02-07
 * 描述：查看图片
 */

class LookImagesActivity : MVPBaseActivity<LookImagesContract.View, LookImagesPresenter>(),
    LookImagesContract.View {

    override fun onResume() {
        super.onResume()
        ImmersionBar.with(this)
            .statusBarColor("#000000")
            .statusBarDarkFont(true)
            .init()

    }

    override fun setTitleBackRId(): Int {
        return R.mipmap.icon_white_back_arrow
    }

    override fun setTitleRightImageView(): List<Int> {
        return listOf(R.mipmap.icon_delete_photo)
    }

    override fun setRightTitleBgRes(): Int {
        return R.drawable.shape_solid_black_radius_4
    }

    override fun onTitleRightClickListenerBundle(index: Int, view: View, bundle: Bundle) {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.user_user_albums_remove_url)
                requestBody.add("albumCode",bundle.getString("albumCode",""))
                requestBody.add("imageCode",bundle.getString("imageCode",""))
                requestBody.add("type",3)
            }
        }, object : SDOkHttpResoutCallBack<BaseEntity>() {
            override fun onSuccess(entity: BaseEntity) {
                SDEventManager.post(bundle.getInt("position"),EnumEventTag.PRIVATE_ALBUMS_REMOVE.ordinal)
                finish()
            }

        })

    }

    override fun getTitleView(toolbar: RelativeLayout) {
        toolbar.setBackgroundColor(Color.BLACK)
    }

    override fun bindLayout(): Int {
        return R.layout.act_lookimages
    }

    override fun setFullScreen(): Boolean {
        return false
    }

    override fun setStatusBarType(): StatusBarType {
        return StatusBarType.Light
    }

    override val viewPager: PhotoViewPager
        get() =  findViewById(R.id.actLookImageBanner)

}

