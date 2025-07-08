package com.crush.view.Loading;

import android.app.Activity;
import android.os.Build;

/**
 * author :
 * date   : 2019/7/4 17:28
 * desc   :
 */
public class LoadingDialog {
    private static LoadingProgress pDialog = null;
    private static int hashCode = 0;

    public LoadingDialog() {
    }

    public static void showLoading(Activity activity) {
        showLoading(activity, "", false);
    }
    public static void showLoading(Activity activity,String text) {
        showLoading(activity, text, false);
    }

    public static void showLoading(Activity activity, boolean isCancelable) {
        showLoading(activity, "", isCancelable);
    }

    private static boolean isActivityAttached(Activity activity) {
        if (Build.VERSION.SDK_INT >= 17) {
            return activity != null && !activity.isFinishing() && !activity.isDestroyed();
        } else {
            return activity != null && !activity.isFinishing();
        }
    }

    public static void showLoading(final Activity activity, final String text, final boolean isCancelable) {
        RunnablePost.post(new Runnable() {
            public void run() {
                if (LoadingDialog.isActivityAttached(activity)) {
                    if (LoadingDialog.hashCode != activity.hashCode()) {
                        LoadingDialog.destroyDialog();
                    }

                    if (null == LoadingDialog.pDialog) {
                        LoadingDialog.pDialog = LoadingProgressFactory.createLoadingProgress(activity);
                        LoadingDialog.hashCode = activity.hashCode();
                    }

                    LoadingDialog.pDialog.setCancelable(isCancelable);
                    if (LoadingDialog.pDialog != null) {
                        if (LoadingDialog.isActivityAttached(activity)) {
                            LoadingDialog.pDialog.showLoading(text);
                        }
                    }

                }
            }
        });
    }

    public static void dismissLoading(Activity activity) {
        if (activity != null && hashCode == activity.hashCode()) {
            destroyDialog();
        }

    }

    public static void dismissLoading() {
        destroyDialog();
    }

    private static void destroyDialog() {
        RunnablePost.post(new Runnable() {
            public void run() {
                try {
                    if (LoadingDialog.pDialog != null) {
                        LoadingDialog.pDialog.dismissLoading();
                        LoadingDialog.pDialog = null;
                    }

                    LoadingDialog.hashCode = 0;
                } catch (Exception var2) {
                    var2.printStackTrace();
                }

            }
        });
    }
}
