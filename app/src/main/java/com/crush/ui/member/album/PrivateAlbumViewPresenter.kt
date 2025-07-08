package com.crush.ui.member.album

import android.app.Activity
import android.os.Bundle
import com.crush.util.GlideUtil
import com.crush.mvp.BasePresenterImpl
import com.luck.picture.lib.basic.PictureSelector


class PrivateAlbumViewPresenter : BasePresenterImpl<PrivateAlbumViewContract.View>(), PrivateAlbumViewContract.Presenter {
    override fun initBundle(bundle: Bundle) {
        mView?.apply {
            val selectorList = PictureSelector.obtainSelectorList(mActivity.intent)
            val path = when {
                selectorList[0].isCompressed -> {
                    selectorList[0].compressPath
                }

                selectorList[0].isCut -> {
                    selectorList[0].cutPath
                }

                else -> {
                    selectorList[0].realPath
                }
            }

            GlideUtil.setImageView(path,privateAlbumView)
            imgDone.setOnClickListener {
                mActivity.setResult(Activity.RESULT_OK, PictureSelector.putIntentResult(selectorList))
                finish()
            }
        }
    }
}