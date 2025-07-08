package com.crush.ui.member.album

import android.content.Intent
import android.graphics.Color
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.crush.R
import io.rong.imkit.event.EnumEventTag
import com.gyf.immersionbar.ImmersionBar
import com.crush.mvp.MVPBaseActivity
import com.sunday.eventbus.SDBaseEvent

/**
 * 私密相册
 */
class PrivateAlbumViewActivity : MVPBaseActivity<PrivateAlbumViewContract.View, PrivateAlbumViewPresenter>(), PrivateAlbumViewContract.View {

    override fun setFullScreen(): Boolean {
        return false
    }

    override fun setTitleBackRId(): Int {
        return R.mipmap.icon_white_back_arrow
    }



    override fun getTitleView(toolbar: RelativeLayout) {
        toolbar.setBackgroundColor(Color.BLACK)
    }

    override fun getTitleTextView(tv: TextView) {
    }

    override fun bindLayout(): Int {
        return R.layout.act_private_albums_view
    }

    override fun initView() {
    }
    override fun onResume() {
        super.onResume()
        ImmersionBar.with(this)
            .statusBarColor("#000000")
            .statusBarDarkFont(true)
            .init()

    }
    override fun onEventMainThread(event: SDBaseEvent) {
        when (EnumEventTag.valueOf(event.tagInt)) {
            EnumEventTag.PRIVATE_ALBUMS_REMOVE->{
            }
            else -> {}
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override val privateAlbumView: ImageView
        get() = findViewById(R.id.private_album_view)
    override val imgDone: TextView
        get() =findViewById(R.id. img_done)
}