package com.crush.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.crush.R
import com.crush.view.GlideEngine
import com.custom.base.manager.SDActivityManager
import com.luck.picture.lib.animators.AnimationType
import com.luck.picture.lib.basic.PictureSelectionModel
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.*
import com.luck.picture.lib.config.SelectMimeType.ofImage
import com.luck.picture.lib.config.SelectModeConfig.MULTIPLE
import com.luck.picture.lib.engine.CompressFileEngine
import com.luck.picture.lib.engine.CropFileEngine
import com.luck.picture.lib.engine.UriToFileTransformEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener
import com.luck.picture.lib.interfaces.OnQueryFilterListener
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.interfaces.OnSelectLimitTipsListener
import com.luck.picture.lib.language.LanguageConfig
import com.luck.picture.lib.style.PictureSelectorStyle
import com.luck.picture.lib.style.PictureWindowAnimationStyle
import com.luck.picture.lib.style.SelectMainStyle
import com.luck.picture.lib.style.TitleBarStyle
import com.luck.picture.lib.utils.*
import com.luck.picture.lib.utils.DateUtils
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropImageEngine
import top.zibin.luban.Luban
import top.zibin.luban.OnNewCompressListener
import java.io.File


/**
 * 时间：2020-12-22
 * 描述：图片选择器
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
super.onActivityResult(requestCode, resultCode, data)
if (resultCode == Activity.RESULT_OK) {
when (requestCode) {
PictureConfig.CHOOSE_REQUEST -> {
// 图片、视频、音频选择结果回调
val selectList = PictureSelector.obtainMultipleResult(data)
// 例如 LocalMedia 里面返回三种path
// 1.media.getPath(); 为原图path
// 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
// 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
// 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
}
}
}
}
 */
