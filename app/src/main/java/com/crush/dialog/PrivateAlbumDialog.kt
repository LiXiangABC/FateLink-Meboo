package com.crush.dialog

import android.Manifest
import android.app.Activity
import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.crush.R
import com.crush.util.PermissionUtil
import com.crush.util.SelectPictureUtil
import io.rong.imkit.activity.Activities
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
                    if (!PermissionUtil.checkPermission(ctx,Manifest.permission.READ_MEDIA_IMAGES) && !PermissionUtil.checkPermission(ctx,Manifest.permission.READ_MEDIA_VIDEO)
                    ) {
                        SelectPictureUtil.selectNoTailor(ctx)
                    } else {
                        Activities.get().top?.let { it1 ->
                            PermissionUtil.requestPermissionCallBack(
                                Manifest.permission.READ_MEDIA_IMAGES,
                                Manifest.permission.READ_MEDIA_VIDEO,
                                activity = it1, type =  4)
                            {
                                if (it) {
                                    SelectPictureUtil.selectNoTailor(ctx)
                                }
                            }
                        }
                    }
                } else {
                    if (!PermissionUtil.checkPermission(ctx,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    ) {
                        SelectPictureUtil.selectNoTailor(ctx)
                    } else {
                        Activities.get().top?.let { it1 ->
                            PermissionUtil.requestPermissionCallBack(Manifest.permission.WRITE_EXTERNAL_STORAGE, activity = it1, type = 4)
                            {
                                SelectPictureUtil.selectNoTailor(ctx)
                            }
                        }
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