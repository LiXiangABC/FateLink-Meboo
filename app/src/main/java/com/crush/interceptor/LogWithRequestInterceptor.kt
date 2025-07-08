package com.crush.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset

/**
 * @Description:设置网络请求日志拦截器
 * @Author: zhanghao
 */

class LogWithRequestInterceptor() : Interceptor {
    private val lineSeparator = System.getProperty("line.separator")
    private val utf8 = Charset.forName("UTF-8")

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        printRequestInf(request, response)
        return response
    }

    /**
     * 获取请求参数
     */
    private fun getRequestParams(request: Request): String {
        var paramsStr = ""
        runCatching {
            var charset = utf8
            val requestBody = request.body
            if (requestBody != null) {
                val buffer = Buffer()
                requestBody.writeTo(buffer)
                val contentType = requestBody.contentType()
                contentType?.let {
                    charset = contentType.charset(utf8)
                }
                paramsStr = buffer.readString(charset)
            }
        }.onFailure {
            paramsStr = ""
        }
        return paramsStr
    }

    /**
     * 获取请求返回的数据
     */
    private fun getResponseData(response: Response): String {
        var responseData = ""
        runCatching {
            var charset = utf8
            val responseBody = response.body
            if (responseBody != null) {
                val source = responseBody.source()
                source.request(java.lang.Long.MAX_VALUE)
                val buffer = source.buffer()
                val contentType = responseBody.contentType()
                contentType?.let {
                    charset = contentType.charset(utf8)
                }
                responseData = buffer.clone().readString(charset)
            }
        }.onFailure {
            responseData = ""
        }
        return responseData
    }

    /**
     * 画线
     */
    private fun printLine(isTop: Boolean) {
        if (isTop) {
            debugLog("╔═══════════════════════════════════════════════════════════════════════════════════════")
        } else {
            debugLog("╚═══════════════════════════════════════════════════════════════════════════════════════")
        }
    }


    /**
     * 数据格式化
     */
    private fun jsonFormat(startStr: String, msg: String) {
        runCatching {
            var message = try {
                when {
                    msg.startsWith("{") -> {
                        val jsonObject = JSONObject(msg)
                        jsonObject.toString(4)
                    }
                    msg.startsWith("[") -> {
                        val jsonArray = JSONArray(msg)
                        jsonArray.toString(4)
                    }
                    else -> {
                        msg
                    }
                }
            } catch (e: JSONException) {
                msg
            }
            lineSeparator?.let { lineSeparator ->
                message = "${startStr}:$lineSeparator${message}"
                val lines = message.split(lineSeparator.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (line in lines) {
                    debugLog("║ $line")
                }
            }
        }
    }

    /**
     * 打印请求信息
     */
    private fun printRequestInf(request: Request, response: Response) {
        runCatching {
            printLine(true)
            debugLog("║ 请求方式：" + request.method)
            debugLog("\n║ 请求url：" + request.url)
            debugLog("\n║ 请求头：" + request.headers)
            jsonFormat("请求参数body", getRequestParams(request))
            debugLog("\n║ 收到响应: code = " + response.code)
            jsonFormat("请求到的数据", getResponseData(response))
            printLine(false)
        }
    }

    private fun debugLog(log:String){
        Log.d(this::class.java.simpleName,log)
    }
}