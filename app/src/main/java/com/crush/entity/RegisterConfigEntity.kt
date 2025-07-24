package com.crush.entity

import com.crush.bean.GenderPreferListBean
import com.crush.bean.RegisterTagBean
import java.io.Serializable

data class RegisterConfigEntity(
    val code: Int, // 200
    val data: Data,
    val msg: String // 操作成功
):Serializable {
    data class Data(
        val configs:List<ConfigsBean>
    ):Serializable
    data class ConfigsBean(
        val additional: String?,//其它提示文案，多个文案采用'|'分隔，body shape选择更多取第一个文案，没有更多选项，取第二个文案 ,
        val backgroundPhoto: String,//背景图，多个背景图采用'|'分隔，性别女取第一张图片，男取第二张图片，queer取第三张图片 ,
        val bodyShapeForMan: ArrayList<GenderPreferListBean>?,//男：body shape选项 ,
        val bodyShapeForWoman: ArrayList<GenderPreferListBean>?,//女：body shape选项 ,
        val firstContent: String?,//1级内容: 无值时不予显示 ,
        val firstTitle: String?,//1级标题：I want页面该字段无值，I want 选项不予显示 ,
        val iwantList: ArrayList<GenderPreferListBean>,
        val nickName: String?,// Nickname页面默认值 ,
        val pageType: Int,//注册页面：1.Age 2.Nickname 3.Gender 4.Photo 5.I want ,
        val secondContent: String?,//2级内容 ,
        val secondTitle: String?,//2级标题：Gender、I want页面，该字段无值时，body shape和You accept不予选择，同时取消至少选择一项控制 ,
        val youAcceptForMan: ArrayList<GenderPreferListBean>?,//男&Queer: You accept选项 ,
        val youAcceptForWomen: ArrayList<GenderPreferListBean>?,//You accept选项
        val interestList: ArrayList<RegisterTagBean>?= arrayListOf()//You accept选项
    ):Serializable
}