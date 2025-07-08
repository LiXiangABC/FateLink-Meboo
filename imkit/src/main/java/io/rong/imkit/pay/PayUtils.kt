package io.rong.imkit.pay

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ConsumeResponseListener
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.Purchase.PurchaseState
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.sunday.eventbus.SDEventManager
import io.rong.common.CollectionUtils
import io.rong.imkit.API
import io.rong.imkit.activity.Activities
import io.rong.imkit.dialog.MemberBuySingleSuccessDialog
import io.rong.imkit.dialog.MemberBuySuccessDialog
import io.rong.imkit.entity.BaseEntity
import io.rong.imkit.entity.OrderCreateEntity
import io.rong.imkit.event.EnumEventTag
import io.rong.imkit.http.HttpRequest
import io.rong.imkit.widget.Loading.LoadingDialog


class PayUtils {
    private var activity: FragmentActivity? = Activities.get().top
    private var context: Context? = null
    private var mBillingResult: BillingResult? = null
    private var startConnectionNumber = 0
    private var data: OrderCreateEntity? = null
    private var connectionState = -1
    private var targetID = ""
    private var orderPayGoogleId: String? = null
    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        //购买结果监听
        when (billingResult.responseCode) {
            BillingResponseCode.OK -> {
                onPurchasesUpdated(billingResult, purchases)
                if (this.data != null) {
//                    if (!this.data!!.data.orderPayFailedSwitch) {
//                        //开关关闭的情况下走老逻辑
//                        SDEventManager.post(EnumEventTag.CLOSE_PAY_BUY_DIALOG.ordinal)
//                    }
                    PayOrderLogUtils.requestOrderLog(
                        this.data!!.data.productCode,
                        this.data!!.data.orderNo,
                        orderPayGoogleId,
                        PayOrderLogUtils.PURCHASE_SUCCESS, billingResult.toString()
                    )
                }

            }

            BillingResponseCode.BILLING_UNAVAILABLE -> {
//                if (this.data != null) {
//                    //支付失败的时候关闭新手礼包弹窗
//                    if (this.data!!.data.purchaseTypeExt == 2) {
//                        SDEventManager.post(data, EnumEventTag.PAY_FAILED_POP_SHOW.ordinal)
//                    }
//                    //如果支付报错弹窗显示开关
//                    if (!this.data!!.data.orderPayFailedSwitch) {
//                        //原来的支付失败显示的弹窗
//                        //SDEventManager.post(data, EnumEventTag.PAY_FAILED_POP_SHOW.ordinal)
//                        PayErrorUtils.showPayFailOldDialog(context, this.data!!)
//                    }
//                }

            }

            BillingResponseCode.USER_CANCELED -> {
                if (this.data != null) {
                    //上报日志
                    PayOrderLogUtils.requestOrderLog(
                        this.data!!.data.productCode,
                        this.data!!.data.orderNo,
                        orderPayGoogleId,
                        PayOrderLogUtils.PURCHASE_CANCEL, billingResult.toString()
                    )
                }
                //如果新手礼包弹窗点击支付然后取消不再触发这个促销
//                if (data != null) {
//                    if (data!!.data.purchaseTypeExt == 2) {
//                        if (data!!.data.orderPayFailedSwitch) {
//                            SDEventManager.post(data, EnumEventTag.PAY_FAILED_POP_SHOW.ordinal)
//                            PayErrorUtils.show(billingResult.responseCode, billingResult, data!!, context)
//                        }
//                        return@PurchasesUpdatedListener
//                    }
//                }
//                SDEventManager.post(3, EnumEventTag.CLOSE_MEMBER_POP.ordinal)
            }

            BillingResponseCode.DEVELOPER_ERROR -> {

            }

            BillingResponseCode.ITEM_ALREADY_OWNED -> {
                data?.apply {
                    val queryPurchasesParams = QueryPurchasesParams.newBuilder()
                        .setProductType(if (this.data.purchaseType == 1) BillingClient.ProductType.INAPP else BillingClient.ProductType.SUBS)
                        .build()

                    billingClient.queryPurchasesAsync(
                        queryPurchasesParams
                    ) { _, p1 ->
                        for (pur in p1) {
                            if (pur.purchaseState == PurchaseState.PURCHASED) {
                                storeToken(pur)
                            }
                        }

                    }
                }

            }
        }

