package io.rong.dot

import io.rong.dot.listener.ImDotExecuteListener

object DotInit {

    var listenet: ImDotExecuteListener? = null
    fun init(): DotInit {
        return this
    }

    fun setImDotExecuteListener(listenet: ImDotExecuteListener) {
        this.listenet = listenet
    }
}