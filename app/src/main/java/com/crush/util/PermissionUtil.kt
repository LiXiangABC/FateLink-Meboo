package com.crush.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.crush.BuildConfig
import com.crush.dialog.RequestPermissionDialog
import com.crush.util.permission.RxPermissions
import io.reactivex.rxjava3.disposables.Disposable
import io.rong.imkit.activity.Activities

object PermissionUtil {
    private var disposable: Disposable? = null

    fun checkPermission(context: Context,permissions:String):Boolean{
        return ContextCompat.checkSelfPermission(context,permissions) == PackageManager.PERMISSION_DENIED
    }

    fun requestPermissionCallBack(vararg permissions:String,activity: FragmentActivity,type:Int?=null,callback:(Boolean)->Unit){
        val rxPermissions = RxPermissions(activity)
        disposable = rxPermissions.requestEachCombined(
            *permissions
        )?.subscribe { permission ->
            callback.invoke(permission.granted)
            if (permission.shouldShowRequestPermissionRationale){
                type?.let { RequestPermissionDialog(activity, it).showPopupWindow() }
            }
        }
    }
    fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
        }
        Activities.get().top?.startActivity(intent)
    }


    fun disposeObservable() {
        // 检查 disposable 是否已初始化，并释放它
        disposable?.let {
            if (it.isDisposed) {
                it.dispose()
                disposable = null
            }
        }
    }
}