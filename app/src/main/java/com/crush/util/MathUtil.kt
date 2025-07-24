package com.crush.util

import android.Manifest
import android.app.Activity
import com.custom.base.config.BaseConfig
import io.rong.imkit.SpName
import java.util.Date

class MathUtil {
    fun isMultipleOfEight(number: Int,limitNumber: Int):Boolean{
        if (number == 0)return false
        return number % limitNumber==0
    }

    fun isShowFeedBackCard(number: Int,limitNumber: Int):Boolean{
        if (!BaseConfig.getInstance.getBoolean(SpName.passFeedBackShow+DateUtils.getTime(Date()),false)) {
            if (number == 0) return false
            return number % limitNumber == 0
        }
        return false
    }

    fun isShowLocationCard(number: Int,limitNumber: Int,mActivity:Activity):Boolean{
        if (PermissionUtil.checkPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) && PermissionUtil.checkPermission(mActivity,Manifest.permission.ACCESS_COARSE_LOCATION)){
            var remainder = 0
            if (BaseConfig.getInstance.getBoolean(SpName.passFeedBackShow+DateUtils.getTime(Date()),false)){
                remainder++
            }
            if (number <= remainder)return false
            return number % limitNumber==remainder
        }
        return false

    }
}