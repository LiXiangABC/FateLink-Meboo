package com.crush.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Criteria
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.provider.Settings
import android.util.Log
import com.crush.App
import com.crush.bean.ScanWifiBean
import com.custom.base.manager.SDActivityManager
import com.google.gson.JsonObject
import io.rong.imkit.activity.Activities
import java.io.IOException
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


object SystemUtils {
    private fun getWifi(mActivity: Activity): String {
        if (mActivity.isDestroyed){
            return ""
        }
        val wifiManager: WifiManager = mActivity.getSystemService(
            Context.WIFI_SERVICE
        ) as WifiManager
        val wifiInfo: WifiInfo = wifiManager.connectionInfo
        return wifiInfo.bssid
    }

    @SuppressLint("MissingPermission")
    fun getWifiList(mActivity: Activity): List<ScanWifiBean> {
        App.appInterface?.apply {
            val wifiManager =
                App.appInterface?.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val scanWifiList = wifiManager.scanResults
            val wifiList: MutableList<ScanWifiBean> = ArrayList()
            val myBssid = getWifi(mActivity)
            if (scanWifiList != null && scanWifiList.size > 0) {
                for (i in scanWifiList.indices) {
                    val scanResult = scanWifiList[i]
                    if (scanResult.SSID.isNotEmpty()) {
                        val scanWifiBean = ScanWifiBean(
                            scanResult.SSID,
                            scanResult.BSSID,
                            if (myBssid == scanResult.BSSID) 0 else 1
                        )
                        wifiList.add(scanWifiBean)
                    }
                }
            }
            return wifiList
        }
       return arrayListOf()
    }

    fun isWifiEnabled(context: Context?): Boolean {
        if (context == null) {
            throw NullPointerException("Global context is null")
        }
        val wifiMgr = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return if (wifiMgr.wifiState == WifiManager.WIFI_STATE_ENABLED) {
            val connManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val wifiInfo = connManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            wifiInfo!!.isConnected
        } else {
            false
        }
    }

    /**
     * 判断网络是否连接
     */
    fun isConnected(): Boolean {
        val manager = Activities.get().top?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (manager != null) {
            val info = manager.activeNetworkInfo
            if (info != null && info.isConnected) {
                if (info.state == NetworkInfo.State.CONNECTED) {
                    return true
                }
            }
        }
        return false
    }


