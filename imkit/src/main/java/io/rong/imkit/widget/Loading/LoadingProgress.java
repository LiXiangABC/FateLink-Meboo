package io.rong.imkit.widget.Loading;

/**
 * Created by  on 2017/8/16.
 */
public interface LoadingProgress {

  void showLoading(String message);

  void dismissLoading();

  void setCancelable(boolean cancelable);
}
