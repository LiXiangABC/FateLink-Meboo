package com.crush.dot

import android.content.Context
import android.os.Build
import android.text.TextUtils
import com.crush.BuildConfig
import com.crush.util.HttpRequest
import org.json.JSONException
import org.json.JSONObject

object DotLogUtil {
    fun setEventName(event: Any): EventContent {
        return EventContent(event)
    }

    class EventContent {
        private var eventName: Any? = null
        private var jsonObject: JSONObject? = null
        private var coustomJsonObject: JSONObject? = null

        constructor(eventName: Any) {
            this.eventName = eventName
        }

        fun setRemark(remark: String = ""): EventContent {
            add("remark", remark)
            return this
        }

        fun addJSONObject(coustomJsonObject: JSONObject?) :EventContent{
            this.coustomJsonObject = coustomJsonObject
            return this
        }

        fun add(key: String?, value: Any?): EventContent? {
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

        fun commit(context: Context? = null, callBack: (() -> Unit?)? = null) {
            if (coustomJsonObject != null) {
                jsonObject = coustomJsonObject
            } else {
                if (jsonObject == null) {
                    jsonObject = JSONObject()
                }
            }
            jsonObject?.put("versionName", BuildConfig.VERSION_NAME)
            jsonObject?.put("versionCode", BuildConfig.VERSION_CODE)
            jsonObject?.put("Vendor", Build.MANUFACTURER)
            jsonObject?.put("model", Build.MODEL)
            HttpRequest.commonNotify(eventName!!, jsonObject.toString(), context,callBack = callBack)
        }
    }
}