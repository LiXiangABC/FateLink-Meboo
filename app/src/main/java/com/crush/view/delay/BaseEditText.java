package com.crush.view.delay;

import android.content.Context;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.AbsoluteSizeSpan;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

public class BaseEditText extends AppCompatEditText {

  public BaseEditText(Context context) {
    this(context, null);
  }

  public BaseEditText(Context context, AttributeSet attrs) {
    super(context, attrs);
    analyzeAttributeSet(context, attrs);
  }

  public BaseEditText(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    analyzeAttributeSet(context, attrs);
  }

  @Override public void setFilters(InputFilter[] filters) {
    super.setFilters(filters == null ? new InputFilter[] {} : filters);
  }

  private void analyzeAttributeSet(Context context, AttributeSet attrs) {

  }

  public void setHintTextSize(int sp) {
    if (getHint() == null) {
      return;
    }
    SpannableString ss = new SpannableString(getHint().toString());
    AbsoluteSizeSpan ass = new AbsoluteSizeSpan(sp, true);
    ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    setHint(new SpannedString(ss));
  }
}
