package com.crush.dialog

import android.Manifest
import android.app.Activity
import android.os.Build
import android.widget.TextView
import com.crush.R
import com.crush.dot.DotLogEventName
import com.crush.dot.DotLogUtil
import com.crush.util.PermissionUtils
import com.crush.util.SelectPictureUtil
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (!PermissionUtils.lacksPermission(Manifest.permission.CAMERA)) {
                    SelectPictureUtil.selectCamera(ctx)
                } else {
                    PermissionUtils.requestPermission(ctx,1, {
                        SelectPictureUtil.selectCamera(ctx) },
                        Manifest.permission.CAMERA
                    )
                }
            } else {
                if (!PermissionUtils.lacksPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) && !PermissionUtils.lacksPermission( Manifest.permission.CAMERA)) {
                    SelectPictureUtil.selectCamera(ctx)
                } else {
                    PermissionUtils.requestPermission(ctx, 1, {
                        SelectPictureUtil.selectCamera(ctx) },
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                    )
                }
            }
//            HttpRequest.commonNotify(605,"")
            DotLogUtil.setEventName(DotLogEventName.USER_PROFILE_CAMERA_IMAGE_ON_CLICK).commit(ctx)

            dismiss()
        }
        dialogAlbums.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (!PermissionUtils.lacksPermission(Manifest.permission.READ_MEDIA_IMAGES) && !PermissionUtils.lacksPermission(Manifest.permission.READ_MEDIA_VIDEO)) {
                     SelectPictureUtil.selectPhoto(ctx)
                } else {
                    PermissionUtils.requestPermission(ctx, 4,
                        {
                             SelectPictureUtil.selectPhoto(ctx)
                        },
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO
                    )
                }
            } else {
//                if (PermissionChecker.isCheckReadStorage(
//                        SelectMimeType.ofImage(),
//                        ctx
//                    )
//                ) {
//                    SelectPictureUtil.selectP(ctx)
//                } else {
//                    val readPermissionArray = PermissionConfig.getReadPermissionArray(
//                        ctx,
//                        SelectMimeType.ofImage()
//                    )
//                    ActivityCompat.requestPermissions(ctx, readPermissionArray, 101)
//                }
                if (!PermissionUtils.lacksPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                     SelectPictureUtil.selectPhoto(ctx)
                } else {
                    PermissionUtils.requestPermission(ctx, 4,
                        {
                             SelectPictureUtil.selectPhoto(ctx)
                        },
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                }
            }
//            HttpRequest.commonNotify(604,"")
            DotLogUtil.setEventName(DotLogEventName.USER_PROFILE_UPLOAD_IMAGE_ON_CLICK).commit(ctx)

            dismiss()
        }
        setOutSideDismiss(true)
    }
}