package com.crush.ui.member.album

import android.content.Intent
import android.graphics.Color
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.crush.R
import io.rong.imkit.event.EnumEventTag
import com.gyf.immersionbar.ImmersionBar
import com.crush.mvp.MVPBaseActivity
import com.sunday.eventbus.SDBaseEvent

/**
 * 私密相册
 */
class PrivateAlbumActivity : MVPBaseActivity<PrivateAlbumContract.View, PrivateAlbumPresenter>(), PrivateAlbumContract.View {

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
        tv.text=getString(R.string.private_album)
        tv.setTextColor(Color.WHITE)
    }

    override fun bindLayout(): Int {
        return R.layout.act_private_album
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
                mPresenter?.removePhoto(event.data.toString().toInt())
            }
            else -> {}
        }
    }


    override val privateAlbumList: RecyclerView
        get() = findViewById(R.id.private_album_list)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mPresenter?.onActivityResult(requestCode, resultCode, data)
    }
}