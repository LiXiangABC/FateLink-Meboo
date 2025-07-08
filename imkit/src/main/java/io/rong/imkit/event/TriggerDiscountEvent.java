package io.rong.imkit.event;

import android.app.Activity;

public class TriggerDiscountEvent {
    private DiscountEnum discountEnum;
    private Activity activity;
    private int triggerType;

    public Activity getActivity() {
        return activity;
    }

    public DiscountEnum getDiscountEnum() {
        return discountEnum;
    }

    public int getTriggerType() {
        return triggerType;
    }

    public TriggerDiscountEvent(Activity activity, DiscountEnum discountEnum) {
        this.discountEnum = discountEnum;
        this.activity = activity;
    }
    public TriggerDiscountEvent(Activity activity, int triggerType, DiscountEnum discountEnum) {
        this.discountEnum = discountEnum;
        this.activity = activity;
        this.triggerType = triggerType;
    }

    public enum DiscountEnum{
        CLOSE_DISCOUNT_POP,CLOSE_MEMBER_POP
    }
}
