package com.crush.ui.index

import android.app.Activity
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.crush.bean.MatchIndexBean
import com.crush.util.CollectionUtils
import com.crush.util.HandlerUtils
import com.facebook.datasource.BaseDataSubscriber
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.common.Priority
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import io.rong.imkit.activity.Activities
import io.rong.imkit.utils.RongUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executors


object HomeCardPreloadHelper : HandlerUtils.OnReceiveMessageListener {

    val executorService = Executors.newSingleThreadExecutor()

    fun preloadPic(position: Int, homepageList: List<MatchIndexBean>) {
        if (loadedPosition == position)
            return
        loadSuccess = 0
        loadFialy = 0
        val newList = when (position + 1) {
            1 -> {
                if (homepageList.size >= 10) {
                    homepageList.subList(0, 10)
                } else {
                    homepageList.subList(0, homepageList.size - 1)
                }
            }

            5 -> {
                if (homepageList.size >= 20) {
                    homepageList.subList(10, 20)
                } else {
                    homepageList.subList(0, homepageList.size - 1)
                }
            }

            15 -> {
                if (homepageList.size >= 30) {
                    homepageList.subList(20, 30)
                } else {
                    homepageList.subList(0, homepageList.size - 1)
                }
            }

            else -> {
                arrayListOf()
            }
        }
        if (newList.isNullOrEmpty())
            return
        var tonum = 0
        newList.stream().forEach {
            it.images.stream().forEach {
                tonum++
            }
        }
        val oneDan = getOneDan(newList)
        val oneTwoDan = getTwoDan(newList)
        val oneThreeDan = getThreeDan(newList)
        // 在你的Activity或Fragment中调用这个函数
        loadPic(oneDan){
            loadPic(oneTwoDan){
                loadPic(oneThreeDan)
            }
        }
    }

    //预加载卡片数量
    var loadSuccess = 0
    var loadFialy = 0

    //加载过卡片数据索引
    var loadedPosition = -1

    private fun loadPic(homepageList: List<String>, loadEnd: (() -> Unit?)? = null) {
        var maxSize = homepageList.size
        for (i in homepageList.indices) {
            if (RongUtils.isDestroy(Activities.get().top)){
                return
            }
            var isPrefetchLoadSuccess = false
            val imageUrl = homepageList[i]
            val requestManager = Activities.get().top?.let {
                Glide.with(it)
                    .load(imageUrl)
                    .timeout(300)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .addListener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            isPrefetchLoadSuccess = false
                            loadFialy++
                            maxSize--
                            if (maxSize <= 0) {
                                loadEnd?.invoke()
                            }
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            isPrefetchLoadSuccess = true
                            loadSuccess++
                            maxSize--
                            if (maxSize <= 0) {
                                loadEnd?.invoke()
                            }
                            return false
                        }

                    })
                    .preload()
            }

            CoroutineScope(Dispatchers.IO).launch {
                // 在后台线程执行
                if (isPrefetchLoadSuccess) return@launch
                if (RongUtils.isDestroy(Activities.get().top)){
                    return@launch
                }
                // 清除之前的图片请求
                Activities.get().top?.let {
                    Glide.with(it).clear(requestManager)
                }

                // 解析图片 URI
                val uri = Uri.parse(imageUrl)

                // 构建 Fresco 图片请求
                val imageRequest = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                    .build()

                // 获取 Fresco 图片管道
                val imagePipeline = Fresco.getImagePipeline()

                // 异步预取图片到磁盘缓存
                imagePipeline.prefetchToDiskCache(
                    imageRequest,
                    null,
                    Priority.MEDIUM
                ).subscribe(object : BaseDataSubscriber<Void?>() {
                    override fun onNewResultImpl(dataSource: com.facebook.datasource.DataSource
                    <Void?>) {
                        loadSuccess++
                        maxSize--
                        if (maxSize <= 0) {
                            loadEnd?.invoke()
                        }
                    }

                    override fun onFailureImpl(dataSource: com.facebook.datasource.DataSource<Void?>) {
                        loadFialy++
                        maxSize--
                        if (maxSize <= 0) {
                            loadEnd?.invoke()
                        }
                    }

                }, executorService)

                // 延迟 300 毫秒，但请注意，这里实际上是在后台线程延迟，不会阻塞主线程
                delay(300)
            }

        }
    }

    private fun getOneDan(homepageList: List<MatchIndexBean>): ArrayList<String> {
        val lists = ArrayList<String>()
        for (i in 0 until homepageList.size) {
            if (CollectionUtils.isNotEmpty(homepageList[i].images)) {
                lists.add(homepageList[i].images[0].imageUrl)
            }
        }
        return lists
    }

    private fun getTwoDan(homepageList: List<MatchIndexBean>): ArrayList<String> {
        val lists = ArrayList<String>()
        for (i in homepageList.indices) {
            if (CollectionUtils.isNotEmpty(homepageList[i].images)) {
                for (k in 0 until homepageList[i].images.size) {
                    if (k >= 3)
                        break
                    if (k > 0) {
                        lists.add(homepageList[i].images[k].imageUrl)
                    }
                }
            }
        }
        return lists
    }

    private fun getThreeDan(homepageList: List<MatchIndexBean>): ArrayList<String> {
        val lists = ArrayList<String>()
        for (i in 0 until homepageList.size) {
            if (CollectionUtils.isNotEmpty(homepageList[i].images)) {
                for (k in 0 until homepageList[i].images.size) {
                    if (k >= 3) {
                        lists.add(homepageList[i].images[k].imageUrl)
                    }
                }
            }
        }
        return lists
    }

    override fun handlerMessage(msg: Message?) {
        when(msg?.what){
            1->{
                Log.e("~~~", "handlerMessage: " )
            }
        }
    }
}