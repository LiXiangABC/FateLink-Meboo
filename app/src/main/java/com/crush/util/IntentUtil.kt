package com.crush.util

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.custom.base.manager.SDActivityManager
import io.rong.imkit.activity.Activities

object IntentUtil {
    /**
     * 页面跳转
     * @param clz
     * @param bundle
     * @param requestCode
     * @param flags
     * @param view
     * @param activity
     * @param animationStart 启动动画
     * @param animationEnd 关闭动画
     */
    fun startActivity(
        clz: Class<*>,
        bundle: Bundle? = null,
        requestCode: Int = 0,
        flags: List<Int> = listOf(),
        view: View? = null,
        activity:Activity? = null
    ) {
        val currentActivity = Activities.get().top ?: return
        if (currentActivity.isFinishing || currentActivity.isDestroyed) {
            return
        }

        val intent = Intent(currentActivity, clz)
        flags.forEach { intent.addFlags(it) }
        bundle?.let { intent.putExtras(it) }

        if (requestCode == 0) {
            if (view == null) {
                currentActivity.startActivity(intent)
            } else {
                view.setOnClickListener { currentActivity.startActivity(intent) }
            }
        } else {
            if (view == null) {
                currentActivity.startActivityForResult(intent, requestCode)
            } else {
                view.setOnClickListener { currentActivity.startActivityForResult(intent, requestCode) }
            }
        }

    }
}