package com.crush.util

import android.app.Activity
import android.util.Log

/**
 * 作者：
 * 日期：2022/3/22
 * 说明：定位
 */
class LocationUtil {
    /**
     * 初始化定位参数配置
     */
//    fun initLocationOption(context: Activity, locationListener: OnLocationListener, scanSpan:Int = 0): LocationClient? {
//        try {
//            LocationClient.setAgreePrivacy(true)
//            //定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动
//            val locationClient = LocationClient(context)
//            //声明LocationClient类实例并配置定位参数
//            val locationOption = LocationClientOption()
//            val myLocationListener = MyLocationListener(scanSpan,locationListener, locationClient)
//            //注册监听函数
//            locationClient.registerLocationListener(myLocationListener)
//            //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
//            locationOption.locationMode = LocationClientOption.LocationMode.Hight_Accuracy
//            //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
//            locationOption.setCoorType("bd09ll")
//            //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
//            locationOption.setScanSpan(scanSpan)
//            //可选，设置是否需要地址信息，默认不需要
//            locationOption.setIsNeedAddress(true)
//            //可选，设置是否需要地址描述
//            locationOption.setIsNeedLocationDescribe(true)
//            //可选，设置是否需要设备方向结果
//            locationOption.setNeedDeviceDirect(false)
//            //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
//            locationOption.isLocationNotify = true
//            //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
//            locationOption.setIgnoreKillProcess(true)
//            //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
//            locationOption.setIsNeedLocationDescribe(true)
//            //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
//            locationOption.setIsNeedLocationPoiList(false)
//            //可选，默认false，设置是否收集CRASH信息，默认收集
//            locationOption.SetIgnoreCacheException(false)
//            //可选，默认false，设置是否开启Gps定位
//            locationOption.isOpenGps = true
//            //可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
//            locationOption.setIsNeedAltitude(false)
//            if(scanSpan!=0){
//                //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
//                locationOption.setOpenAutoNotifyMode();
//                //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
//                locationOption.setOpenAutoNotifyMode(3000,1, LocationClientOption.LOC_SENSITIVITY_HIGHT);
//            }
//            //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
//            locationClient.locOption = locationOption
//            //开始定位
//            locationClient.start()
//            return locationClient
//        } catch (e: Exception) {
//            e.printStackTrace()
//            locationListener.result(null)
//        }
//        return null
//    }

    /**
     * 实现定位回调
     */
//    class MyLocationListener(var scanSpan:Int, var locationListener: OnLocationListener, var locationClient: LocationClient) :
//        BDAbstractLocationListener() {
//        override fun onReceiveLocation(location: BDLocation) {
//            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
//            //以下只列举部分获取经纬度相关（常用）的结果信息
//            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
//            Log.e("MyLocationListener", location.toString())
//            locationListener.result(location)
//            if(scanSpan == 0) locationClient.stop()
////            //获取纬度信息
////            double latitude = location.getLatitude();
////            //获取经度信息
////            double longitude = location.getLongitude();
////            //获取定位精度，默认值为0.0f
////            float radius = location.getRadius();
////            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
////            String coorType = location.getCoorType();
////            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
////            int errorCode = location.getLocType();
//        }
//
//    }

    interface OnLocationListener {
//        fun result(location: BDLocation?)
    }
}