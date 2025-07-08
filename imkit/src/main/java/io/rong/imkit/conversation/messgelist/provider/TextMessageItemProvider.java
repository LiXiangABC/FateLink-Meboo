package io.rong.imkit.conversation.messgelist.provider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.LayoutDirection;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.text.TextUtilsCompat;

import com.custom.base.config.BaseConfig;
import com.google.gson.Gson;
import com.sunday.eventbus.SDEventManager;

import java.util.List;
import java.util.Locale;

import io.rong.common.RLog;
import io.rong.imkit.R;
import io.rong.imkit.SpName;
import io.rong.imkit.activity.RongWebviewActivity;
import io.rong.imkit.conversation.extension.component.emoticon.AndroidEmoji;
import io.rong.imkit.dialog.GoogleEvaluateDialog;
import io.rong.imkit.dialog.MemberBuyDialog;
import io.rong.imkit.entity.EvaluateCheckBean;
import io.rong.imkit.entity.TipsEntity;
import io.rong.imkit.event.EnumEventTag;
import io.rong.imkit.http.HttpRequest;
import io.rong.imkit.model.State;
import io.rong.imkit.model.UiMessage;
import io.rong.imkit.utils.RouteUtils;
import io.rong.imkit.utils.TextViewUtils;
import io.rong.imkit.widget.adapter.IViewProviderListener;
import io.rong.imkit.widget.adapter.ViewHolder;
import io.rong.imkit.widget.refresh.util.DesignUtil;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;

public class TextMessageItemProvider extends BaseMessageItemProvider<TextMessage> {

    public static final String GoogleUrl = "https://play.google.com/store/apps/details?id=com.blazeupa.auramix&pli=1";
    public static final String memberDialogClickText = "click here to get limited-time special premium.";
    public static final String emailClickText = "auraMix-Official@outlook.com";

    public TextMessageItemProvider() {
        mConfig.showReadState = true;
    }

    @Override
    protected ViewHolder onCreateMessageContentViewHolder(ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rc_translate_text_message_item, parent, false);
        return new ViewHolder(parent.getContext(), view);
    }

    @Override
    protected void bindMessageContentViewHolder(
            final ViewHolder holder,
            ViewHolder parentHolder,
            TextMessage message,
            final UiMessage uiMessage,
            int position,
            List<UiMessage> list,
            IViewProviderListener<UiMessage> listener) {
        final TextView textView = holder.getView(R.id.rc_text);
        final TextView translatedView = holder.getView(R.id.rc_translated_text);
        final ProgressBar progressBar = holder.getView(R.id.rc_pb_translating);

        if (!checkViewsValid(textView, translatedView, progressBar)) {
            RLog.e(TAG, "checkViewsValid error," + uiMessage.getObjectName());
            return;
        }

        if (TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault())
                == LayoutDirection.RTL) {
            textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        }
        textView.setTag(uiMessage.getMessageId());
        if (uiMessage.getContentSpannable() == null) {
            SpannableStringBuilder spannable =
                    TextViewUtils.getSpannable(
                            message.getContent(),
                            new TextViewUtils.RegularCallBack() {
                                @Override
                                public void finish(SpannableStringBuilder spannable) {
                                    uiMessage.setContentSpannable(spannable);
                                    textView.post(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (TextUtils.equals(
                                                            textView.getTag() == null
                                                                    ? ""
                                                                    : textView.getTag().toString(),
                                                            String.valueOf(
                                                                    uiMessage.getMessageId())))
                                                        textView.setText(
                                                                uiMessage.getContentSpannable());
                                                }
                                            });
                                }
                            });
            uiMessage.setContentSpannable(spannable);
        }
        if (uiMessage.getMessage().getMessageDirection().getValue() == 1) {
            textView.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            textView.setTextColor(Color.parseColor("#000000"));
        }
        //TODO 设置textColorLink修改"链接"颜色
        textView.setText(uiMessage.getContentSpannable());
