package com.crush.util;

import android.content.Context;
import android.text.InputType;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * @author zxp
 * @since 27/10/21
 */
public class SoftInputUtils {
    public SoftInputUtils() {
    }

    public static void hideSoftInput(View view) {
        if (view != null && view.getContext() != null) {
            InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            view.clearFocus();
            if (imm != null && imm.isActive()) {
                imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public static void showSoftInput(EditText editText) {
        if (editText != null) {
            InputMethodManager imm = (InputMethodManager)editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            do {
                editText.requestFocus();

            } while(!editText.isFocused());
                imm.showSoftInput(editText, 2);
                imm.toggleSoftInput(0, 2);
                editText.setKeyListener(new TextKeyListener(TextKeyListener.Capitalize.WORDS, true) {
                    @Override
                    public int getInputType() {
                        //优先弹出字母键盘
                        return InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;
                    }
                });

        }
    }
    public static void showSoftInput(EditText editText,int inputType) {
        if (editText != null) {
            InputMethodManager imm = (InputMethodManager)editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            do {
                editText.requestFocus();

            } while(!editText.isFocused());
                imm.showSoftInput(editText, 2);
                imm.toggleSoftInput(0, 2);
                editText.setKeyListener(new TextKeyListener(TextKeyListener.Capitalize.WORDS, true) {
                    @Override
                    public int getInputType() {
                        //优先弹出字母键盘
                        return inputType;
                    }
                });

        }
    }


    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     */
    public static boolean isShouldHideInput(View focusView, MotionEvent event, View view) {
        if ((focusView instanceof EditText)) {
            return !isEditTextOrClickViewTouched(event, view);
        }
        return false;
    }

    /**
     * 判断是否触摸在可触摸的EditText上面,或者带有点击事件的View上面
     */
    private static boolean isEditTextOrClickViewTouched(MotionEvent event, View view) {

        if (view == null || !view.isEnabled()) {
            return false;
        }

        if (view instanceof EditText || view.hasOnClickListeners()) {

            int[] l = { 0, 0 };
            view.getLocationInWindow(l);

            int left = l[0], top = l[1], bottom = top + view.getHeight(), right = left + view.getWidth();

            return event.getX() > left
                    && event.getX() < right
                    && event.getY() > top
                    && event.getY() < bottom;
        }

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                if (isEditTextOrClickViewTouched(event, ((ViewGroup) view).getChildAt(i))) {
                    return true;
                }
            }
        }

        return false;
    }

}
