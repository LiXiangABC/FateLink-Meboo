package com.crush.util

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.crush.R
import com.custom.base.util.ToastUtil


class SystemAppCallUtil {
    /**
     * 发送邮件
     */
    fun checkGmail(context: Activity?, title: String, extraText: String) {
        val packageManager: PackageManager? = context?.packageManager
        val emailIntent1 = Intent(Intent.ACTION_SEND)
//        emailIntent1.type = "text/plain"
        emailIntent1.type = "message/rfc822"
        val resolveInfoList = packageManager?.queryIntentActivities(emailIntent1, 0)
//        var isEmailEnabled = false
//        if (resolveInfoList != null) {
//            for (resolveInfo in resolveInfoList) {
//                if (resolveInfo.activityInfo.packageName.contains("com.google.android.gm")) {
//                    isEmailEnabled = true
//                    break
//                }
//            }
//        }
        // 检查是否有应用可以处理发送邮件的Intent
        val hasEmailApp = !resolveInfoList.isNullOrEmpty()
        val isGmailAvailable = hasEmailApp && resolveInfoList?.any {
            it.activityInfo.packageName.contains("com.google.android.gm")
        } == true

        if (!isGmailAvailable) {
           sendEmail(context, title, extraText)
            // 系统邮箱应用可用
        } else {
            // 系统邮箱应用被禁用
            ToastUtil.toast("Please set up an email account and send us feedback")
        }
    }

    fun sendEmail(context: Activity?, title: String, extraText: String) {
        try {
            val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))/*不带附件发送邮件*/
            if(emailIntent!= null) {
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, title)
                emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(context?.getString(R.string.email_address)))
                emailIntent.putExtra(Intent.EXTRA_TEXT, extraText) //发送的内容
                context?.startActivity(emailIntent)
            }
        }catch (e :Exception){
            ToastUtil.toast("Please set up an email account and send us feedback")
        }

    }
    fun checkOutlookApp(context: Activity?, title: String, extraText: String) {
        val packageManager = context?.packageManager
        val outlookPackageName = "com.microsoft.office.outlook" // Outlook应用程序的包名

        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "text/plain"
        val installedPackages = packageManager?.queryIntentActivities(emailIntent,0)
        var isEmailEnabled = false
        if (installedPackages != null) {
            for (resolveInfo in installedPackages) {
                if (resolveInfo.activityInfo.packageName.contains(outlookPackageName)) {
                    isEmailEnabled = true
                    break
                }
            }
        }
        if (isEmailEnabled) {
            // 检查Outlook应用程序是否被禁用
            val packageInfo = packageManager?.getPackageInfo(outlookPackageName, 0)
            val enabled = packageInfo?.applicationInfo?.enabled
            if (enabled == true) {
                sendEmail(context, title, extraText)
            } else {
                // 提示用户启用Outlook应用程序
                val message = "Outlook应用程序已被禁用。请在设置中启用Outlook应用程序以发送邮件。"
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        } else {
            // 提示用户安装Outlook应用程序
            val message = "Outlook应用程序未安装。请从应用商店安装Outlook应用程序以发送邮件。"
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }



}