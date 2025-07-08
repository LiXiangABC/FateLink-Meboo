package io.rong.imkit.conversationlist.provider;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.custom.base.config.BaseConfig;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imkit.R;
import io.rong.imkit.config.RongConfigCenter;
import io.rong.imkit.conversationlist.model.BaseUiConversation;
import io.rong.imkit.userinfo.RongUserInfoManager;
import io.rong.imkit.utils.JsonUtils;
import io.rong.imkit.utils.RongDateUtils;
import io.rong.imkit.utils.RongUtils;
import io.rong.imkit.widget.adapter.IViewProvider;
import io.rong.imkit.widget.adapter.IViewProviderListener;
import io.rong.imkit.widget.adapter.ViewHolder;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;

import java.util.List;

public class BaseConversationProvider implements IViewProvider<BaseUiConversation> {
    private final String TAG = this.getClass().getSimpleName();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rc_conversationlist_item, parent, false);
        return ViewHolder.createViewHolder(parent.getContext(), view);
    }

    @Override
    public boolean isItemViewType(BaseUiConversation item) {
        return true;
    }

    @Override
    public void bindViewHolder(
            final ViewHolder holder,
            final BaseUiConversation uiConversation,
            int position,
            List<BaseUiConversation> list,
            IViewProviderListener<BaseUiConversation> listener) {
        String title = uiConversation.mCore.getConversationTitle();
        boolean isNumeric = title.matches("\\d+");
        if (!(isNumeric && title.length() > 15)) {
            if (title.length() > 20) {
                title = title.substring(0, 20);
                holder.setText(R.id.rc_conversation_title, title + "...");
            } else {
                holder.setText(R.id.rc_conversation_title, title);
            }
        }
        if (TextUtils.isEmpty(uiConversation.mConversationContent)) {
            if (uiConversation.mCore.getLatestMessage() != null && uiConversation.mCore.getLatestMessage().getExtra() != null && !uiConversation.mCore.getLatestMessage().getExtra().equals("")) {
                SpannableStringBuilder builder = new SpannableStringBuilder();
                builder.append(uiConversation.mCore.getLatestMessage().getExtra().equals("null") ? "" : uiConversation.mCore.getLatestMessage().getExtra());
                uiConversation.mConversationContent = builder;
            }
        }
        // 会话头像
        if (!TextUtils.isEmpty(uiConversation.mCore.getPortraitUrl())) {
            if (holder.getView(R.id.rc_conversation_portrait) instanceof ImageView) {
                RongConfigCenter.featureConfig()
                        .getKitImageEngine()
                        .loadConversationListPortrait(
                                holder.getContext(),
                                uiConversation.mCore.getPortraitUrl(),
                                holder.<ImageView>getView(R.id.rc_conversation_portrait),
                                uiConversation.mCore);
            }

        } else {
            int drawableId = R.drawable.rc_default_portrait;
            if (uiConversation
                    .mCore
                    .getConversationType()
                    .equals(Conversation.ConversationType.GROUP)) {
                drawableId = R.drawable.rc_default_group_portrait;
            } else if (uiConversation
                    .mCore
                    .getConversationType()
                    .equals(Conversation.ConversationType.CHATROOM)) {
                drawableId = R.drawable.rc_default_chatroom_portrait;
            } else if (uiConversation
                    .mCore
                    .getConversationType()
                    .equals(Conversation.ConversationType.CUSTOMER_SERVICE)) {
                drawableId = R.drawable.rc_default_chatroom_portrait;
            }

            if (holder.getView(R.id.rc_conversation_portrait) instanceof ImageView) {
                Uri uri = RongUtils.getUriFromDrawableRes(holder.getContext(), drawableId);
                RongConfigCenter.featureConfig()
                        .getKitImageEngine()
                        .loadConversationListPortrait(
                                holder.getContext(),
                                uri.toString(),
                                holder.<ImageView>getView(R.id.rc_conversation_portrait),
                                uiConversation.mCore);
            }
        }

        UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(uiConversation.getConversationIdentifier().getTargetId());
        if (userInfo != null) {
            String extra = userInfo.getExtra();
            if (extra != null && extra!="") {
                try {
                    if (JsonUtils.isJSON(extra)) {
                        JSONObject jsonObject = new JSONObject(extra);
                        if (jsonObject.has("flashchatFlag")) {
                            holder.getView(R.id.rc_conversation_title_flash_chat).setVisibility(jsonObject.getInt("flashchatFlag") == 1 ? View.VISIBLE : View.GONE);
                        }
                        if (jsonObject.has("onlineStatus")) {
                            holder.getView(R.id.rc_conversation_online_tag).setVisibility(jsonObject.getInt("onlineStatus") == 1 ? View.VISIBLE : View.GONE);
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        holder.getView(R.id.rc_conversation_portrait)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (RongConfigCenter.conversationListConfig().getListener()
                                        != null) {
                                    RongConfigCenter.conversationListConfig()
                                            .getListener()
                                            .onConversationPortraitClick(
                                                    holder.getContext(),
                                                    uiConversation.mCore.getConversationType(),
                                                    uiConversation.mCore.getTargetId());
                                }
                            }
                        });
        holder.getView(R.id.rc_conversation_portrait)
                .setOnLongClickListener(
                        new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                if (RongConfigCenter.conversationListConfig().getListener()
                                        != null) {
                                    return RongConfigCenter.conversationListConfig()
                                            .getListener()
                                            .onConversationPortraitLongClick(
                                                    holder.getContext(),
                                                    uiConversation.mCore.getConversationType(),
                                                    uiConversation.mCore.getTargetId());
                                }
                                return false;
                            }
                        });
        // 会话内容
        ((TextView) holder.getView(R.id.rc_conversation_content))
                .setCompoundDrawables(null, null, null, null);
        if (uiConversation.mCore.getSentStatus() != null
                && TextUtils.isEmpty(uiConversation.mCore.getDraft())
                && !TextUtils.isEmpty(uiConversation.mConversationContent)) {
            Drawable drawable = null;
            if (uiConversation.mCore.getSentStatus() == Message.SentStatus.FAILED) {
                drawable = holder.getContext().getResources().getDrawable(R.drawable.rc_ic_warning);
            } else if (uiConversation.mCore.getSentStatus() == Message.SentStatus.SENDING) {
                drawable =
                        holder.getContext()
                                .getResources()
                                .getDrawable(R.drawable.rc_conversation_list_msg_sending);
            }
            if (drawable != null) {
                Bitmap bitmap =
                        BitmapFactory.decodeResource(
                                holder.getContext().getResources(), R.drawable.rc_ic_warning);
                int width = bitmap.getWidth();
                int bottom = width;
                drawable.setBounds(0, 0, width, bottom);
                ((TextView) holder.getView(R.id.rc_conversation_content))
                        .setCompoundDrawablePadding(10);
                ((TextView) holder.getView(R.id.rc_conversation_content))
                        .setCompoundDrawables(drawable, null, null, null);
            }
        }
        holder.setText(
                R.id.rc_conversation_content,
                uiConversation.mConversationContent,
                TextView.BufferType.SPANNABLE);
        // 未读数
        int unreadCount = uiConversation.mCore.getUnreadMessageCount();
        if (unreadCount > 0) {
            holder.setVisible(R.id.rc_conversation_unread, true);
            if (unreadCount > 99) {
                holder.setImageResource(
                        R.id.rc_conversation_unread_bg, R.drawable.rc_unread_count_bg_large);
                holder.setText(
                        R.id.rc_conversation_unread_count,
                        holder.getContext().getString(R.string.rc_conversation_unread_dot));
            } else {
                holder.setImageResource(
                        R.id.rc_conversation_unread_bg, R.drawable.rc_unread_count_bg_normal);
                String count = Integer.toString(unreadCount);
                holder.setText(R.id.rc_conversation_unread_count, count);
            }
        } else {
            holder.setVisible(R.id.rc_conversation_unread, false);
        }

        String time =
                RongDateUtils.getConversationListFormatDate(
                        uiConversation.mCore.getSentTime(), holder.getContext());
        holder.setText(R.id.rc_conversation_date, time);

//        if (uiConversation.mCore.isTop()) {
//            holder.getConvertView()
//                    .setBackgroundColor(
//                            holder.getContext().getResources().getColor(R.color.rc_item_top_color));
//        } else {
//            holder.getConvertView()
//                    .setBackgroundColor(
//                            holder.getContext().getResources().getColor(R.color.rc_white_color));
//        }
        boolean noDisturb =
                uiConversation
                        .mCore
                        .getNotificationStatus()
                        .equals(Conversation.ConversationNotificationStatus.DO_NOT_DISTURB);
        holder.setVisible(R.id.rc_conversation_no_disturb, noDisturb);
        if (Conversation.ConversationType.ULTRA_GROUP.equals(
                uiConversation.mCore.getConversationType())) {
//            holder.setVisible(R.id.divider, false);
        } else {
//            holder.setVisible(R.id.divider, true);
        }
//        if (isDebugMode(holder.getContext())) {
//            if (uiConversation.mCore.getConversationType()
//                            == Conversation.ConversationType.ULTRA_GROUP
//                    && uiConversation.mCore.getChannelType() != null) {
//                holder.setText(
//                        R.id.rc_conversation_title,
//                        uiConversation.mCore.getConversationTitle()
//                                + (uiConversation.mCore.getChannelType()
//                                                == IRongCoreEnum.UltraGroupChannelType
//                                                        .ULTRA_GROUP_CHANNEL_TYPE_PRIVATE
//                                        ? "(私)"
//                                        : "(公)")
//                                + "("
//                                + uiConversation.mCore.getPushNotificationLevel()
//                                + ")");
//            } else {
//                holder.setText(
//                        R.id.rc_conversation_title,
//                        uiConversation.mCore.getConversationTitle()
//                                + "("
//                                + uiConversation.mCore.getPushNotificationLevel()
//                                + ")");
//            }
//        }
    }

    private boolean isDebugMode(Context context) {
        return context.getSharedPreferences("config", MODE_PRIVATE).getBoolean("isDebug", false);
    }
}
