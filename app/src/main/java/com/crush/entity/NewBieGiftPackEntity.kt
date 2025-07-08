package com.crush.entity

/**
 * @Author ct
 * @Date 2024/5/11 11:23
 * 新手礼包弹窗
 */
data class NewBieGiftPackEntity(
    val code: Int, // 200
    val data: Data,
    val msg: String // 操作成功
) {
    data class Data(
        val backgroundImg:String? = null,//背景图片
        val benefitsInfo: BenefitsInfo? = null,//赠送PP卡片
        val carousel: List<Carousel> = ArrayList(),//顶部轮播图
        val discountTag: String? = null,//折扣标签
        val memberInfo: MemberInfo? = null,//会员卡片
        val newcomerPrice: String? = null,//新手价
        val currencyType: String? = null,//新手价
        val normalPrice: String? = null,//正常价
        val notice: String? = null,//小字说明
        val payInfo: PayInfo? = null,//底部支付相关注释，支持底部区域文案上下滑动
        val productCode: String? = null,//谷歌商品ID
        val productCategory: String? = null,//谷歌商品分类
        val subtitle: String? = null,//小字
        val title: String? = null//标题
    ) {
        data class BenefitsInfo(
            val icon: String? = null,
            val name: String? = null,
            val num: String? = null
        )

        data class Carousel(
            val img:String?=null,
            val title:String?=null,
            val content:String?=null
        )

        data class MemberInfo(
            val price: String? = null,
            val remark: String? = null
        )

        data class PayInfo(
            val content: String? = null,
            val title: String? = null
        )
    }
}