package com.crush.entity

data class BlindBoxInfoEntity(
    val code: Int, // 200
    val data: Data,
    val msg: String // 操作成功
) {
    data class Data(
        val ambiguousCount: Int,//暧昧次数
        val haveBindBox: Int,//是否需要开盲盒 0否 1是
        val haveFlowExposure: Int,//是否流量曝光 0否 1是
        val homePageLimit: Int,//首页无限划卡 0否 1是
        val homePageLimitTime: Long,//首页无限划卡到期时间
    )
}