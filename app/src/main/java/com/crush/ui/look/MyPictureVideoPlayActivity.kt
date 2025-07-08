package com.crush.ui.look

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.MediaController
import android.widget.VideoView
import com.crush.Constant
import com.crush.R
import com.crush.entity.BaseEntity
import io.rong.imkit.event.EnumEventTag
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.sunday.eventbus.SDEventManager
import io.rong.common.RLog
import io.rong.imkit.picture.PictureBaseActivity

class MyPictureVideoPlayActivity : PictureBaseActivity(), MediaPlayer.OnErrorListener,
    MediaPlayer.OnPreparedListener,
    MediaPlayer.OnCompletionListener,
    View.OnClickListener {
    val TAG = MyPictureVideoPlayActivity::class.java.canonicalName
    var video_path = ""
    var picture_left_back: ImageView? = null
    var mMediaController: MediaController? = null
    var mVideoView: VideoView? = null
    var iv_play: ImageView? = null
    var mPositionWhenPaused = -1
    var pictureDeleted: ImageView? = null

    override fun getResourceId(): Int {
        return R.layout.my_picture_activity_video_play
    }

    override fun initWidgets() {
        super.initWidgets()
        video_path = intent.getStringExtra("video_path").toString()
        if (TextUtils.isEmpty(video_path)) {
            RLog.d(TAG, "video_path is empty! return directly!")
            return
        }
        picture_left_back = findViewById(R.id.picture_left_back)
        mVideoView = findViewById(R.id.video_view)
        mVideoView!!.setBackgroundColor(Color.BLACK)
        iv_play = findViewById(R.id.iv_play)
        pictureDeleted = findViewById(R.id.picture_deleted)
        mMediaController = MediaController(this)
        mVideoView!!.setOnCompletionListener(this)
        mVideoView!!.setOnPreparedListener(this)
        mVideoView!!.setMediaController(mMediaController)
        picture_left_back!!.setOnClickListener(this)
        iv_play!!.setOnClickListener(this)
        pictureDeleted!!.setOnClickListener(this)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(
            object : ContextWrapper(newBase) {
                override fun getSystemService(name: String): Any {
                    return if (AUDIO_SERVICE == name) {
                        applicationContext.getSystemService(name)
                    } else super.getSystemService(name)
                }
            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        mMediaController = null
        mVideoView = null
        iv_play = null
        super.onDestroy()
    }

    override fun onStart() {
        // Play Video
        if (mVideoView != null) {
            mVideoView!!.setVideoPath(video_path)
            mVideoView!!.start()
        }
        super.onStart()
    }

    override fun onPause() {
        // Stop video when the activity is pause.
        if (mVideoView != null) {
            mPositionWhenPaused = mVideoView!!.currentPosition
            mVideoView!!.stopPlayback()
        }
        super.onPause()
    }

    override fun onResume() {
        // Resume video player
        if (mPositionWhenPaused >= 0) {
            if (mVideoView != null) {
                mVideoView!!.seekTo(mPositionWhenPaused)
            }
            mPositionWhenPaused = -1
        }
        super.onResume()
    }

    override fun onError(player: MediaPlayer?, arg1: Int, arg2: Int): Boolean {
        return false
    }

    override fun onCompletion(mp: MediaPlayer?) {
        if (null != iv_play) {
            iv_play!!.visibility = View.VISIBLE
        }
    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.picture_left_back) {
            finish()
        } else if (id == R.id.iv_play) {
            if (mVideoView != null) {
                mVideoView!!.start()
            }
            iv_play!!.visibility = View.INVISIBLE
        } else if (id == R.id.picture_deleted) {
            OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                override fun addBody(requestBody: OkHttpBodyEntity) {
                    requestBody.setPost(Constant.user_user_albums_remove_url)
                    requestBody.add("albumCode",intent.getStringExtra("albumCode").toString())
                    requestBody.add("imageCode",intent.getStringExtra("imageCode").toString())
                    requestBody.add("type",3)
                }
            }, object : SDOkHttpResoutCallBack<BaseEntity>() {
                override fun onSuccess(entity: BaseEntity) {
                    SDEventManager.post(intent.getIntExtra("position",0), EnumEventTag.PRIVATE_ALBUMS_REMOVE.ordinal)
                    finish()
                }

            })
        }
    }

    override fun onPrepared(mp: MediaPlayer) {
        mp.setOnInfoListener(
            MediaPlayer.OnInfoListener { mp, what, extra ->
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    if (mVideoView != null) {
                        mVideoView!!.setBackgroundColor(Color.TRANSPARENT)
                    }
                    return@OnInfoListener true
                }
                false
            })
    }
}