
package io.rong.imkit.activity

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import java.util.*

/**
 * ActivityList栈的管理类.
 * 可以便捷的在任意地方 finish 指定的 Activity
 * 在任意地方获得当前栈顶的活跃的Activity对象
 */
class Activities private constructor() {
    private val mActivityList: MutableList<FragmentActivity> = LinkedList()
//    val top: FragmentActivity?
//        get() = if (mActivityList.size > 0) mActivityList[mActivityList.size - 1] else null
    val top: FragmentActivity?
        get() = mActivityList.lastOrNull { !it.isFinishing && !it.isDestroyed }

    val topNormal: FragmentActivity?
        get() = mActivityList.findLast { !it.isFinishing } // 这个方法会找到栈顶没有finishing的


    fun finish(activityClazz: Class<*>) {
        for (activity in mActivityList) {
            if (!activity.isFinishing) {
                if (activity.javaClass.simpleName == activityClazz.simpleName) {
                    activity.finish()
                }
            }
        }
    }

    fun contains(activityClazz: Class<*>): Boolean {
        for (activity in mActivityList) {
            if (!activity.isFinishing) {
                if (activity.javaClass.simpleName == activityClazz.simpleName) {
                    return true
                }
            }
        }
        return false
    }

    fun finishAll() {
        val iterator = mActivityList.iterator()
        while (iterator.hasNext()) {
            val next: Activity = iterator.next()
            iterator.remove()
            next.finish()
        }
    }

    fun finishAllExclude(activityClazz: Class<*>) {
        val iterator = mActivityList.iterator()
        while (iterator.hasNext()) {
            val next: Activity = iterator.next()
            if (!next.isFinishing) {
                if (next.javaClass.simpleName != activityClazz.simpleName) {
                    iterator.remove()
                    next.finish()
                }
            }
        }
    }

    fun remove(activity: FragmentActivity) {
        synchronized(Activities::class.java) { mActivityList.remove(activity) }
    }

    fun add(activity: FragmentActivity) {
        synchronized(Activities::class.java) {
            val activities = mActivityList
            if (!activities.contains(activity) && !activity.isFinishing) {
                activities.add(activity)
            }
        }
    }

    companion object {
        @Volatile
        private var sAppManager: Activities? = null
        fun get(): Activities {
            if (sAppManager == null) {
                synchronized(Activities::class.java) {
                    if (sAppManager == null) {
                        sAppManager =
                            Activities()
                    }
                }
            }
            return sAppManager!!
        }
    }
}