object SelectPictureUtil {
    private var selectorStyle: PictureSelectorStyle? = null
    fun selectPhoto(context: Context){
        selectorStyle = PictureSelectorStyle()
        val mainStyle = selectorStyle!!.selectMainStyle
        // 进入相册
        val selectionModel: PictureSelectionModel = PictureSelector.create(context)
            .openGallery(ofImage())
            .setSelectorUIStyle(selectorStyle)
            .setImageEngine(GlideEngine.createGlideEngine())
            .setCropEngine(ImageFileCropEngine(context))
            .setCompressEngine(ImageFileCompressEngine())
            .setSandboxFileEngine(MeSandboxFileEngine())
            .setSelectLimitTipsListener(MeOnSelectLimitTipsListener())
            .isPageSyncAlbumCount(true)
            .setQueryFilterListener(OnQueryFilterListener { false }) //.setExtendLoaderEngine(getExtendLoaderEngine())
            .setSelectionMode(SelectModeConfig.SINGLE)
            .setLanguage(LanguageConfig.ENGLISH)
            .isDisplayTimeAxis(false)
            .isPageStrategy(true)
            .isOriginalControl(false)
            .isDisplayCamera(false)
            .setSkipCropMimeType(
                *arrayOf(
                    PictureMimeType.ofGIF(),
                    PictureMimeType.ofWEBP()
                )
            )
            .isWithSelectVideoImage(true)
            .isPreviewFullScreenMode(true)
            .isDirectReturnSingle(true)
            .setMaxSelectNum(1)
            .setMaxVideoSelectNum(1)
            .setRecyclerAnimationMode(AnimationType.ALPHA_IN_ANIMATION)
            .isGif(true)
        selectionModel.forResult(PictureConfig.CHOOSE_REQUEST)
    }
    fun selectP(context: Context){
        selectorStyle = PictureSelectorStyle()
        val mainStyle = selectorStyle!!.selectMainStyle
        // 进入相册
        val selectionModel: PictureSelectionModel = PictureSelector.create(context)
            .openGallery(SelectMimeType.ofAll())
            .setSelectorUIStyle(selectorStyle)
            .setImageEngine(GlideEngine.createGlideEngine())
            .setCropEngine(ImageFileCropEngine(context))
            .setCompressEngine(ImageFileCompressEngine())
            .setSandboxFileEngine(MeSandboxFileEngine())
            .setSelectLimitTipsListener(MeOnSelectLimitTipsListener())
            .isPageSyncAlbumCount(true)
            .setQueryFilterListener(OnQueryFilterListener { false }) //.setExtendLoaderEngine(getExtendLoaderEngine())
            .setSelectionMode(SelectModeConfig.SINGLE)
            .setLanguage(LanguageConfig.ENGLISH)
            .isDisplayTimeAxis(false)
            .isPageStrategy(true)
            .isOriginalControl(false)
            .isDisplayCamera(false)
            .setSkipCropMimeType(
                *arrayOf(
                    PictureMimeType.ofGIF(),
                    PictureMimeType.ofWEBP()
                )
            )
            .isWithSelectVideoImage(true)
            .isPreviewFullScreenMode(true)
            .isDirectReturnSingle(true)
            .setMaxSelectNum(1)
            .setMaxVideoSelectNum(1)
            .setRecyclerAnimationMode(AnimationType.ALPHA_IN_ANIMATION)
            .isGif(true)
        selectionModel.forResult(PictureConfig.CHOOSE_REQUEST)
    }
    fun selectNoTailor(context: Context){
        selectorStyle = PictureSelectorStyle()
        // 进入相册
        val selectionModel: PictureSelectionModel = PictureSelector.create(context)
            .openGallery(SelectMimeType.ofAll())
            .setSelectorUIStyle(selectorStyle)
            .setImageEngine(GlideEngine.createGlideEngine())
            .setCompressEngine(ImageFileCompressEngine())
            .setSandboxFileEngine(MeSandboxFileEngine())
            .setSelectLimitTipsListener(MeOnSelectLimitTipsListener())
            .isPageSyncAlbumCount(true)
            .setQueryFilterListener(OnQueryFilterListener { false }) //.setExtendLoaderEngine(getExtendLoaderEngine())
            .setSelectionMode(SelectModeConfig.SINGLE)
            .setLanguage(LanguageConfig.ENGLISH)
            .isDisplayTimeAxis(false)
            .isPageStrategy(true)
            .isOriginalControl(false)
            .isDisplayCamera(false)
            .setSkipCropMimeType(
                *arrayOf(
                    PictureMimeType.ofGIF(),
                    PictureMimeType.ofWEBP()
                )
            )
            .isWithSelectVideoImage(true)
            .isPreviewFullScreenMode(true)
            .isDirectReturnSingle(true)
            .setMaxSelectNum(1)
            .setMaxVideoSelectNum(1)
            .setRecyclerAnimationMode(AnimationType.ALPHA_IN_ANIMATION)
            .isGif(true)
        selectionModel.forResult(PictureConfig.CHOOSE_REQUEST)
    }

    /**
     * 拦截自定义提示
     */
    private class MeOnSelectLimitTipsListener : OnSelectLimitTipsListener {
        override fun onSelectLimitTips(
            context: Context,
            media: LocalMedia?,
            config: SelectorConfig,
            limitType: Int
        ): Boolean {
            if (limitType == SelectLimitType.SELECT_MIN_SELECT_LIMIT) {
                ToastUtils.showToast(context, "The picture must be no less than" + config.minSelectNum)
                return true
            } else if (limitType == SelectLimitType.SELECT_MIN_VIDEO_SELECT_LIMIT) {
                ToastUtils.showToast(context, "Video at least no less" + config.minVideoSelectNum)
                return true
            } else if (limitType == SelectLimitType.SELECT_MIN_AUDIO_SELECT_LIMIT) {
                ToastUtils.showToast(context, "The audio should not be lower than the minimum" + config.minAudioSelectNum)
                return true
            }
            return false
        }
    }
    /**
     * 自定义沙盒文件处理
     */
    private class MeSandboxFileEngine : UriToFileTransformEngine {
        override fun onUriToFileAsyncTransform(
            context: Context,
            srcPath: String,
            mineType: String,
            call: OnKeyValueResultCallbackListener
        ) {
            if (call != null) {
                call.onCallback(
                    srcPath,
                    SandboxTransformUtils.copyPathToSandbox(context, srcPath, mineType)
                )
            }
        }
    }

