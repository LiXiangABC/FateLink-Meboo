package io.rong.imkit.utils

import io.rong.dot.DotInit

object FirebaseEventUtils {
    fun logEvent(name:String){
        DotInit.listenet?.execute(name)
    }
}