//        textView.setMovementMethod(
//                new LinkTextViewMovementMethod(
//                        new ILinkClickListener() {
//                            @Override
//                            public boolean onLinkClick(String link) {
//                                boolean result = false;
//                                if (RongConfigCenter.conversationConfig()
//                                                .getConversationClickListener()
//                                        != null) {
//                                    result =
//                                            RongConfigCenter.conversationConfig()
//                                                    .getConversationClickListener()
//                                                    .onMessageLinkClick(
//                                                            holder.getContext(),
//                                                            link,
//                                                            uiMessage.getMessage());
//                                }
//                                if (result) {
//                                    return true;
//                                }
//                                String str = link.toLowerCase();
//                                if (str.startsWith("http") || str.startsWith("https")) {
//                                    RouteUtils.routeToWebActivity(textView.getContext(), link);
//                                    result = true;
//                                }
//
//                                return result;
//                            }
//                        }));
        textView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ViewParent parent = view.getParent();
                        if (parent instanceof View) {
                            ((View) parent).performClick();
                        }
                    }
                });

        textView.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        ViewParent parent = view.getParent();
                        if (parent instanceof View) {
                            return ((View) parent).performLongClick();
                        }
                        return false;
                    }
                });

        boolean isSender =
                uiMessage.getMessage().getMessageDirection().equals(Message.MessageDirection.SEND);

        if (BaseConfig.Companion.getGetInstance().getString(SpName.INSTANCE.getServerUserCode(), "").equals(uiMessage.getTargetId())) {
            SpannableStringBuilder contentSpannable = uiMessage.getContentSpannable();
            if (contentSpannable.toString().contains(GoogleUrl)) {
                contentSpannable.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View view) {
                        String evaluate = BaseConfig.Companion.getGetInstance().getString(SpName.INSTANCE.getEvaluateCheck(), "");
                        EvaluateCheckBean evaluateCheckBean = new Gson().fromJson(evaluate, EvaluateCheckBean.class);
                        if (evaluateCheckBean != null) {
                            if (evaluateCheckBean.getStatus() == 1) {
                                new GoogleEvaluateDialog(holder.getContext(),
                                        evaluateCheckBean.getMessage(), () -> {
                                    HttpRequest.INSTANCE.getEvaluateGoShop();
                                    RouteUtils.routeToWebActivity(textView.getContext(), GoogleUrl);
                                }).showPopupWindow();
                            } else {
                                RouteUtils.routeToWebActivity(textView.getContext(), GoogleUrl);
                            }
                        }
                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        ds.setUnderlineText(false);
                        ds.setColor(Color.parseColor("#1877F2"));
                        ds.clearShadowLayer();
                    }
                }, contentSpannable.toString().indexOf(GoogleUrl), contentSpannable.toString().indexOf(GoogleUrl) + GoogleUrl.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setMovementMethod(LinkMovementMethod.getInstance());
                textView.setText(contentSpannable);
            }
            if (contentSpannable.toString().contains(memberDialogClickText)) {
                contentSpannable.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View view) {
                        HttpRequest.INSTANCE.showBuyMember(holder.getContext(),0,uiMessage.getTargetId());
                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        ds.setUnderlineText(false);
                        ds.setColor(Color.parseColor("#1877F2"));
                        ds.clearShadowLayer();
                    }
                }, contentSpannable.toString().indexOf(memberDialogClickText), contentSpannable.toString().indexOf(memberDialogClickText) + memberDialogClickText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setMovementMethod(LinkMovementMethod.getInstance());
                textView.setText(contentSpannable);
            }

            if (contentSpannable.toString().contains(emailClickText)) {
                contentSpannable.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View view) {
                        SDEventManager.post(EnumEventTag.EMAIL_EDIT.ordinal());
                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        ds.setUnderlineText(false);
                        ds.setColor(Color.parseColor("#1877F2"));
                        ds.clearShadowLayer();
                    }
                }, contentSpannable.toString().indexOf(emailClickText), contentSpannable.toString().indexOf(emailClickText) + emailClickText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setMovementMethod(LinkMovementMethod.getInstance());
                textView.setText(contentSpannable);
            }
        }
        if (mConfig.showContentBubble) {
            holder.setBackgroundRes(
                    R.id.rc_text,
                    isSender ? R.drawable.shape_text_send_bubble : R.drawable.shape_text_receive_bubble);
        }
        if (uiMessage.getTranslateStatus() == State.SUCCESS
                && !TextUtils.isEmpty(uiMessage.getTranslatedContent())) {
            translatedView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            translatedView.setText(uiMessage.getTranslatedContent());
            holder.setBackgroundRes(
                    R.id.rc_translated_text,
                    isSender
                            ? R.drawable.rc_ic_translation_bubble_right
                            : R.drawable.rc_ic_translation_bubble_left);
        } else if (uiMessage.getTranslateStatus() == State.PROGRESS) {
            translatedView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            translatedView.setVisibility(View.GONE);
            holder.setBackgroundRes(
                    R.id.rc_pb_translating,
                    isSender
                            ? R.drawable.rc_ic_translation_bubble_right
                            : R.drawable.rc_ic_translation_bubble_left);
        } else {
            translatedView.setText(null);
            translatedView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            translatedView.setBackground(null);
        }

        setDirection(textView, isSender);
        setDirection(translatedView, isSender);
        setDirection(progressBar, isSender);

        holder.itemView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewParent parent = v.getParent();
                        if (parent instanceof View) {
                            ((View) parent).performClick();
                        }
                    }
                });
        holder.itemView.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        ViewParent parent = v.getParent();
                        if (parent instanceof View) {
                            return ((View) parent).performLongClick();
                        }
                        return false;
                    }
                });
    }

    private void setDirection(View view, boolean isSender) {
        ConstraintLayout.LayoutParams lp = ((ConstraintLayout.LayoutParams) view.getLayoutParams());
        if (isSender) {
            lp.startToStart = ConstraintLayout.LayoutParams.UNSET;
            lp.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        } else {
            lp.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            lp.endToEnd = ConstraintLayout.LayoutParams.UNSET;
        }
        view.setPadding(DesignUtil.dp2px(view.getContext(), 12f), DesignUtil.dp2px(view.getContext(), 12f), DesignUtil.dp2px(view.getContext(), 12f), DesignUtil.dp2px(view.getContext(), 12f));
        view.setLayoutParams(lp);
    }

    @Override
    protected boolean onItemClick(
            ViewHolder holder,
            TextMessage message,
            UiMessage uiMessage,
            int position,
            List<UiMessage> list,
            IViewProviderListener<UiMessage> listener) {
        return false;
    }

    @Override
    protected boolean isMessageViewType(MessageContent messageContent) {
        try {
            Gson gson = new Gson();
            String json = ((TextMessage) messageContent).getContent();
            TipsEntity tipsEntity = gson.fromJson(json, TipsEntity.class);
            return tipsEntity == null;
        } catch (Exception e) {
            return messageContent instanceof TextMessage && !messageContent.isDestruct();
        }
    }

    @Override
    public Spannable getSummarySpannable(Context context, TextMessage message) {
        if (message != null && !TextUtils.isEmpty(message.getContent())) {
            String content = message.getContent();
            content = content.replace("\n", " ");
            if (content.length() > 100) {
                content = content.substring(0, 100);
            }
            return new SpannableString(AndroidEmoji.ensure(content));
        } else {
            return new SpannableString("");
        }
    }

    @Override
    public boolean showBubble() {
        return false;
    }
}
