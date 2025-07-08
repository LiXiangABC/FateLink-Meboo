package com.crush.view.delay.delegate;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

/**
 * @author
 * @date 2017/6/7
 */
public class Res {

  private static Context context;

  public Res() {
  }

  public static void init(Context context) {
    Res.context = context.getApplicationContext();
  }

  public static String string(@StringRes int id) {
    return context.getString(id);
  }

  public static int color(@ColorRes int id) {
    return ContextCompat.getColor(context, id);
  }

  public static Drawable drawable(@DrawableRes int id) {
    return ContextCompat.getDrawable(context, id);
  }

  public static float dimen(@DimenRes int id) {
    return context.getResources().getDimension(id);
  }

  public static float dimensionPixelSize(@DimenRes int id) {
    return (float) context.getResources().getDimensionPixelSize(id);
  }
}
