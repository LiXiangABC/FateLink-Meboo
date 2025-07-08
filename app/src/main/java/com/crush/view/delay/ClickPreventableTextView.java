package com.crush.view.delay;

import android.content.Context;
import android.text.Spannable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * @author zxp
 * @since 26/10/21
 */
public class ClickPreventableTextView  extends AppCompatTextView implements View.OnClickListener {
    private boolean preventClick;
    private OnClickListener clickListener;
    private boolean ignoreSpannableClick;

    public ClickPreventableTextView(Context context) {
        super(context);
    }

    public ClickPreventableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClickPreventableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.getMovementMethod() != null) {
            this.getMovementMethod().onTouchEvent(this, (Spannable) this.getText(), event);
        }

        this.ignoreSpannableClick = true;
        boolean ret = super.onTouchEvent(event);
        this.ignoreSpannableClick = false;
        return ret;
    }

    public boolean ignoreSpannableClick() {
        return this.ignoreSpannableClick;
    }

    public void preventNextClick() {
        this.preventClick = true;
    }

    public void setOnClickListener(OnClickListener listener) {
        this.clickListener = listener;
        super.setOnClickListener(this);
    }

    public void onClick(View v) {
        if (this.preventClick) {
            this.preventClick = false;
        } else if (this.clickListener != null) {
            this.clickListener.onClick(v);
        }

    }
}