    @SuppressLint("MissingPermission")
    fun getGPS(context: Activity):JsonObject? {
        if (!PermissionUtils.lacksPermission(Manifest.permission.ACCESS_FINE_LOCATION) || !PermissionUtils.lacksPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            // 获取位置管理服务
            val locationManager: LocationManager
            val serviceName = Context.LOCATION_SERVICE
            locationManager = context.getSystemService(serviceName) as LocationManager

            // 查找到服务信息
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_FINE // 高精度
            criteria.isAltitudeRequired = false
            criteria.isBearingRequired = false
            criteria.isCostAllowed = true
            criteria.powerRequirement = Criteria.POWER_LOW // 低功耗
            var location: Location? = null
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                location =
                    locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) // 通过GPS获取位置
                if (location == null) {
                    if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        location =
                            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) // 通过NETWORK获取位置
                    }
                }
                return showLocationCity(location,context)
            } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                location =
                    locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) // 通过NETWORK获取位置
                return showLocationCity(location,context)
            } else {
                return null
            }
        }else{
            val jsonObject = JsonObject()
            jsonObject.addProperty("lng",0.0)
            jsonObject.addProperty("lat",0.0)
            return jsonObject
        }

    }

    @SuppressLint("MissingPermission")
    fun getRefreshGPS(context: Activity):JsonObject? {
        if (!PermissionUtils.lacksPermission(Manifest.permission.ACCESS_FINE_LOCATION) || !PermissionUtils.lacksPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            // 获取位置管理服务
            val locationManager: LocationManager
            val serviceName = Context.LOCATION_SERVICE
            locationManager = context.getSystemService(serviceName) as LocationManager

            // 查找到服务信息
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_FINE // 高精度
            criteria.isAltitudeRequired = false
            criteria.isBearingRequired = false
            criteria.isCostAllowed = true
            criteria.powerRequirement = Criteria.POWER_LOW // 低功耗
            var location: Location? = null
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                location =
                    locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) // 通过GPS获取位置
                if (location == null) {
                    if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        location =
                            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) // 通过NETWORK获取位置
                    }
                }
                return showLocationCity(location,context)
            } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                location =
                    locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) // 通过NETWORK获取位置
                return showLocationCity(location, context)
            } else {
                val intent = Intent(Settings.ACTION_SECURITY_SETTINGS)
                context.startActivityForResult(intent, 0)
                return null

            }
        }else{
            val jsonObject = JsonObject()
            jsonObject.addProperty("lng",0.0)
            jsonObject.addProperty("lat",0.0)
            return jsonObject
        }

    }

    private fun showLocation(location: Location?): JsonObject? {
        /*latitude = location.getLatitude();     //经度
	    	longitude = location.getLongitude(); //纬度
	    	altitude =  location.getAltitude();     //海拔
	    	 */
        location?.apply {
            val jsonObject = JsonObject()
            jsonObject.addProperty("lng",longitude)
            jsonObject.addProperty("lat",latitude)
            return jsonObject
        }
        return null
    }
    private fun showLocationCity(location: Location?, context: Activity): JsonObject? {
        /*latitude = location.getLatitude();     //经度
	    	longitude = location.getLongitude(); //纬度
	    	altitude =  location.getAltitude();     //海拔
	    	 */
        location?.apply {
            var city= ""
            var state= ""
            val geocoder = Geocoder(context, Locale.getDefault())
            try {
                val addresses: MutableList<Address>? = geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1
                )
                if (addresses != null) {
                    if (addresses.isNotEmpty()) {
                        city = addresses[0].locality?:""
                        state = addresses[0].adminArea?:""
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val jsonObject = JsonObject()
            jsonObject.addProperty("city",city)
            jsonObject.addProperty("state",state)
            jsonObject.addProperty("lng",longitude)
            jsonObject.addProperty("lat",latitude)
            return jsonObject
        }
        return null
    }

    fun calculateDistance(context: Context,lat1: Double, lon1: Double, lat2: Double, lon2: Double): String {
        if (!PermissionUtils.lacksPermission(Manifest.permission.ACCESS_FINE_LOCATION) || !PermissionUtils.lacksPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            val earthRadius = 6371 // 地球半径，单位为千米
            val dLat = Math.toRadians(lat2 - lat1)
            val dLon = Math.toRadians(lon2 - lon1)

            val a = sin(dLat / 2) * sin(dLat / 2) +
                    cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                    sin(dLon / 2) * sin(dLon / 2)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))

            val number = earthRadius * c
            return if (number > 500){
                "500km+"
            }else {
                Log.e("~~~", "calculateDistance: "+number)
                val roundedNumber = BigDecimal(number).setScale(2, RoundingMode.HALF_UP)
                return "${roundedNumber}km"
            }
        }else{
            return "Nearby"
        }
    }

    fun isNetConnection(mContext: Context?): Boolean {
        if (mContext != null) {
            val connectivityManager =
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            val connected: Boolean = networkInfo?.isConnected ?: false
            if (networkInfo != null && connected) {
                return networkInfo.state === NetworkInfo.State.CONNECTED
            }
        }
        return false
    }

    /**
     * 获取当前app的升级版本号
     *
     * @param context 上下文
     */
    fun getVersionCode(context: Context): Int {
        return try {
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionCode
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            1
        }
    }
    /**
     * 获取当前app的版本号
     *
     * @param context 上下文
     */
    fun getVersionName(context: Context): String? {
        return try {
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: Exception) {
            e.printStackTrace()
            "1.0.0"
        }
    }

    /**
     * 获取app名字
     *
     * @param context 上下文
     */
    fun getAppName(context: Context): String? {
        try {
            val packageManager = context.packageManager
            val applicationInfo = packageManager.getApplicationInfo(context.packageName, 0)
            return packageManager.getApplicationLabel(applicationInfo) as String
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return ""
    }

    fun isBackground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses
        for (appProcess in appProcesses) {
            if (appProcess.processName == context.packageName) {
                return appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND
            }
        }
        return false
    }

}