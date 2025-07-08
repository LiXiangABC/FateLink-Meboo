package io.rong.imkit.feature.recallEdit;

import io.rong.imlib.model.Message;

public interface RecallEditCountDownCallBack {
    /**
     * @param messageId 消息Uid
     * @param mMessage
     */
    void onFinish(String messageId, Message mMessage);

    void onTick(long untilFinished, String messageId);
}
