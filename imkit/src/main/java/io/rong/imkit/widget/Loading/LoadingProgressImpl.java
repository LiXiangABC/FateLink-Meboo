package io.rong.imkit.widget.Loading;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import io.rong.imkit.R;


/**
 * author :
 * date   : 2019/7/4 17:30
 * desc   :
 */
public class LoadingProgressImpl extends Dialog implements LoadingProgress {
    private Activity activity;
    private LottieAnimationView loadingAnim;

    public LoadingProgressImpl(Context context) {
        super(context, R.style.common_ui_loading_progress);
        this.setContentView(R.layout.layout_loading);
        loadingAnim = findViewById(R.id.loading_anim);
        this.initContext(context);
    }

    private void initContext(Context context) {
        this.activity = (Activity)context;
    }

    public boolean isActivityAttached() {
        if (Build.VERSION.SDK_INT >= 17) {
            return this.activity != null && !this.activity.isFinishing() && !this.activity.isDestroyed() && this.getWindow() != null;
        } else {
            return this.activity != null && !this.activity.isFinishing() && this.getWindow() != null;
        }
    }

    public LoadingProgressImpl setMessage(String strMessage) {
        TextView tvMsg = (TextView)this.findViewById(R.id.id_tv_loadingmsg);
        if (tvMsg != null) {
            tvMsg.setText(strMessage);
        }

        return this;
    }

    @Override
    public void showLoading(String message) {
        if (this.isActivityAttached()) {
            this.setMessage(message);
            if (!this.isShowing()) {
                super.show();
            }
        }

    }

    @Override
    public void dismissLoading() {
        if (this.isActivityAttached() && this.isShowing()) {
            loadingAnim.cancelAnimation();
            super.dismiss();
        }

    }
}