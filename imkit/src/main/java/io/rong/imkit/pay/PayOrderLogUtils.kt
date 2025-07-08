package io.rong.imkit.pay

import android.text.TextUtils
import com.custom.base.config.BaseConfig
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import io.rong.imkit.API
import io.rong.imkit.SpName
import io.rong.imkit.entity.BaseEntity
import org.json.JSONObject

/**
 * 订单日志上报
 */
object PayOrderLogUtils {

    const val TO_PRODUCT_DETAIL = "To_Product_Detail"//进行入订阅或一次商品购买页面
    const val PRODUCT_FORM_CONFIRM = "Product_Form_Confirm"//订单购买确认
    const val CLICK_BUY_BUTTON = "Click_Buy_Button"//点击了购买弹窗上的按钮
    const val ORDER_SUBMIT = "Order_Submit"// 订单提交==生成订单接口
    const val PAY_CENTER = "Pay_Center"// 拉起收银台
    const val PAY_CONNECT = "Pay_Connect"//支付连接
    const val PURCHASE_SUCCESS = "Purchase_Success"//支付成功
    const val PURCHASE_CANCEL = "Purchase_Cancel"//支付取消
    const val PURCHASE_FAILED = "Purchase_Failed"//支付失败

    /**
     * @param eventId 事件Id
     * @param userCode 用户的usercode
     * @param productCode 生成订单里面的产品code
     * @param orderNo 创建订单的订单号
     * @param orderId google订单号
     * @param eventCode 事件名称
     * @param snapshotContent
     * */
    fun requestOrderLog(
        productCode: String?,
        orderNo: String?,
        orderId: String?,
        eventCode: String?,
        payResult: String? = null
    ) {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(API.order_log_url)
                requestBody.add("eventId", createEventId())
                requestBody.add("userCode", BaseConfig.getInstance.getString(SpName.userCode, ""))
                if (!TextUtils.isEmpty(productCode)) {
                    requestBody.add("productCode", productCode ?: "")
                }
                if (!TextUtils.isEmpty(orderNo)) {
                    requestBody.add("orderNo", orderNo ?: "")
                }
                if (!TextUtils.isEmpty(orderId)) {
                    requestBody.add("orderId", orderId ?: "")
                }
                if (!TextUtils.isEmpty(eventCode)) {
                    requestBody.add("eventCode", eventCode ?: "")
                    requestBody.add("eventName", eventCode ?: "")
                }
                if (!TextUtils.isEmpty(payResult)) {
                    requestBody.add("snapshotContent", createSnapshotContent(payResult ?: ""))
                }
            }
        }, object : SDOkHttpResoutCallBack<BaseEntity>() {
            override fun onSuccess(entity: BaseEntity) {

            }

            override fun onFailure(code: Int, msg: String) {
            }
        })
    }

    fun createSnapshotContent(payResult: String): String {
        val jsonObject = JSONObject()
        jsonObject.put("payResult", payResult)
        return jsonObject.toString()
    }

    private fun createEventId(): String {
        //调用支付成功或者失败之后、还未拉起收银台弹窗关闭、杀进程都需要清除这个sp，下次再重新生成
        return if (TextUtils.isEmpty(BaseConfig.getInstance.getString(SpName.orderEventId, ""))) {
            val orderEventId = "pay-${System.currentTimeMillis()}"
            BaseConfig.getInstance.setString(
                SpName.orderEventId,
                "pay-${System.currentTimeMillis()}"
            )
            orderEventId
        } else {
            BaseConfig.getInstance.getString(SpName.orderEventId, "")
        }
    }

}