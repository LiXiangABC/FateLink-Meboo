package io.rong.imkit.conversation.messgelist.provider;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import io.rong.imkit.R;
import io.rong.imkit.dialog.UnblockUserDialog;
import io.rong.imkit.model.UiMessage;
import io.rong.imkit.widget.adapter.IViewProviderListener;
import io.rong.imkit.widget.adapter.ViewHolder;
import io.rong.imlib.model.MessageContent;
import io.rong.message.InformationNotificationMessage;
import java.util.List;

public class InformationNotificationMessageItemProvider
        extends BaseNotificationMessageItemProvider<InformationNotificationMessage> {

    @Override
    protected ViewHolder onCreateMessageContentViewHolder(ViewGroup parent, int viewType) {
        View rootView =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rc_item_information_notification_message, parent, false);
        return new ViewHolder(parent.getContext(), rootView);
    }

    @Override
    public boolean isItemViewType(UiMessage item) {
        return item.getMessage().getContent() instanceof InformationNotificationMessage;
    }

    @Override
    protected boolean isMessageViewType(MessageContent messageContent) {
        return messageContent instanceof InformationNotificationMessage;
    }

    @Override
    protected void bindMessageContentViewHolder(
            ViewHolder holder,
            ViewHolder parentHolder,
            InformationNotificationMessage content,
            UiMessage uiMessage,
            int position,
            List<UiMessage> list,
            IViewProviderListener<UiMessage> listener) {
        TextView rcMsg = holder.getView(R.id.rc_msg);
        if (content.getMessage().contains("Unblock")) {
            SpannableStringBuilder style = new SpannableStringBuilder();
            style.append(content.getMessage());
            style.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View view) {
                    UnblockUserDialog unblockUserDialog = new UnblockUserDialog(holder.getContext(), uiMessage.getTargetId());
                    unblockUserDialog.showPopupWindow();
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(true);
                    ds.setColor(Color.parseColor("#FF70AC"));
                    ds.clearShadowLayer();
                }
            },style.toString().lastIndexOf("Unblock"), style.toString().lastIndexOf("Unblock")+"Unblock".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.setText(R.id.rc_msg, style);
            rcMsg.setText(style);
            rcMsg.setMovementMethod(LinkMovementMethod.getInstance());
        }else {
            holder.setText(R.id.rc_msg, content.getMessage());

        }
    }

    @Override
    public Spannable getSummarySpannable(Context context, InformationNotificationMessage data) {
        if (data != null && !TextUtils.isEmpty(data.getMessage())) {
            return new SpannableString(data.getMessage());
        }
        return null;
    }
}
