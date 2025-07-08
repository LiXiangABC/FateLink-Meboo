package com.crush.rongyun;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crush.R;

import java.util.List;

import io.rong.imkit.conversation.extension.component.emoticon.AndroidEmoji;
import io.rong.imkit.conversation.messgelist.provider.BaseMessageItemProvider;
import io.rong.imkit.model.UiMessage;
import io.rong.imkit.widget.adapter.IViewProviderListener;
import io.rong.imkit.widget.adapter.ViewHolder;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;

public class MyTextMessageProvider extends BaseMessageItemProvider<TextMessage>{

        public MyTextMessageProvider() {
            mConfig.centerInHorizontal = false; // 配置展示属性，该消息居中显示。
        }

        /**
         * 根据自定义布局文件生成 ViewHolder
         */
        @Override
        protected ViewHolder onCreateMessageContentViewHolder(ViewGroup viewGroup, int i) {
            View textView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_chat_text_message, viewGroup, false);
            return new ViewHolder(viewGroup.getContext(), textView);
        }

        /**
         * 根据消息内容，设置布局文件里各控件的值。
         */
        @Override
        protected void bindMessageContentViewHolder(ViewHolder viewHolder, ViewHolder viewHolder1, TextMessage textMessage, UiMessage uiMessage, int i, List<UiMessage> list, IViewProviderListener<UiMessage> iViewProviderListener) {
            TextView textView = viewHolder.getView(R.id.my_text);

            if (uiMessage.getMessage().getMessageDirection().getValue() == 1){
                textView.setTextColor(Color.parseColor("#FFFFFF"));
            }else {
                textView.setTextColor(Color.parseColor("#000000"));
            }
            textView.setText(textMessage.getContent());
        }

        /**
         * 自定义布局里各控件点击时会回调此方法，可以在这里实现点击逻辑。
         * 此处忽略处理，没有处理点击事件。
         */
        @Override
        protected boolean onItemClick(ViewHolder viewHolder, TextMessage textMessage, UiMessage uiMessage, int i, List<UiMessage> list, IViewProviderListener<UiMessage> iViewProviderListener) {
            return false;
        }

        /**
         * 根据消息类型，返回是否为本模板需要展示的消息类型。
         * 此处示例代表本模板仅处理文本类型的消息。
         */
        @Override
        protected boolean isMessageViewType(MessageContent messageContent) {
            return messageContent instanceof TextMessage;
        }

        /**
         * 当该类型消息为会话最后一条消息时，需要在会话列表的会话里展示此消息的描述，该方法返回描述内容。
         * 此处以返回文本消息的具体内容为例。
         */
        @Override
        public Spannable getSummarySpannable(Context context, TextMessage textMessage) {
            Log.e("~~~", "getSummarySpannable: "+ textMessage.getContent() );
            return new SpannableString(AndroidEmoji.ensure(textMessage.getContent()));
        }
}