package io.rong.imkit.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

public class DensityUtil {
    //获取屏幕宽度
    public static int getScreensWidth(Context context){
        WindowManager m = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        m.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    //获取屏幕高度
    public static int getScreensHeight(Context context){
        WindowManager m = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        m.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    //根据手机的分辨率从 dp 的单位 转成为 px(像素)
    public static float dp2px(Resources resources, float dpValue) {
        final float scale = resources.getDisplayMetrics().density;
        return (dpValue * scale + 0.5f);
    }
    //根据手机的分辨率从 dp 的单位 转成为 px(像素)
    public static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5f);
    }
    public static float dp2pxF(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return dpValue * scale + 0.5f;
    }
    //根据手机的分辨率从 px(像素) 的单位 转成为 dp
    public static float px2dp(Resources resources, float pxValue) {
        final float scale = resources.getDisplayMetrics().density;
        return (pxValue / scale + 0.5f);
    }
    //获取屏幕dpi
    public static int getDpi(Context context) {
        return context.getResources().getDisplayMetrics().densityDpi;
    }

    //动态设置view的宽高
    public static void setWH(View view, int width, int height){
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        view.setLayoutParams(layoutParams);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
