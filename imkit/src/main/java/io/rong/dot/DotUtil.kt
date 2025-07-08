package io.rong.dot

import android.text.TextUtils
import io.rong.dot.listener.ImDotExecuteListener
import org.json.JSONException
import org.json.JSONObject

object DotUtil {


    var listenet: ImDotExecuteListener? = null
    fun setEventName(eventName: String): EventContent {
        return EventContent(eventName)
    }

    class EventContent(val eventName: String) {
        private var jsonObject: JSONObject? = null
        fun add(key: String?, value: Any?): EventContent {
            if (jsonObject == null)
                jsonObject = JSONObject()
            if (!TextUtils.isEmpty(key) && value != null) {
                try {
                    jsonObject?.put(key, value)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            return this
        }

        fun commit() {
            listenet?.execute(eventName, jsonObject!!)
        }
    }
}