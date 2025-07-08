package io.rong.imkit.utils

import android.util.Log
import com.custom.base.config.BaseConfig.Companion.getInstance
import io.rong.imkit.SpName.conversationFlashList
import io.rong.imkit.SpName.conversationList
import io.rong.imkit.SpName.userCode

/**
 * @Author ct
 * @Date 2024/5/20 14:23
 * 原因：因为Homepresenter里面获取在线列表的时候通过sp保存用户fc标识和用户在线状态字段，导致了oom，fb上统计总共5个用户受影响
 */
class DataCommonUtil {
    //维护每个用户自己的在线列表 key 用户usercode，
    //<usercode,<SpName.conversationFlashList + entity.data[it].userCodeFriend, entity.data[it].flashchatFlag>>
    private val stringHashMap = HashMap<String, HashMap<String, Int>>()

    fun setStringLiveData(key: String, defaultValue: HashMap<String, Int>) {
        stringHashMap[key] = defaultValue
        // 遍历 HashMap 并打印值
        stringHashMap.forEach { (key, innerHashMap) ->
            Log.i("DataCommonUtil", "setStringLiveData  Key: $key")
            innerHashMap.forEach { (innerKey, value) ->
                Log.i("DataCommonUtil", "setStringLiveData Inner key: $innerKey, Value: $value")
            }
        }
    }

    private fun getStringLiveData(key: String): HashMap<String, Int>? {
        Log.i("DataCommonUtil", "getStringLiveData  Key: $key")
        return try {
            stringHashMap[key]
        } catch (e: Exception) {
            throw RuntimeException("未找到key值为： $key 的MutableLiveData")
        }
    }

    fun updateFlashTagKeyToValue(targetId: String, flashTagValue: Int) {
        Log.i("DataCommonUtil", "updateFlashTagKeyToValue  Key: $targetId,Value: $flashTagValue")
        if (stringHashMap.containsKey(getInstance.getString(userCode, ""))) {
            stringHashMap[getInstance.getString(
                userCode,
                ""
            )]?.set(conversationFlashList + targetId, flashTagValue)
        }
    }

    fun updateOnLineTagKeyToValue(targetId: String, onlineTagValue: Int) {
        Log.i("DataCommonUtil", "updateOnLineTagKeyToValue  Key: $targetId,Value: $onlineTagValue")
        if (stringHashMap.containsKey(getInstance.getString(userCode, ""))) {
            stringHashMap[getInstance.getString(
                userCode,
                ""
            )]?.set(conversationList + targetId, onlineTagValue)
        }
    }

    fun getFlashTag(targetId: String): Int {
        Log.i("DataCommonUtil", "getFlashTag  Key: $targetId")
        if (stringHashMap.containsKey(getInstance.getString(userCode, ""))) {
            val map = getStringLiveData(getInstance.getString(userCode, ""))
            val flashChatTag = map?.get(conversationFlashList + targetId)
            return flashChatTag ?: 0
        }
        return 0;
    }

    fun getOnlineTag(targetId: String): Int {
        Log.i("DataCommonUtil", "getOnlineTag  Key: $targetId")
        if (stringHashMap.containsKey(getInstance.getString(userCode, ""))) {
            val map = getStringLiveData(getInstance.getString(userCode, ""))
            val onlineTag = map?.get(conversationList + targetId)
            return onlineTag ?: 0
        }
        return 0;
    }

    companion object {
        //伴生对象，该代码块内的属性和方法都是静态类型的
        @Volatile
        private var commonUtil: DataCommonUtil? = null
        fun getInstance(): DataCommonUtil {
            return (commonUtil ?: synchronized(this) {
                commonUtil ?: DataCommonUtil().also {
                    commonUtil = it
                }
            })
        }
    }
}