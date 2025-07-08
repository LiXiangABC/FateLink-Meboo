package io.rong.imkit.utils

import android.util.Log
import com.custom.base.config.BaseConfig
import com.custom.base.config.BaseConfig.Companion.getInstance
import io.rong.imkit.SpName
import java.text.SimpleDateFormat

object ChatMessageNumber {
    var userMessageNum:Int = 0
    fun saveTodayMessageNumber(targetId:String){
        val sf = SimpleDateFormat("yyyyMMdd")
        val day = sf.format(System.currentTimeMillis())
        val userCode = getInstance.getString(SpName.userCode, "")
        var num = getInstance.getInt(userCode+targetId + day, 0)
        num += 1
        getInstance.setInt(userCode+targetId + day, num)
    }
    fun getTodayMessageNumber(targetId: String): Int {
        val sf = SimpleDateFormat("yyyyMMdd")
        val day = sf.format(System.currentTimeMillis())
        val userCode = getInstance.getString(SpName.userCode, "")
        return getInstance.getInt(userCode+targetId + day, 0)
    }
}