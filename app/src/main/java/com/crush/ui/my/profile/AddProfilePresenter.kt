package com.crush.ui.my.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.crush.R
import com.crush.adapter.AddPhotoSelectAdapter
import com.crush.bean.PhotoBean
import com.crush.dialog.SelectPhotoDialog
import com.crush.dot.DotLogEventName
import com.crush.dot.DotLogUtil
import com.crush.util.*
import com.crush.mvp.BasePresenterImpl
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.entity.LocalMedia
import kotlin.collections.ArrayList


class AddProfilePresenter : BasePresenterImpl<AddProfileContact.View>() {
    private lateinit var photoSelectAdapter: AddPhotoSelectAdapter
    private var photoSelectPosition: Int = 0 //选择照片
    private var gender = 1 // 0未选择 1男2女0未知
    var imagesShow = arrayListOf(
        PhotoBean("", "", false),
        PhotoBean("", "", false),
        PhotoBean("", "", false),
        PhotoBean("", "", false),
        PhotoBean("", "", false),
        PhotoBean("", "", false)
    )
    fun getGender():Int{return gender}
    fun getImageShow():ArrayList<PhotoBean>{return imagesShow}

    override fun initBundle(bundle: Bundle) {
        mView?.apply {
            initNickName()

            initPhoto()


            initLooking()

        }

    }


    /**
     * 头像上传
     */
    private fun AddProfileContact.View.initPhoto() {

        photoList.layoutManager = GridLayoutManager(mActivity, 3)
        photoSelectAdapter =
            AddPhotoSelectAdapter(imagesShow, mActivity, object : AddPhotoSelectAdapter.OnCallBack {
                override fun selectPhoto(url: String, position: Int) {
                    photoSelectPosition = position
                    SelectPhotoDialog(mActivity).showPopupWindow()
                }

                override fun delPhoto(url: String, position: Int) {
                    imagesShow.removeAt(position)
                    imagesShow.add(PhotoBean("", "", false))
                    txtPhotoNext.isEnabled = imagesShow[0].imageUrl != ""
                    photoSelectAdapter.notifyDataSetChanged()

                }

            })
        photoList.adapter = photoSelectAdapter
    }

    /**
     * 爱好选择
     */
    private fun AddProfileContact.View.initLooking() {

        radioLookingGroup.setOnCheckedChangeListener { p0, p1 ->
            textLookingNext.setTextColor(ContextCompat.getColor(appContext, R.color.color_001912))
            txtLookingNext.setBackgroundResource(R.drawable.shape_solid_pink_radius_26)
            txtLookingNext.isEnabled = true
        }

    }
    fun setGender(gender:Int){
        this.gender = gender
    }

    /**
     * 昵称输入
     */
    private fun initNickName() {
        mView?.apply {
            editNickname.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    txtNicknameNext.isEnabled = editNickname.text.toString().isNotEmpty()

                }

                override fun afterTextChanged(p0: Editable?) {
                }

            })
        }
    }

    private fun addImage(addPosition: Any, selectList: ArrayList<LocalMedia>?) {
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

                if (imagesShow[photoSelectPosition].imageUrl != "") {
                    imagesShow[photoSelectPosition].imageUrl = ""
                    imagesShow[photoSelectPosition].loading = true
                    photoSelectAdapter.notifyItemChanged(photoSelectPosition)
                } else {
                    for (i in 0 until imagesShow.size) {
                        if (imagesShow[i].imageLoadUrl == "" && !imagesShow[i].loading) {
                            imagesShow[i].loading = true
                            photoSelectAdapter.notifyItemChanged(i)
                            break
                        }

                    }
                }



                UploadPhoto.uploadFileNew(
                    mActivity,
                    path,
                    it.mimeType,
                    object : UploadPhoto.OnLister {
                        override fun onSuccess(successPath: String, imageCode: String) {
                            var showPosition = 0
                            for (i in 0 until imagesShow.size) {
                                if (imagesShow[i].imageUrl == "") {
                                    showPosition = i
                                    break
                                }
                            }
                            imagesShow[showPosition].imageUrl = path
                            imagesShow[showPosition].imageLoadUrl = successPath
                            imagesShow[showPosition].loading = false
                            photoSelectAdapter.notifyItemChanged(showPosition)
                            txtPhotoNext.isEnabled = true
//                            HttpRequest.commonNotify(606, successPath)
                            DotLogUtil.setEventName(DotLogEventName.USER_PROFILE_UPLOAD_IMAGE_RESULT).commit(mActivity)

                        }

                        override fun fail() {
                            showToast("upload error,pls try again")
                        }

                    })
            }
        }
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