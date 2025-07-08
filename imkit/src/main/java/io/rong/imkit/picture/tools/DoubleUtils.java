package io.rong.imkit.picture.tools;

/**
 * author：luck project：PictureSelector package：com.luck.picture.lib.tool email：893855882@qq.com
 * data：2017/5/25
 */
public class DoubleUtils {
    /** Prevent continuous click, jump two pages */
    private static long lastClickTime;

    private static final long TIME = 800;

    public static boolean isFastDoubleClick() {
        return isFastDoubleClick(TIME);
    }
    public static boolean isFastDoubleClick(long latstTime) {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < latstTime) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