        //非支付成功
        if (billingResult.responseCode != BillingResponseCode.OK) {
            payFail()
        }

        //针对新手礼包支付报错，不会继续走下面
        if (data != null) {
            //这个OK返回是因为上面已经上报日志了且不需要弹出报错弹窗
            if (billingResult.responseCode == BillingResponseCode.OK) {
                Log.i("PayUtils", "billingResult.responseCode == BillingResponseCode.OK")
                //新手礼包购买ok的情况下，关闭新手礼包弹窗
                if (data!!.data.purchaseTypeExt == 2) {
                    SDEventManager.post(data, EnumEventTag.PAY_FAILED_POP_SHOW.ordinal)
                }
                //只要是购买成功就应该关闭所有的购买弹窗，关闭所有购买会员、pp、pv、fc的弹窗
                SDEventManager.post(EnumEventTag.CLOSE_PAY_BUY_DIALOG.ordinal)
                return@PurchasesUpdatedListener
            }
            //这个取消返回是因为上面日志已经上报了，但是针对新手礼包开关逻辑要走这
            if (billingResult.responseCode == BillingResponseCode.USER_CANCELED) {
                Log.i("PayUtils", "billingResult.responseCode == BillingResponseCode.USER_CANCELED")
                //如果新手礼包弹窗点击支付然后取消不再触发这个促销，其他的购买逻辑取消要正常走促销
                if (data!!.data.purchaseTypeExt == 2) {
                    //支付优化弹窗开关 开的时候，显示支付报错弹窗，并且关闭新手礼包弹窗
                    if (data!!.data.orderPayFailedSwitch) {
                        //关闭新手礼包弹窗
                        SDEventManager.post(data, EnumEventTag.PAY_FAILED_POP_SHOW.ordinal)
                        //显示支付报错弹窗，这一层最好还是显示在外面所以传入activity
                        Activities.get().top?.let {
                            if (it.isDestroyed || it.isFinishing){
                                return@let
                            }
                            PayErrorUtils.show(
                                billingResult.responseCode,
                                billingResult,
                                data!!,
                                it
                            )
                        }
                    }
                    return@PurchasesUpdatedListener
                }
                //弹窗开关关闭情况：显示购买弹窗，但是取消支付，促销正常下发，需要关闭购买弹窗，显示促销
                //弹窗开关开启情况：显示购买弹窗，取消支付，显示报错提示弹窗，应该是先关闭报错弹窗提示再显示促销
                if (!data!!.data.orderPayFailedSwitch) {
                    //关闭购买弹窗
                    SDEventManager.post(EnumEventTag.CLOSE_PAY_BUY_DIALOG.ordinal)
                    //下发促销弹窗
                    SDEventManager.post(3, EnumEventTag.CLOSE_MEMBER_POP.ordinal)
                }
            }
            //如果支付失败走BILLING_UNAVAILABLE情况
            if (billingResult.responseCode == BillingResponseCode.BILLING_UNAVAILABLE) {
                Log.i(
                    "PayUtils",
                    "billingResult.responseCode == BillingResponseCode.BILLING_UNAVAILABLE"
                )
                //针对新手礼包支付失败的情况，需要关闭新手礼包弹窗
                if (this.data!!.data.purchaseTypeExt == 2) {
                    SDEventManager.post(data, EnumEventTag.PAY_FAILED_POP_SHOW.ordinal)
                }
                //如果开关是关的 所有的购买失败都走原来的失败弹窗逻辑
                if (!this.data!!.data.orderPayFailedSwitch) {
                    PayErrorUtils.showPayFailOldDialog(context, this.data!!)
                }
            }
            //除了新手礼包其他购买，对支付结果进行一个报错提示 关闭的情况下不显示
            if (data!!.data.orderPayFailedSwitch) {
                //开关是开的情况
                PayErrorUtils.show(billingResult.responseCode, billingResult, data!!, context)
            }
            //除了取消和成功 剩下的支付失败都上报
            if (billingResult.responseCode == BillingResponseCode.OK) return@PurchasesUpdatedListener
            if (billingResult.responseCode == BillingResponseCode.USER_CANCELED) return@PurchasesUpdatedListener
            PayOrderLogUtils.requestOrderLog(
                this.data!!.data.productCode,
                this.data!!.data.orderNo,
                orderPayGoogleId,
                PayOrderLogUtils.PURCHASE_FAILED, billingResult.toString()
            )
        }


    }

    private var billingClient: BillingClient = BillingClient.newBuilder(activity!!)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()
    private var callBack: EmptySuccessCallBack? = null
    private var mEntity: OrderCreateEntity? = null
    private val billingClientStateListener = object : BillingClientStateListener {

        override fun onBillingSetupFinished(billingResult: BillingResult) {
            if (this@PayUtils.data != null) {
                //对连接结果进行上报
                PayOrderLogUtils.requestOrderLog(
                    this@PayUtils.data!!.data.productCode,
                    this@PayUtils.data!!.data.orderNo,
                    orderPayGoogleId,
                    PayOrderLogUtils.PAY_CONNECT,
                    billingResult.toString()
                )
            }
            mBillingResult = billingResult
            //如果在调用支付时处于连接中，在得到结果为成功时，且支付已经准备完成，调起商品获取
            if (connectionState == BillingClient.ConnectionState.CONNECTING && billingResult.responseCode == BillingResponseCode.OK) {
                if (billingClient.isReady) {
                    data?.let {
                        connectionState = -1
                        getProductDetail(it)
                    }
                }
            } else {
                showPayLoading(false)
                //对连接结果进行一个报错提示
                if (data != null) {
                    //orderPayGoogleId = ""
                    //针对新手礼包拉起收银台失败的情况，需要关闭新手礼包弹窗，显示支付报错弹窗

                    if (data!!.data.orderPayFailedSwitch) {
                        //开关是开的情况
                        if (data!!.data.purchaseTypeExt == 2) {
                            SDEventManager.post(data, EnumEventTag.PAY_FAILED_POP_SHOW.ordinal)
                            Activities.get().top?.let {

                                if (it.isDestroyed || it.isFinishing){
                                    return@let
                                    //针对新手礼包显示必须在activity层面,要不然遮挡
                                    PayErrorUtils.show(
                                        billingResult.responseCode,
                                        billingResult,
                                        data!!,
                                        it
                                    )
                                }
                            }

                        } else {
                            //其他购买弹窗连接报错正常走显示报错弹窗逻辑
                            PayErrorUtils.show(
                                billingResult.responseCode,
                                billingResult,
                                data!!,
                                context
                            )
                        }
                        Log.i("PayErrorUtils", "onBillingSetupFinished $connectionState")
                    }
                }
            }
            HttpRequest.commonNotify(311, billingResult.toString())
        }

        override fun onBillingServiceDisconnected() {
            HttpRequest.commonNotify(311, "connected failed")
            // Try to restart the connection on the next request to
            // Google Play by calling the startConnection() method.
            if (startConnectionNumber < 3) {
                billingClient.startConnection(this)
                startConnectionNumber++
            } else {
                showPayLoading(false)
            }
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var mInstance: PayUtils? = null

        val instance: PayUtils
            get() {
                if (null == mInstance) {
                    synchronized(PayUtils::class.java) {
                        if (null == mInstance) {
                            mInstance = PayUtils()
                        }
                    }
                }
                return mInstance!!
            }
    }

    init {
        billingClient.startConnection(billingClientStateListener)
    }


    fun start(
        entity: OrderCreateEntity,
        context: Context,
        callBack: EmptySuccessCallBack,
        targetID: String = ""
    ) {
        this.targetID = targetID
        this.context = context
        this.data = entity
        this.callBack = callBack
        //因为这个谷歌订单id需要进行上报、所以每次进来之前先置为空
        orderPayGoogleId = ""
        //todo 为了方便快捷测试根据google error信息显示报错弹窗不同的场景
        //PayErrorUtils.show(1,BillingResult.newBuilder().setResponseCode(1).setDebugMessage("Response Code: USER_CANCELED, Debug Message: The form of payment being used to make this purchase is not valid.").build(),entity,context)
        showPayLoading(true)
        when (billingClient.connectionState) {
            BillingClient.ConnectionState.DISCONNECTED,
            BillingClient.ConnectionState.CLOSED -> {
                PayOrderLogUtils.requestOrderLog(
                    entity.data.productCode,
                    entity.data.orderNo,
                    "",
                    PayOrderLogUtils.ORDER_SUBMIT,
                    "BillingClient.ConnectionState.DISCONNECTED OR CLOSED:${billingClient.connectionState}"
                )
                //处于未连接或者连接关闭时，重新发起连接
                connectionState = BillingClient.ConnectionState.CONNECTING
                startConnectionNumber = 0
                billingClient.startConnection(billingClientStateListener)
            }

            BillingClient.ConnectionState.CONNECTING -> {
                PayOrderLogUtils.requestOrderLog(
                    entity.data.productCode,
                    entity.data.orderNo,
                    "",
                    PayOrderLogUtils.ORDER_SUBMIT,
                    "BillingClient.ConnectionState.CONNECTING:${billingClient.connectionState}"
                )
                //处于连接中的时候，记得当前状态，为连接完成做判断
                connectionState = BillingClient.ConnectionState.CONNECTING
            }

            BillingClient.ConnectionState.CONNECTED -> {
                PayOrderLogUtils.requestOrderLog(
                    entity.data.productCode,
                    entity.data.orderNo,
                    "",
                    PayOrderLogUtils.ORDER_SUBMIT,
                    "BillingClient.ConnectionState.CONNECTED:${billingClient.connectionState}"
                )
                if (billingClient.isReady) {
                    // The BillingClient is ready. You can query purchases here.
                    getProductDetail(entity)
                }
            }
        }
    }

    fun payCenterDetail(billingResult: BillingResult){
        //测试环境模拟了一个network 显示通用报错信息弹窗，上报
        if (this.data != null) {
            //show dialog
            if (this.data!!.data.orderPayFailedSwitch) {
                //拉起收银台不ok的时候
                if (billingResult.responseCode != BillingResponseCode.OK) {
                    PayErrorUtils.show(
                        billingResult.responseCode,
                        billingResult,
                        this.data!!,
                        context
                    )
                }
            }
            PayOrderLogUtils.requestOrderLog(
                this.data!!.data.productCode,
                this.data!!.data.orderNo,
                orderPayGoogleId,
                PayOrderLogUtils.PAY_CENTER,
                billingResult.toString()
            )
        }
    }


    fun handleBillingResult(type: Int, billingResult: BillingResult) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.SERVICE_DISCONNECTED) {
            billingClient.startConnection(billingClientStateListener)
        }
        HttpRequest.commonNotify(type, billingResult.toString())
    }

    /**
     * 获取商品详情
     */
    private fun getProductDetail(entity: OrderCreateEntity) {
        //当谷歌收银台因版本过低无法获取到商品详情时，向下兼容获取商品
        val featureSupported =
            billingClient.isFeatureSupported(BillingClient.FeatureType.PRODUCT_DETAILS)
        handleBillingResult(315, featureSupported)
        if (featureSupported.responseCode == BillingResponseCode.FEATURE_NOT_SUPPORTED) {
            getOldProductDetail(entity)
            return
        }
        mEntity = entity
        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder().setProductList(
                mutableListOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(if (entity.data.purchaseType == 1) entity.data.productCode else entity.data.purchaseProductName)
                        .setProductType(if (entity.data.purchaseType == 1) BillingClient.ProductType.INAPP else BillingClient.ProductType.SUBS)
                        .build()
                )
            ).build()
        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult,
                                                                            productDetailsList ->
            if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                handleBillingResult(312, billingResult)
            } else {
                bugProduct(productDetailsList, entity)
            }
            payCenterDetail(billingResult)
            showPayLoading(false)
        }
    }

    /**
     * 向下兼容消耗性产品
     */
    private fun getOldProductDetail(entity: OrderCreateEntity) {
        mEntity = entity
        val queryProductDetailsParams =
            SkuDetailsParams.newBuilder().setSkusList(
                mutableListOf(
                    if (entity.data.purchaseType == 1) entity.data.productCode else entity.data.purchaseProductName
                )
            )
                .setType(if (entity.data.purchaseType == 1) BillingClient.ProductType.INAPP else BillingClient.ProductType.SUBS)
                .build()
        billingClient.querySkuDetailsAsync(queryProductDetailsParams) { billingResult,
                                                                        productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                productDetailsList?.let { buyOldProduct(it, entity) }
            }
            payCenterDetail(billingResult)
            showPayLoading(false)
        }
    }

    /**
     * 购买商品
     */
    private fun bugProduct(
        productDetailsList: List<ProductDetails>,
        entity: OrderCreateEntity
    ) {
        if (com.google.android.gms.common.util.CollectionUtils.isEmpty(productDetailsList)) {
            HttpRequest.commonNotify(312, "Product Details List is empty")
        }
        // check billingResult
        // process returned productDetailsList
        if (!CollectionUtils.checkNullOrEmptyOrContainsNull(productDetailsList)) {
            val productDetailsParamsList: List<BillingFlowParams.ProductDetailsParams>
            //获取订阅信息
            if (productDetailsList[0].subscriptionOfferDetails != null) {
                var offerToken = ""
                for (i in 0 until (productDetailsList[0].subscriptionOfferDetails?.size
                    ?: 0)) {
                    if (entity.data.discountCode != null) {//当服务端给到优惠ID时，取优惠id和产品code都相等的值
                        if (productDetailsList[0].subscriptionOfferDetails!![i].basePlanId == entity.data.productCode && productDetailsList[0].subscriptionOfferDetails!![i].offerId == entity.data.discountCode) {
                            offerToken =
                                productDetailsList[0].subscriptionOfferDetails!![i].offerToken
                        }
                    } else {//没有优惠ID，直接去产品code相等的值
                        if (productDetailsList[0].subscriptionOfferDetails!![i].basePlanId == entity.data.productCode) {
                            offerToken =
                                productDetailsList[0].subscriptionOfferDetails!![i].offerToken

                        }
                    }

                }
                productDetailsParamsList = listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                        .setProductDetails(productDetailsList[0])
                        // to get an offer token, call ProductDetails.subscriptionOfferDetails()
                        // for a list of offers that are available to the user
                        .setOfferToken(offerToken)
                        .build()
                )
            } else {
                //购买单个产品
                productDetailsParamsList = listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetailsList[0])
                        .build()
                )
            }
            Log.e("~~~", "bugProduct: ${mEntity?.data}")
            val billingFlowParams: BillingFlowParams =
                if (mEntity?.data?.latestPaidOrderPurchaseToken != null) {
                    BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(productDetailsParamsList)
                        .setSubscriptionUpdateParams(
                            BillingFlowParams.SubscriptionUpdateParams.newBuilder()
                                .setOldPurchaseToken(mEntity?.data?.latestPaidOrderPurchaseToken!!)
                                .setReplaceSkusProrationMode(BillingFlowParams.ProrationMode.IMMEDIATE_AND_CHARGE_FULL_PRICE)
                                .build()
                        )
                        .build()
                } else {
                    BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(productDetailsParamsList)
                        .build()
                }
            val billingResult =
                activity?.let { billingClient.launchBillingFlow(it, billingFlowParams) }
            if (billingResult?.responseCode != BillingResponseCode.OK) {
                billingResult?.let { handleBillingResult(313, it) }
            }
        }
    }

    /**
     * 购买商品
     */
    private fun buyOldProduct(
        skuDetails: List<SkuDetails>,
        entity: OrderCreateEntity
    ) {
        if (com.google.android.gms.common.util.CollectionUtils.isEmpty(skuDetails)) {
            HttpRequest.commonNotify(312, "Product Details List is empty")
        }
        // check billingResult
        // process returned productDetailsList
        if (!CollectionUtils.checkNullOrEmptyOrContainsNull(skuDetails)) {
            val billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails[0])
                .build()
            val billingResult =
                activity?.let { billingClient.launchBillingFlow(it, billingFlowParams) }
            if (billingResult?.responseCode != BillingResponseCode.OK) {
                billingResult?.let {
                    handleBillingResult(313, it)
                }
            }
        }
    }

    fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else {
            if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                handleBillingResult(315, billingResult)
            }
        }
    }

    var requestNum = 0

    fun handlePurchase(purchase: Purchase) {
        orderPayGoogleId = purchase.orderId
        if (mEntity?.data?.purchaseType == 1) {
            val consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            val listener = ConsumeResponseListener { billingResult, purchaseToken ->
                if (billingResult.responseCode == BillingResponseCode.OK) {
                    requestNum = 0
                    storeToken(purchase)
                    SDEventManager.post(mEntity, EnumEventTag.PAY_RESULT.ordinal)
                }
            }
            billingClient.consumeAsync(consumeParams, listener)
        } else {
            if (purchase.purchaseState === PurchaseState.PURCHASED) {
                if (!purchase.isAcknowledged) {
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                        if (billingResult.responseCode == BillingResponseCode.OK) {
                            storeToken(purchase)
                            SDEventManager.post(mEntity, EnumEventTag.PAY_RESULT.ordinal)
                        }
                    }
                }
            }
        }
    }


    private fun storeToken(purchase: Purchase) {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(API.user_order_token_url)
                requestBody.add("purchase", purchase.originalJson)
                requestBody.add("orderNoTmp", mEntity?.data!!.orderNo)
                requestBody.add("modelUserCode", targetID)
            }

        }, object : SDOkHttpResoutCallBack<OrderCreateEntity>(false) {
            override fun onSuccess(entity: OrderCreateEntity) {
                if (mEntity?.data?.purchaseType == 1) {
                    if (mEntity?.data?.purchaseTypeExt == 2) {
                        //走新手礼包购买成功回调
                        callBack?.OnSuccessListener()
                        return
                    }
                    MemberBuySingleSuccessDialog(
                        context!!,
                        mEntity?.data!!.productCategory,
                        mEntity?.data!!.benefitNum,
                        object : MemberBuySingleSuccessDialog.ChangeMembershipListener {
                            override fun onListener() {
                                callBack?.OnSuccessListener()
                            }
                        }).showPopupWindow()
                } else {
                    SDEventManager.post(EnumEventTag.REFRESH_GET_FLASH_DATA.ordinal)
                    MemberBuySuccessDialog(
                        context!!,
                        object :
                            MemberBuySuccessDialog.ChangeMembershipListener {
                            override fun onListener() {
                                callBack?.OnSuccessListener()
                            }

                        }).showPopupWindow()
                }

            }

            override fun onFailure(code: Int, msg: String) {
                if (requestNum < 6) {
                    storeToken(purchase)
                    requestNum++
                }
            }
        })
    }
    private fun payFail() {
        try {
            if (targetID!= "") {
                OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                    override fun addBody(requestBody: OkHttpBodyEntity) {
                        requestBody.setPost(API.pay_fail_notice_url)
                        requestBody.add("modelCode", targetID)
                        mEntity?.data?.productCategory?.let { requestBody.add("productCategory", it) }
                    }

                }, object : SDOkHttpResoutCallBack<BaseEntity>(false) {
                    override fun onSuccess(entity: BaseEntity) {
                    }

                    override fun onFailure(code: Int, msg: String) {
                    }
                })
            }
        }catch (e:Exception){

        }
    }


    //全局支付加载中弹窗
    private fun showPayLoading(isShow: Boolean) {
        Activities.get().top?.let {
            if(it.isDestroyed){
                return
            }
            if (isShow) {
                LoadingDialog.showLoading(it)
            } else {
                LoadingDialog.dismissLoading(it)
            }
        }
    }


}