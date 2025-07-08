package io.rong.imkit.widget.Loading;

import android.os.Handler;
import android.os.Looper;

/**
 * @author
 */
public class RunnablePost {

  /**
   * 在MainLooper上执行
   */
  public static void post(Runnable runnable) {
    if (runnable == null) {
      return;
    }
    RunnablePost.postOn(runnable, Looper.getMainLooper());
  }

  /**
   * 指定looper执行
   */
  public static void postOn(Runnable runnable, Looper looper) {
    if (runnable == null) {
      return;
    }
    if (looper == null || Looper.myLooper() == looper) {
      runnable.run();
    } else {
      new Handler(looper).post(runnable);
    }
  }
}