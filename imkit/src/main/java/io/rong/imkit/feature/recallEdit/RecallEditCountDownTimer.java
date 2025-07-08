package io.rong.imkit.feature.recallEdit;

import io.rong.common.CountDownTimer;
import io.rong.imlib.model.Message;

/** 用于撤回消息，保持重新编辑状态倒计时的类 */
public class RecallEditCountDownTimer {
    private static final int COUNTDOWN_INTERVAL = 1000;
    private CountDownTimer mCountDownTimer;
    private String mMessageId;

    private Message mMessage;
    private RecallEditCountDownTimerListener mListener;

    public RecallEditCountDownTimer(
            String messageId,Message message ,RecallEditCountDownTimerListener listener, long millisInFuture) {
        mMessageId = messageId;
        mMessage = message;
        mListener = listener;
        mCountDownTimer =
                new CountDownTimer(millisInFuture, COUNTDOWN_INTERVAL) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        if (mListener != null) {
                            mListener.onTick(Math.round(millisUntilFinished / 1000f), mMessageId);
                        }
                    }

                    @Override
                    public void onFinish() {
                        if (mListener != null) {
                            mListener.onFinish(mMessageId,mMessage);
                        }
                    }
                };
    }

    public void start() {
        if (mCountDownTimer != null && !mCountDownTimer.isStart()) {
            mCountDownTimer.start();
        }
    }

    public void cancel() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    public void setListener(RecallEditCountDownTimerListener listener) {
        this.mListener = listener;
    }
}
