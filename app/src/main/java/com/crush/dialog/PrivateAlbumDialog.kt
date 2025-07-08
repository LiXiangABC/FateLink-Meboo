package com.crush.dialog

import android.Manifest
import android.app.Activity
import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.crush.R
import com.crush.util.PermissionUtils
import com.crush.util.SelectPictureUtil
import razerdp.basepopup.BasePopupWindow

class PrivateAlbumDialog(var ctx:Activity, var isMember:Boolean, var listener: onCallBack ) :  BasePopupWindow(ctx) {
    init {
        setContentView(R.layout.dialog_private_album)
        initView()
    }

    private fun initView() {
        val outsideView = findViewById<View>(R.id.outside_view)
        val dialogClose = findViewById<ImageView>(R.id.dialog_close)
        val ownPrivateAlbum = findViewById<TextView>(R.id.txt_own_private_album)
        val noMemberPrivateAlbumTip = findViewById<TextView>(R.id.no_member_private_album_tip)
        noMemberPrivateAlbumTip.visibility=if (isMember)View.INVISIBLE else View.VISIBLE
        ownPrivateAlbum.setBackgroundResource(if (isMember) R.drawable.shape_solid_black_radius_24  else R.drawable.shape_solid_yellow_radius_24)
        ownPrivateAlbum.text=(if (isMember) ctx.getString(R.string.add_photo)  else ctx.getString(R.string.own_private_album))
        ownPrivateAlbum.setOnClickListener {
            if (isMember){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (!PermissionUtils.lacksPermission(
                            Manifest.permission.READ_MEDIA_IMAGES
                        ) && !PermissionUtils.lacksPermission(
                            Manifest.permission.READ_MEDIA_VIDEO
                        )
                    ) {
                        SelectPictureUtil.selectNoTailor(ctx)
                    } else {
                        PermissionUtils.requestPermission(
                            ctx, 4,
                            {
                                SelectPictureUtil.selectNoTailor(ctx)
                            },
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_MEDIA_VIDEO
                        )
                    }
                } else {
                    if (!PermissionUtils.lacksPermission(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    ) {
                        SelectPictureUtil.selectNoTailor(ctx)
                    } else {
                        PermissionUtils.requestPermission(
                            ctx, 4,
                            {
                                SelectPictureUtil.selectNoTailor(ctx)
                            },
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    }
                }
                dismiss()
            }else{
                listener.onCallback()
                dismiss()
            }
        }
        dialogClose.setOnClickListener {
            dismiss()
        }
        outsideView.setOnClickListener {
            dismiss()
        }

        setOutSideDismiss(true)
    }
    interface onCallBack{
       fun onCallback()
    }
}