    /**
     * 自定义压缩
     */
    private class ImageFileCompressEngine : CompressFileEngine {
        override fun onStartCompress(
            context: Context,
            source: java.util.ArrayList<Uri>,
            call: OnKeyValueResultCallbackListener
        ) {
            Luban.with(context).load(source).ignoreBy(100).setRenameListener { filePath ->
                val indexOf = filePath.lastIndexOf(".")
                val postfix = if (indexOf != -1) filePath.substring(indexOf) else ".jpg"
                DateUtils.getCreateFileName("CMP_") + postfix
            }.filter { path ->
                if (PictureMimeType.isUrlHasImage(path) && !PictureMimeType.isHasHttp(path)) {
                    true
                } else !PictureMimeType.isUrlHasGif(path)
            }.setCompressListener(object : OnNewCompressListener {
                override fun onStart() {}
                override fun onSuccess(source: String, compressFile: File) {
                    if (call != null) {
                        call.onCallback(source, compressFile.absolutePath)
                    }
                }

                override fun onError(source: String, e: Throwable) {
                    if (call != null) {
                        if (source!="") {
                            call.onCallback(source, null)
                        }
                    }
                }
            }).launch()
        }
    }
    /**
     * 自定义裁剪
     */
    private class ImageFileCropEngine(var context: Context) : CropFileEngine {
        override fun onStartCrop(
            fragment: Fragment,
            srcUri: Uri,
            destinationUri: Uri,
            dataSource: ArrayList<String>,
            requestCode: Int
        ) {
            val options = buildOptions(context)
            val uCrop = UCrop.of(srcUri, destinationUri, dataSource)
            uCrop.withOptions(options)
            uCrop.setImageEngine(object : UCropImageEngine {
                override fun loadImage(context: Context, url: String, imageView: ImageView) {
                    if (!ImageLoaderUtils.assertValidRequest(context)) {
                        return
                    }
                    Glide.with(context).load(url).override(180, 180).into(imageView)
                }

                override fun loadImage(
                    context: Context,
                    url: Uri,
                    maxWidth: Int,
                    maxHeight: Int,
                    call: UCropImageEngine.OnCallbackListener<Bitmap>
                ) {
                    Glide.with(context).asBitmap().load(url).override(maxWidth, maxHeight)
                        .into(object : CustomTarget<Bitmap?>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap?>?
                            ) {
                                if (call != null) {
                                    call.onCall(resource)
                                }
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                                if (call != null) {
                                    call.onCall(null)
                                }
                            }
                        })
                }
            })
            uCrop.start(fragment.requireActivity(), fragment, requestCode)
        }
    }
    private fun buildOptions(context: Context): UCrop.Options {
        val options = UCrop.Options()
        options.setHideBottomControls(true)
        options.setFreeStyleCropEnabled(false)
        options.setShowCropFrame(true)
        options.setShowCropGrid(false)
        options.setCircleDimmedLayer(false)
        options.withAspectRatio(3f, 4f)
        options.setCropOutputPathDir(getSandboxPath(context).toString())
        options.isCropDragSmoothToCenter(false)
        options.setSkipCropMimeType(*arrayOf(PictureMimeType.ofGIF(), PictureMimeType.ofWEBP()))
        options.isForbidCropGifWebp(true)
        options.isForbidSkipMultipleCrop(true)
        options.setMaxScaleMultiplier(100f)
        if (selectorStyle != null && selectorStyle!!.selectMainStyle.statusBarColor != 0) {
            val mainStyle: SelectMainStyle = selectorStyle!!.selectMainStyle
            val isDarkStatusBarBlack = mainStyle.isDarkStatusBarBlack
            val statusBarColor = mainStyle.statusBarColor
            options.isDarkStatusBarBlack(isDarkStatusBarBlack)
            if (StyleUtils.checkStyleValidity(statusBarColor)) {
                options.setStatusBarColor(statusBarColor)
                options.setToolbarColor(statusBarColor)
            } else {
                options.setStatusBarColor(
                    ContextCompat.getColor(
                        context,
                        R.color.ps_color_grey
                    )
                )
                options.setToolbarColor(ContextCompat.getColor(context, R.color.ps_color_grey))
            }
            val titleBarStyle: TitleBarStyle = selectorStyle!!.titleBarStyle
            if (StyleUtils.checkStyleValidity(titleBarStyle.titleTextColor)) {
                options.setToolbarWidgetColor(titleBarStyle.titleTextColor)
            } else {
                options.setToolbarWidgetColor(
                    ContextCompat.getColor(
                        context,
                        R.color.ps_color_white
                    )
                )
            }
        } else {
            options.setStatusBarColor(ContextCompat.getColor(context, R.color.ps_color_grey))
            options.setToolbarColor(ContextCompat.getColor(context, R.color.ps_color_grey))
            options.setToolbarWidgetColor(
                ContextCompat.getColor(
                    context,
                    R.color.ps_color_white
                )
            )
        }
        return options
    }

    /**
     * 创建自定义输出目录
     *
     * @return
     */
    private fun getSandboxPath(context:Context): String? {
        val externalFilesDir: File? = context.getExternalFilesDir("")
        val customFile = File(externalFilesDir?.absolutePath, "Sandbox")
        if (!customFile.exists()) {
            customFile.mkdirs()
        }
        return customFile.absolutePath + File.separator
    }
    /**
     * 选择图片
     */
    fun selectAlbums() {
        val animationStyle = PictureWindowAnimationStyle()
        animationStyle.setActivityEnterAnimation(R.anim.ps_anim_up_in)
        animationStyle.setActivityExitAnimation(R.anim.ps_anim_down_out)
        val selectorStyle = PictureSelectorStyle()

        PictureSelector.create(SDActivityManager.instance.lastActivity)
            .openGallery(ofImage())
            .setSelectionMode(SelectModeConfig.SINGLE)
            .setSelectorUIStyle(selectorStyle)
            .isDisplayCamera(false)
            .setLanguage(LanguageConfig.ENGLISH)
            .setImageEngine(GlideEngine.createGlideEngine())
//            .setInjectLayoutResourceListener(object :OnInjectLayoutResourceListener {
//                override fun getLayoutResourceId(context: Context?, resourceSource: Int): Int {
//                    return when (resourceSource) {
//                        InjectResourceSource.MAIN_ITEM_IMAGE_LAYOUT_RESOURCE -> R.layout.ps_custom_item_grid_image
//                        else -> 0
//                    }
//                }
//
//            })
            .forResult(PictureConfig.CHOOSE_REQUEST)
    }
    fun selectCamera(context: Context) {
       PictureSelector.create(context)
            .openCamera(ofImage())
            .setCropEngine(ImageFileCropEngine(context))
            .setCompressEngine(ImageFileCompressEngine())
            .setSandboxFileEngine(MeSandboxFileEngine())
            .setSelectLimitTipsListener(MeOnSelectLimitTipsListener())
            .setLanguage(LanguageConfig.ENGLISH)
            .isOriginalControl(false)
            .setMaxVideoSelectNum(1)
            .forResultActivity(PictureConfig.CHOOSE_REQUEST)

    }

    /**
     * 包括裁剪和压缩后的缓存，要在上传成功后调用，注意：需要系统sd卡权限
     */
    fun deleteCache() {
        PictureFileUtils.deleteCacheDirFile(SDActivityManager.instance.lastActivity, 10001)
    }

}