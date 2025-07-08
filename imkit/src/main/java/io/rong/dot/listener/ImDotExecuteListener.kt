package io.rong.dot.listener

import org.json.JSONObject

interface ImDotExecuteListener {
    fun execute(eventName:String,jsonObject: JSONObject?=null){

    }
}