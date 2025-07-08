package com.crush.ui.my.profile.info.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.crush.Constant
import com.crush.adapter.PhotoSelectAdapter
import com.crush.bean.UserPhotoBean
import com.crush.dialog.SelectPhotoDialog
import com.crush.entity.UserPhotoEntity
import com.crush.entity.UserProfileEntity
import io.rong.imkit.event.EnumEventTag
import com.crush.util.UploadPhoto
import com.custom.base.config.BaseConfig
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.crush.mvp.BasePresenterImpl
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.entity.LocalMedia
import com.sunday.eventbus.SDEventManager
import io.rong.imkit.SpName
import io.rong.imkit.activity.Activities


class PhotoPresenter : BasePresenterImpl<PhotoContract.View>(), PhotoContract.Presenter {
    private lateinit var photoSelectAdapter: PhotoSelectAdapter
    private var photoSelectPosition: Int = 0 //选择照片
    private var userImages = arrayListOf<UserPhotoBean>()
    override fun initBundle(bundle: Bundle) {
        mView?.apply {
            val data = bundle.getSerializable("entity") as UserProfileEntity.Data
            data?.apply {
                Activities.get().top?.let {
                    for (i in 0 until imagesV2.size) {
                        userImages.add(UserPhotoBean(imagesV2[i].imageUrl, imagesV2[i].imageCode,imagesV2[i].imageUrl,false))
                    }
                    if (userImages.size < 6) {
                        userImages.add(UserPhotoBean("", "","", false))
                    }
                    photoList?.layoutManager = GridLayoutManager(it, 2)
                    // 解决调用 notifyItemChanged 闪烁问题,取消默认动画
                    (photoList?.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
                    photoSelectAdapter =
                        PhotoSelectAdapter(userImages, it, object : PhotoSelectAdapter.OnCallBack {
                            override fun selectPhoto(bean: UserPhotoBean, position: Int) {
                                photoSelectPosition = position
                                SelectPhotoDialog(it).showPopupWindow()
                            }

                            override fun delPhoto(bean: UserPhotoBean, position: Int) {
                                if (position == 0) {
                                    showToast("Cover cannot be deleted!")
                                    return
                                }
                                removePhoto(position)
                                saveProfileInfo(bean.imageCode,bean.imageUrl,3,0)// 删除照片


                            }

                        })
                    photoList?.adapter = photoSelectAdapter
                }

            }

        }
    }

    private fun addImage(addPosition: Int, selectList: java.util.ArrayList<LocalMedia>?) {
        mView?.apply {
            selectList?.forEach {
                val path = when {
                    it.isCompressed -> {
                        it.compressPath
                    }

                    it.isCut -> {
                        it.cutPath
                    }

                    else -> {
                        it.realPath
                    }
                }
                if (path.contains(".mp4", true) || it.mimeType.contains(
                        ".3gp",
                        true
                    ) || it.mimeType.contains(".mov", true)
                ) {
                    showToast("pls upload photo.")
                    return
                }
                var opType:Int
                if (addPosition == userImages.size){
                    return
                }
                if (userImages[addPosition].imageUrl == "") {
                    opType=1
                    val imagesBean = UserPhotoBean("", "","", true)
                    addPhoto(imagesBean)
                } else {
                    opType=2
                    userImages[addPosition].loading = true
                    userImages[addPosition].imageUrl = ""
                    photoSelectAdapter.notifyItemChanged(addPosition)
                }
                Activities.get().top?.let { it1 ->
                    UploadPhoto.uploadFileNewPhoto(it1,path, if (opType==1)"" else userImages[photoSelectPosition].imageCode, object : UploadPhoto.OnLister {
                        override fun onSuccess(successPath: String,imageCode:String) {
                            if(opType==2){
                                var selectPosition = 0
                                for (i in 0 until userImages.size) {
                                    if (userImages[i].imageCode==imageCode) {
                                        selectPosition = i
                                        break
                                    }
                                }
                                userImages[selectPosition].imageUrl = successPath
                                saveProfileInfo(userImages[selectPosition].imageCode,successPath,opType,if (selectPosition == 0)1 else 0)
                            }else {
                                var lastPosition = 0
                                for (i in 0 until userImages.size) {
                                    if (userImages[i].imageUrl == "") {
                                        lastPosition = i
                                        break
                                    }
                                }
                                userImages[lastPosition].imageUrl = successPath
            //                        userImages[lastPosition].loading = false
                                if (photoSelectPosition == userImages.size){
                                    photoSelectPosition -= 1
                                }
                                saveProfileInfo(
                                    userImages[photoSelectPosition].imageCode,
                                    successPath,
                                    opType,
                                    if (photoSelectPosition == 0) 1 else 0
                                )//
                            }
                        }

                        override fun fail() {
                            var lastPosition = 0
                            for (i in 0 until userImages.size) {
                                if (userImages[i].loading) {
                                    lastPosition = i
                                }
                            }
                            if (userImages[lastPosition].imageUrl != "") {
                                userImages[lastPosition].loading = false
                            } else {
                                userImages.removeAt(lastPosition)
                            }
                            photoSelectAdapter.notifyDataSetChanged()
                            showToast("upload error,pls try again")
                        }

                    })
                }
            }
        }
    }

    fun saveProfileInfo(
        imageCode: String,
        imageUrl: String,
        opType:Int,
        avatarFlag:Int
    ) {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.user_changeImage_url)
                requestBody.add("opType", opType)//1-新增 2-修改 3-删除
                requestBody.add("imageCode", imageCode)
                requestBody.add("imageUrl", imageUrl)
                requestBody.add("avatarFlag", avatarFlag)//1-是 0-否
                requestBody.add("type", 1)
            }
        }, object : SDOkHttpResoutCallBack<UserPhotoEntity>() {
            override fun onSuccess(entity: UserPhotoEntity) {
                if (opType==1) {
                    mView?.apply {
                        if (entity.data != null) {
                            var lastPosition = 0
                            for (i in 0 until userImages.size) {
                                if (userImages[i].imageLoadUrl == "") {
                                    lastPosition = i
                                    break
                                }
                            }
                            userImages[lastPosition].imageLoadUrl = entity.data.imageUrl
                            userImages[lastPosition].imageCode = entity.data.imageCode
                            userImages[lastPosition].loading = false
                            photoSelectAdapter.notifyItemChanged(lastPosition)
                        }
                    }
                }else if (opType==2){
                    if (entity.data != null) {
                        var lastPosition = 0
                        for (i in 0 until userImages.size) {
                            if (userImages[i].imageCode == entity.data.imageCode) {
                                lastPosition = i
                                break
                            }
                        }
                        userImages[lastPosition].imageLoadUrl = entity.data.imageUrl
                        userImages[lastPosition].imageCode = entity.data.imageCode
                        userImages[lastPosition].loading = false
                        photoSelectAdapter.notifyItemChanged(lastPosition)
                        if (avatarFlag==1){
                            if (entity.data.imageUrl != BaseConfig.getInstance.getString(SpName.avatarUrl, "")){
                                SDEventManager.post(entity.data.imageUrl,EnumEventTag.REFRESH_ME_BOTTOM_AVATAR.ordinal)
                            }
                        }
                    }
                }


            }

            override fun onFailure(code: Int, msg: String) {
                showToast(msg)
            }
        })
    }

    fun addPhoto(bean: UserPhotoBean) {
        if (userImages.size < 6) {
            userImages.add(userImages.size - 1, bean)
        } else {
            userImages.removeLast()
            userImages.add(bean)
        }
        photoSelectAdapter.notifyDataSetChanged()
    }

    fun removePhoto(position: Int) {
        userImages.removeAt(position)
        if (userImages.size < 6) {
            if (userImages[userImages.size - 1].imageUrl != "") {
                userImages.add(UserPhotoBean("", "","", false))
            }
        }
        photoSelectAdapter.notifyDataSetChanged()
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    val selectList = PictureSelector.obtainSelectorList(data)
                    addImage(photoSelectPosition, selectList)
                }
            }
        }
    }

}
