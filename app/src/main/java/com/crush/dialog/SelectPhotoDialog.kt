package com.crush.dialog

import android.Manifest
import android.app.Activity
import android.os.Build
import android.widget.TextView
import com.crush.R
import com.crush.dot.DotLogEventName
import com.crush.dot.DotLogUtil
import com.crush.util.PermissionUtil
import com.crush.util.SelectPictureUtil
import io.rong.imkit.activity.Activities
import razerdp.basepopup.BasePopupWindow

class SelectPhotoDialog(var ctx: Activity) :  BasePopupWindow(ctx) {
    init {
        setContentView(R.layout.dialog_select_photo)
        initView()
    }

    private fun initView() {
        val dialogCancel = findViewById<TextView>(R.id.dialog_cancel)
        dialogCancel.setOnClickListener {
            dismiss()
        }
        val dialogAlbums = findViewById<TextView>(R.id.dialog_albums)
        val dialogCamera = findViewById<TextView>(R.id.dialog_camera)
        dialogCamera.setOnClickListener {
            Activities.get().top?.let {activity->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (!PermissionUtil.checkPermission(activity,Manifest.permission.CAMERA)) {
                        SelectPictureUtil.selectCamera(activity)
                    } else {
                        PermissionUtil.requestPermissionCallBack(Manifest.permission.CAMERA, activity = activity, type = 1) {
                            if (it){
                                SelectPictureUtil.selectCamera(activity)
                            }
                        }
                    }
                } else {
                    if (!PermissionUtil.checkPermission(activity,Manifest.permission.WRITE_EXTERNAL_STORAGE) && !PermissionUtil.checkPermission(activity, Manifest.permission.CAMERA)) {
                        SelectPictureUtil.selectCamera(activity)
                    } else {
                        PermissionUtil.requestPermissionCallBack(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,activity = activity, type = 1) {
                            if (it) {
                                SelectPictureUtil.selectCamera(activity)
                            }
                        }
                    }
                }
            }
//            HttpRequest.commonNotify(605,"")
            DotLogUtil.setEventName(DotLogEventName.USER_PROFILE_CAMERA_IMAGE_ON_CLICK).commit(ctx)

            dismiss()
        }
        dialogAlbums.setOnClickListener {
            Activities.get().top?.let {activity->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (!PermissionUtil.checkPermission(activity,Manifest.permission.READ_MEDIA_IMAGES) && !PermissionUtil.checkPermission(activity,Manifest.permission.READ_MEDIA_VIDEO)) {
                        SelectPictureUtil.selectPhoto(activity)
                    } else {
                        PermissionUtil.requestPermissionCallBack(Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_MEDIA_VIDEO, activity = activity, type = 4) {
                            if (it) {
                                SelectPictureUtil.selectPhoto(activity)
                            }
                        }
                    }
                } else {
                    if (!PermissionUtil.checkPermission(activity,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        SelectPictureUtil.selectPhoto(activity)
                    } else {
                        PermissionUtil.requestPermissionCallBack(Manifest.permission.WRITE_EXTERNAL_STORAGE, activity = activity, type = 4) {
                            if (it) {
                                SelectPictureUtil.selectPhoto(activity)
                            }
                        }
                    }
                }
            }

//            HttpRequest.commonNotify(604,"")
            DotLogUtil.setEventName(DotLogEventName.USER_PROFILE_UPLOAD_IMAGE_ON_CLICK).commit(ctx)

            dismiss()
        }
        setOutSideDismiss(true)
    }
}