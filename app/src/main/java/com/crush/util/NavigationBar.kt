package com.crush.util

import android.content.Context


/**
 * @author zxp
 * @since 2/8/22
 */
open class NavigationBar {

    /**
     * 如果有底部导航栏 获取底部导航栏高度
     * @param context
     * @return
     */
    fun getBottomNavigatorHeight(context: Context): Int {
        val rid: Int = context.resources.getIdentifier(
            "config_showNavigationBar",
            "bool",
            "android"
        )
        if (0 != rid) {
            val resourceId: Int = context.resources.getIdentifier(
                "navigation_bar_height",
                "dimen",
                "android"
            )
            return context.resources.getDimensionPixelSize(resourceId)
        }
        return 0
    }
}