package io.rong.imkit.conversation.messgelist.provider;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;

import java.util.List;

import io.rong.imkit.R;
import io.rong.imkit.entity.TipsEntity;
import io.rong.imkit.model.UiMessage;
import io.rong.imkit.widget.adapter.IViewProviderListener;
import io.rong.imkit.widget.adapter.ViewHolder;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;

public class TipsMessageItemProvider extends BaseMessageItemProvider<TextMessage> {

    @Override
    protected ViewHolder onCreateMessageContentViewHolder(ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rc_item_tips_card_message, parent, false);
        return new ViewHolder(parent.getContext(), view);
    }

    @Override
    public void changeUi(ViewHolder holder,boolean isSender, int position, Message message) {
        if (!isTips(message.getContent())) {
            return;
        }
        FrameLayout rcContent = holder.getView(R.id.rc_content);
        rcContent.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
        holder.setBackgroundRes(
                R.id.rc_content,
                R.color.picture_color_transparent);
        holder.getView(R.id.rc_left_portrait).setVisibility(View.GONE);
    }

    @Override
    protected void bindMessageContentViewHolder(ViewHolder holder, ViewHolder parentHolder, TextMessage textMessage, UiMessage uiMessage, int position, List<UiMessage> list, IViewProviderListener<UiMessage> listener) {
        LottieAnimationView aminJson = holder.itemView.findViewById(R.id.beshyJson);
        String json = textMessage.getContent();
        try {
            Gson gson = new Gson();
            TipsEntity tipsEntity = gson.fromJson(json, TipsEntity.class);
            if (!TextUtils.isEmpty(tipsEntity.getTitle())){
                holder.setText(R.id.tvTitle, tipsEntity.getTitle());
            }
            holder.getView(R.id.tvTitle).setVisibility(TextUtils.isEmpty(tipsEntity.getTitle())?View.GONE:View.VISIBLE);

            if (tipsEntity.getContents()!=null && !tipsEntity.getContents().isEmpty()){
                if (tipsEntity.getContents().size()>0){
                    holder.setText(R.id.tvContentOne, tipsEntity.getContents().get(0)).setVisible(R.id.tvContentOne,true);
                }
                if (tipsEntity.getContents().size()>1){
                    holder.setText(R.id.tvContentTwo, tipsEntity.getContents().get(1)).setVisible(R.id.tvContentTwo,true);
                }
                if (tipsEntity.getContents().size()>2){
                    holder.setText(R.id.tvContentThree, tipsEntity.getContents().get(2)).setVisible(R.id.tvContentThree,true);
                }
            }
            switch (tipsEntity.getConversationType()) {
                case 1:
                    aminJson.setAnimation("beshy.json");
                    break;
                case 2:
                    aminJson.setAnimation("boldly.json");
                    break;
                case 3:
                    aminJson.setAnimation("courageous.json");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected boolean onItemClick(ViewHolder holder, TextMessage textMessage, UiMessage uiMessage, int position, List<UiMessage> list, IViewProviderListener<UiMessage> listener) {
        return false;
    }

    private boolean isTips(MessageContent messageContent) {
        try {
            Gson gson = new Gson();
            String json = ((TextMessage) messageContent).getContent();
            TipsEntity tipsEntity = gson.fromJson(json, TipsEntity.class);
            return tipsEntity.getShow();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected boolean isMessageViewType(MessageContent messageContent) {
        return isTips(messageContent);
    }

    @Override
    public Spannable getSummarySpannable(Context context, TextMessage textMessage) {
        return new SpannableString("");
    }
}
