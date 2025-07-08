package com.crush.view.Loading;

import android.app.Activity;

import androidx.annotation.NonNull;


/**
 * author :
 * date   : 2019/7/4 17:28
 * desc   :
 */
public class LoadingProgressFactory {
    private static Producer producer;

    public LoadingProgressFactory() {
    }

    public static LoadingProgress createLoadingProgress(Activity activity) {
        LoadingProgress progress = producer == null ? null : producer.create(activity);
        return (LoadingProgress)(progress == null ? new LoadingProgressImpl(activity) : progress);
    }

    public static void registerProducer(Producer producer) {
        producer = producer;
    }

    public interface Producer {
        @NonNull
        LoadingProgress create(Activity var1);
    }
}
