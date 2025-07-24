package com.crush.ui.member.album

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.crush.Constant
import com.crush.R
import com.crush.adapter.PrivatePhotoAdapter
import com.crush.bean.ImagesBean
import com.crush.entity.PrivateAlbumAddEntity
import com.crush.entity.PrivateAlbumEntity
import io.rong.imkit.event.EnumEventTag
import com.crush.util.SelectPictureUtil
import com.crush.util.UploadPhoto
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.crush.mvp.BasePresenterImpl
import com.crush.util.PermissionUtil
import com.custom.base.util.Md5Util
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.entity.LocalMedia
import com.sunday.eventbus.SDEventManager
import io.rong.imkit.IMCenter
import io.rong.imkit.activity.Activities
import io.rong.imkit.utils.KitStorageUtils
import io.rong.imkit.utils.videocompressor.VideoCompress
import java.io.File


class PrivateAlbumPresenter : BasePresenterImpl<PrivateAlbumContract.View>(), PrivateAlbumContract.Presenter {
    private lateinit var adapter: PrivatePhotoAdapter
    private var urls = arrayListOf<ImagesBean>()
    private val confirm_choose=3003
    override fun initBundle(bundle: Bundle) {
        mView?.apply {
            urls = arrayListOf(
                ImagesBean(
                    "",
                    "", 0,false
                )
            )

            privateAlbumList.layoutManager = GridLayoutManager(mActivity, 3)
            OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                override fun addBody(requestBody: OkHttpBodyEntity) {
                    requestBody.setPost(Constant.user_user_albums_url)
                    requestBody.add("type", 3)
                }

            }, object : SDOkHttpResoutCallBack<PrivateAlbumEntity>() {
                override fun onSuccess(entity: PrivateAlbumEntity) {
                    urls.addAll(entity.data.images)
                    adapter = PrivatePhotoAdapter(
                        mActivity,
                        urls,
                        entity.data.albumCode,
                        object : PrivatePhotoAdapter.OnCallBack {
                            override fun callback() {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    Activities.get().top?.let {activity->
                                        if (!PermissionUtil.checkPermission(activity,Manifest.permission.READ_MEDIA_IMAGES) && !PermissionUtil.checkPermission(mActivity,Manifest.permission.READ_MEDIA_VIDEO)) {
                                            SelectPictureUtil.selectNoTailor(activity)
                                        } else {
                                            PermissionUtil.requestPermissionCallBack(Manifest.permission.READ_MEDIA_IMAGES,
                                                Manifest.permission.READ_MEDIA_VIDEO, activity = activity, type =  4) {
                                                if (it) {
                                                    SelectPictureUtil.selectNoTailor(activity)
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Activities.get().top?.let {activity->
                                        if (!PermissionUtil.checkPermission(activity,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                            SelectPictureUtil.selectNoTailor(activity)
                                        } else {
                                            PermissionUtil.requestPermissionCallBack(Manifest.permission.WRITE_EXTERNAL_STORAGE, activity = activity, type = 4) {
                                                if (it){
                                                    SelectPictureUtil.selectP(activity)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        })
                    privateAlbumList.adapter = adapter

                    val parcelableArrayList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        bundle.getParcelableArrayList("selectList",LocalMedia::class.java)
                    } else {
                        bundle.getParcelableArrayList("selectList")
                    }
                    if (parcelableArrayList != null){
                        addImage(parcelableArrayList)
                    }
                }

            })
        }
    }

    private fun addImage(selectList: ArrayList<LocalMedia>?) {
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
//                LoadingDialog.showLoading(mActivity)
                if (path.contains(".mp4", true) || it.mimeType.contains(
                        ".3gp",
                        true
                    ) || it.mimeType.contains(".mov", true)
                ) {
                    if (it.duration > 60 * 1000) {
                        showToast(mActivity.getString(R.string.video_too_overlong_tip))
                        return
                    }
                    if (it.duration < 1000) {
                        showToast(mActivity.getString(R.string.video_too_short_tip))
                        return
                    }

                    if (it.size > 50 * 1024 * 1024) {
                        showToast(mActivity.getString(R.string.video_too_big_tip))
                        return
                    }
                    var suffix="mp4"
                    if (it.mimeType.contains("/")){
                        suffix = it.mimeType.substring(it.mimeType.lastIndexOf("/") + 1)
                    }

                    val compressPath = KitStorageUtils.getImageSavePath(IMCenter.getInstance().context) +
                                File.separator+"${Md5Util.MD5(System.currentTimeMillis().toString())}.$suffix"

                    VideoCompress.compressVideo(
                        IMCenter.getInstance().context,
                        path, compressPath, object : VideoCompress.CompressListener {
                            override fun onStart() {}
                            override fun onSuccess() {
                                val imagesBean = ImagesBean("","",0,true)
                                addPhoto(imagesBean)
                                UploadPhoto.uploadFileNew(mActivity,compressPath, it.mimeType, object : UploadPhoto.OnLister {
                                    override fun onSuccess(successPath: String,imageCode:String) {
                                        saveAlbums(successPath, it.duration)
                                    }

                                    override fun fail() {
//                                        LoadingDialog.dismissLoading(mActivity)
                                        showToast("upload error,pls try again")
                                    }

                                })
                            }

                            override fun onFail() {
//                                LoadingDialog.dismissLoading(mActivity)
                            }

                            override fun onProgress(percent: Float) {
                            }
                        })
                } else {
                    val imagesBean = ImagesBean("","",0,true)
                    addPhoto(imagesBean)
                    UploadPhoto.uploadFileNew(mActivity,path, it.mimeType, object : UploadPhoto.OnLister {
                        override fun onSuccess(successPath: String,imageCode:String) {
                            saveAlbums(successPath, it.duration)
                        }

                        override fun fail() {
                            showToast("Upload failed")
                        }

                    })
                }


            }
        }
    }

    fun saveAlbums(image: String, videoLength: Long) {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.user_user_albums_add_url)
                requestBody.add("imageUrl", image)
                requestBody.add("type", 3)
                requestBody.add("videoLength", videoLength)

            }
        }, object : SDOkHttpResoutCallBack<PrivateAlbumAddEntity>() {
            override fun onSuccess(entity: PrivateAlbumAddEntity) {
                var lastPosition=0
                for (i in 0 until adapter.mUrls.size){
                    if (adapter.mUrls[i].loading){
                        lastPosition= i
                    }
                }
                adapter.mUrls[lastPosition].imageUrl=entity.data.imageUrl
                adapter.mUrls[lastPosition].imageCode=entity.data.imageCode
                adapter.mUrls[lastPosition].videoLength=entity.data.videoLength
                adapter.mUrls[lastPosition].loading=false
                adapter.notifyDataSetChanged()
//                LoadingDialog.dismissLoading(mActivity)
//                addPhoto(entity.data)
            }

            override fun onFailure(code: Int, msg: String) {
//                LoadingDialog.dismissLoading(mActivity)
            }
        })
    }


    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    val selectList = PictureSelector.obtainSelectorList(data)
                    val intent = Intent(mActivity,PrivateAlbumViewActivity::class.java)
                    intent.putParcelableArrayListExtra(PictureConfig.EXTRA_RESULT_SELECTION, selectList)
                    mActivity.startActivityForResult(intent,confirm_choose)
                }
                confirm_choose->{
                    val selectList = PictureSelector.obtainSelectorList(data)
                    addImage(selectList)
                }
            }
        }
    }

    fun removePhoto(position: Int) {
        adapter.mUrls.removeAt(position)
        if (adapter.mUrls.size==1){
            SDEventManager.post(EnumEventTag.MY_REFRESH.ordinal)
        }
        adapter.notifyDataSetChanged()
    }
//    fun removePhotoBean(imageUrl: String) {
//        for (i in 0 until adapter.mUrls.size){
//            if (adapter.mUrls[i].imageUrl == imageUrl){
//
//            }
//        }
//        adapter.mUrls.removeAt(position)
//        adapter.notifyDataSetChanged()
//    }

    fun addPhoto(bean: ImagesBean) {
        adapter.mUrls.add(1, bean)
        adapter.notifyDataSetChanged()
    }
}