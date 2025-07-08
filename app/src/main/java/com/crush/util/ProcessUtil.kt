package com.crush.util

import android.app.ActivityManager
import android.content.Context

object ProcessUtil {
    fun isMainProcess(context: Context): Boolean {
        try {
            return context.getPackageName().equals(getProcessName(context));
        } catch (e: Exception) {
            return false;
        }
        return true;
    }
    fun getProcessName(cxt: Context): String? {
        val pid = android.os.Process.myPid();
        val am = cxt.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningApps = am.getRunningAppProcesses() ?: return null;
        for (procInfo: ActivityManager.RunningAppProcessInfo in runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }
}