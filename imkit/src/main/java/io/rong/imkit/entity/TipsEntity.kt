package io.rong.imkit.entity

data class TipsEntity(
    val contents: ArrayList<String>,
    val title: String,
    var show: Boolean,
    var conversationType: Int
)