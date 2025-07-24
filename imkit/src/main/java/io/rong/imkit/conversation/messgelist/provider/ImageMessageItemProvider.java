package io.rong.imkit.conversation.messgelist.provider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.custom.base.config.BaseConfig;
import com.google.gson.JsonObject;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.common.RLog;
import io.rong.imkit.IMCenter;
import io.rong.imkit.R;
import io.rong.imkit.activity.PicturePagerActivity;
import io.rong.imkit.config.RongConfigCenter;
import io.rong.imkit.dialog.FirstClickPrivateDialog;
import io.rong.imkit.event.FirebaseEventTag;
import io.rong.imkit.feature.recallEdit.RecallEditCountDownCallBack;
import io.rong.imkit.feature.recallEdit.RecallEditManager;
import io.rong.imkit.feature.resend.ResendManager;
import io.rong.imkit.http.HttpRequest;
import io.rong.imkit.model.State;
import io.rong.imkit.model.UiMessage;
import io.rong.imkit.picture.tools.DateUtils;
import io.rong.imkit.picture.tools.ScreenUtils;
import io.rong.imkit.utils.FirebaseEventUtils;
import io.rong.imkit.utils.JsonUtils;
import io.rong.imkit.utils.TimeUtils;
import io.rong.imkit.widget.adapter.IViewProviderListener;
import io.rong.imkit.widget.adapter.ViewHolder;
import io.rong.imkit.widget.refresh.util.DesignUtil;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.ImageMessage;
import jp.wasabeef.glide.transformations.BlurTransformation;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ImageMessageItemProvider extends BaseMessageItemProvider<ImageMessage> {
    // 应用模糊效果
    int radius = 13; // 模糊半径
    int sampling = 9; // 采用率
    private static final String TAG = "ImageMessageItemProvide";
    private static int THUMB_COMPRESSED_SIZE = 240;
    private static int THUMB_COMPRESSED_MIN_SIZE = 100;
    private final String MSG_TAG = "RC:ImgMsg";
    private Integer minSize = null;
    private Integer maxSize = null;

    public ImageMessageItemProvider() {
        mConfig.showContentBubble = false;
        mConfig.showProgress = false;
        mConfig.showReadState = true;
        Context context = IMCenter.getInstance().getContext();
        if (context != null) {
            Resources resources = context.getResources();
            try {
                THUMB_COMPRESSED_SIZE = resources.getInteger(R.integer.rc_thumb_compress_size);
                THUMB_COMPRESSED_MIN_SIZE = resources.getInteger(R.integer.rc_thumb_compress_min_size);
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected ViewHolder onCreateMessageContentViewHolder(ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rc_image_message_item, parent, false);
        return new RecallEditViewHolder(view.getContext(), view);
    }

    @Override
    protected void bindMessageContentViewHolder(
            final ViewHolder holder,
            ViewHolder parentHolder,
            ImageMessage message,
            UiMessage uiMessage,
            int position,
            List<UiMessage> list,
            IViewProviderListener<UiMessage> listener) {
        final ImageView view = holder.getView(R.id.rc_image);
        final LinearLayout rlProgress = holder.getView(R.id.rl_progress);
        final LinearLayout readContainer = holder.getView(R.id.read_container);
        final View mainBg = holder.getView(R.id.main_bg);
        final TextView tvProgress = holder.getView(R.id.tv_progress);
        final TextView imgDestroyedText = holder.getView(R.id.img_destroyed_text);
        final RoundedImageView privateLogo = holder.getView(R.id.private_logo);
        if (!checkViewsValid(view)) {
            RLog.e(TAG, "checkViewsValid error," + uiMessage.getObjectName());
            return;
        }
        Uri thumUri = message.getThumUri();
        if (uiMessage.getState() == State.PROGRESS
                || (uiMessage.getState() == State.ERROR
                && ResendManager.getInstance().needResend(uiMessage.getMessageId()))) {
            rlProgress.setVisibility(View.VISIBLE);
            mainBg.setVisibility(View.VISIBLE);
            tvProgress.setText(uiMessage.getProgress() + "%");
        } else {
            rlProgress.setVisibility(View.GONE);
            mainBg.setVisibility(View.GONE);
        }
        if (thumUri != null && thumUri.getPath() != null) {
            RequestOptions options;
            if (uiMessage.getExpansion() != null) {
                Map<String, String> expansion = uiMessage.getExpansion();
                if ("true".equals(expansion.get("isPrivate"))) {
                    if (expansion.get("destructTime") != null && !Objects.equals(expansion.get("destructTime"), "")) {
                        options = RequestOptions.bitmapTransform(
                                        new RoundedCorners(ScreenUtils.dip2px(IMCenter.getInstance().getContext(), 6)))
                                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);

                        long validTime = RongConfigCenter.conversationConfig().rc_message_reading_burn_interval;
                        long countDownTime = DateUtils.getInstance().cdTimes(System.currentTimeMillis(), Long.parseLong(Objects.requireNonNull(expansion.get("destructTime"))));
                        if (countDownTime < validTime) {
                            imgDestroyedText.setVisibility(View.GONE);
                            view.setVisibility(View.VISIBLE);
                            readContainer.setVisibility(View.VISIBLE);
                            privateLogo.setVisibility(View.GONE);
                            RecallEditViewHolder viewHolder = (RecallEditViewHolder) holder;
                            viewHolder.messageId = String.valueOf(uiMessage.getMessage().getMessageId());
                            RecallEditManager.getInstance().startCountDown(uiMessage.getMessage(),
                                            validTime - countDownTime,
                                            new RecallEditCountDownListener(viewHolder));
                        } else {
                            imgDestroyedText.setVisibility(View.VISIBLE);
                            imgDestroyedText.setBackgroundResource(uiMessage.getMessageDirection().getValue() == 2?R.drawable.icon_left_private_photo_destroyed:R.drawable.icon_private_photo_destroyed);
                            view.setVisibility(View.GONE);
                            readContainer.setVisibility(View.GONE);
                            privateLogo.setVisibility(View.GONE);
                        }
                    } else {
                        imgDestroyedText.setVisibility(View.GONE);
                        readContainer.setVisibility(View.GONE);
                        view.setVisibility(View.VISIBLE);
                        privateLogo.setVisibility(View.VISIBLE);
                        //如果该条消息带有radius值，则去消息内的radius值，否则取本地值
                        if (expansion.get("radius")!= null){
                            radius = Integer.parseInt(Objects.requireNonNull(expansion.get("radius")));
                            sampling = Integer.parseInt(Objects.requireNonNull(expansion.get("radius")));
                        }
                        options = RequestOptions.bitmapTransform(new BlurTransformation(radius, sampling))
                                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                    }
                } else {
                    setViewVisible(view, readContainer, imgDestroyedText, privateLogo);
                    options = RequestOptions.bitmapTransform(
                                    new RoundedCorners(ScreenUtils.dip2px(IMCenter.getInstance().getContext(), 6)))
                            .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                }
            } else {
                setViewVisible(view, readContainer, imgDestroyedText, privateLogo);
                options = RequestOptions.bitmapTransform(
                                new RoundedCorners(ScreenUtils.dip2px(IMCenter.getInstance().getContext(), 6)))
                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
            }

            if (view.getVisibility() == View.VISIBLE) {
                String path = thumUri.toString();
                if (message.getMediaUrl() != null && !message.getMediaUrl().equals("")) {
                    if (!message.getMediaUrl().toString().contains("ronghub.com")) {
                        path = message.getMediaUrl().toString();
                    }
                }

                Glide.with(view)
                        .load(path)
                        .error(
                                Glide.with(view)
                                        .load(path) // 第二次加载尝试（重试）
                                        .apply(options.timeout(15000))
                                        .error(R.drawable.image_error)
                        )
                        .apply(options.timeout(15000))
                        .into(view);
            }
        } else {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = ScreenUtils.dip2px(view.getContext(), 40);
            params.width = ScreenUtils.dip2px(view.getContext(), 35);
            view.setLayoutParams(params);
            view.setImageResource(R.drawable.image_error);
        }
    }

    private static void setViewVisible(ImageView view, LinearLayout readContainer, TextView imgDestroyedText, ImageView privateLogo) {
        view.setVisibility(View.VISIBLE);
        imgDestroyedText.setVisibility(View.GONE);
        readContainer.setVisibility(View.GONE);
        privateLogo.setVisibility(View.GONE);
    }

    @Override
    protected boolean onItemClick(
            ViewHolder holder,
            ImageMessage imageMessage,
            UiMessage uiMessage,
            int position,
            List<UiMessage> list,
            IViewProviderListener<UiMessage> listener) {
        InputMethodManager imm = (InputMethodManager) holder.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(holder.itemView.getWindowToken(), 0);

        if (uiMessage.getExpansion() != null) {
            Map<String, String> expansion = uiMessage.getExpansion();
            if (expansion.get("destructTime") != null && !Objects.equals(expansion.get("destructTime"), "")) {
                long validTime = RongConfigCenter.conversationConfig().rc_message_reading_burn_interval;
                long countDownTime = System.currentTimeMillis() - Long.parseLong(Objects.requireNonNull(expansion.get("destructTime")));
                if (countDownTime >= validTime) {
                    return true;
                } else {
                    FirebaseEventUtils.INSTANCE.logEvent(FirebaseEventTag.Chat_PP.name());
                    return !viewImage(holder, uiMessage);
                }
            } else {
                if (uiMessage.getMessageDirection().getValue() == 2) {
                    if (Objects.equals(expansion.get("isPrivate"), "true")) {
                        if (!BaseConfig.Companion.getGetInstance().getBoolean("firstClickPrivatePhoto", false)) {
                            new FirstClickPrivateDialog(holder.getContext(), 1, type -> {
                                if (type == 1) {
                                    BaseConfig.Companion.getGetInstance().setBoolean("firstClickPrivatePhoto", true);
                                } else {
                                    BaseConfig.Companion.getGetInstance().setBoolean("firstClickPrivateVideo", true);
                                }
                                clickItem(holder, imageMessage, uiMessage, expansion);
                            }).showPopupWindow();
                            return true;
                        }
                        clickItem(holder, imageMessage, uiMessage, expansion);

                        return true;
                    }
                }

            }

        }

        if (uiMessage.getMessageDirection().getValue() == 2) {//只记录用户发送的普通照片
            FirebaseEventUtils.INSTANCE.logEvent(FirebaseEventTag.Chat_Photo.name());
        }
        return !viewImage(holder, uiMessage);
    }

    private static boolean viewImage(ViewHolder holder, UiMessage uiMessage) {
        Message message = uiMessage.getMessage();
        if (message == null || message.getContent() == null) {
            RLog.e(TAG, "onItemClick error, message or message content is null");
            return true;
        }
        Intent intent = new Intent(holder.getContext(), PicturePagerActivity.class);
        intent.putExtra("message", uiMessage.getMessage());
        holder.getContext().startActivity(intent);
        return false;
    }

    /**
     * 判断Activity是否Destroy
     * @param mActivity
     * @return
     */
    public static boolean isDestroy(Activity mActivity) {
        return mActivity == null || mActivity.isFinishing() || mActivity.isDestroyed();
    }
    private void clickItem(ViewHolder holder, ImageMessage imageMessage, UiMessage uiMessage, Map<String, String> expansion) {
        String path = imageMessage.getThumUri().getPath();
        if (imageMessage.getMediaUrl() != null && !imageMessage.getMediaUrl().equals("")) {
            path = imageMessage.getMediaUrl().toString();
        }
        String finalPath = path;
        Message message = uiMessage.getMessage();
        if (message == null || message.getContent() == null) {
            RLog.e(TAG, "onItemClick error, message or message content is null");
            return;
        }
        String imageCode=path;
//        if (uiMessage.getContent()!=null){
//            if (uiMessage.getContent().getExtra()!= null){
//                String extra = uiMessage.getContent().getExtra();
//                if (JsonUtils.isJSON(extra)) {
//                    try {
//                        JSONObject jsonObject = new JSONObject(extra);
//                        imageCode= jsonObject.getString("imageCode");
//                    } catch (JSONException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            }
//        }
        HttpRequest.INSTANCE.getMemberReduce(holder.getContext(), uiMessage.getTargetId(),uiMessage.getMessage().getUId(), new HttpRequest.RequestCallBack() {
            @Override
            public void onSuccess() {
                expansion.put("destructTime", String.valueOf(System.currentTimeMillis()));
                RongIMClient.getInstance().updateMessageExpansion(expansion, uiMessage.getMessage().getUId(), new RongIMClient.OperationCallback() {
                    @Override
                    public void onSuccess() {
                        if (!isDestroy((Activity) holder.getContext())) {
                            if (holder.getContext() != null) {
                                holder.setVisible(R.id.private_logo, false);
                                holder.setVisible(R.id.read_container, true);
                                ImageView view = holder.getView(R.id.rc_image);
                                Glide.with(view)
                                        .load(finalPath)
                                        .error(R.drawable.image_error)
                                        .into(view);

                                RecallEditViewHolder viewHolder = (RecallEditViewHolder) holder;
                                viewHolder.messageId = String.valueOf(uiMessage.getMessage().getMessageId());
                                long validTime = RongConfigCenter.conversationConfig().rc_message_reading_burn_interval;
                                RecallEditManager.getInstance()
                                        .startCountDown(
                                                uiMessage.getMessage(),
                                                validTime,
                                                new RecallEditCountDownListener(viewHolder));
                            }
                        }

                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }
                });
                FirebaseEventUtils.INSTANCE.logEvent(FirebaseEventTag.Chat_PP.name());
                Intent intent = new Intent(holder.getContext(), PicturePagerActivity.class);
                intent.putExtra("message", uiMessage.getMessage());
                holder.getContext().startActivity(intent);
            }

            @Override
            public void onFailure(int code, @NonNull String msg) {

            }
        }, 2, 3,imageCode);
    }

    @Override
    protected boolean isMessageViewType(MessageContent messageContent) {
        return messageContent instanceof ImageMessage && !messageContent.isDestruct();
    }

    // 图片消息最小值为 100 X 100，最大值为 240 X 240
    // 重新梳理规则，如下：
    // 1、宽高任意一边小于 100 时，如：20 X 40 ，则取最小边，按比例放大到 100 进行显示，如最大边超过240 时，居中截取 240
    // 进行显示
    // 2、宽高都小于 240 时，大于 100 时，如：120 X 140 ，则取最长边，按比例放大到 240 进行显示
    // 3、宽高任意一边大于240时，分两种情况：
    // (1）如果宽高比没有超过 2.4，等比压缩，取长边 240 进行显示。
    // (2）如果宽高比超过 2.4，等比缩放（压缩或者放大），取短边 100，长边居中截取 240 进行显示。
    private void measureLayoutParams(View view, Drawable drawable) {
        if (view == null) {
            return;
        }
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        if (minSize == null) {
            minSize = THUMB_COMPRESSED_MIN_SIZE;
        }
        if (maxSize == null) {
            maxSize = THUMB_COMPRESSED_SIZE;
        }
        int finalWidth;
        int finalHeight;
        if (width < minSize || height < minSize) {
            if (width < height) {
                finalWidth = minSize;
                finalHeight = Math.min((int) (minSize * 1f / width * height), maxSize);
            } else {
                finalHeight = minSize;
                finalWidth = Math.min((int) (minSize * 1f / height * width), maxSize);
            }
        } else if (width < maxSize && height < maxSize) {
            finalWidth = width;
            finalHeight = height;
        } else {
            if (width > height) {
                if (width * 1f / height <= maxSize * 1.0f / minSize) {
                    finalWidth = maxSize;
                    finalHeight = (int) (maxSize * 1f / width * height);
                } else {
                    finalWidth = maxSize;
                    finalHeight = minSize;
                }
            } else {
                if (height * 1f / width <= maxSize * 1.0f / minSize) {
                    finalHeight = maxSize;
                    finalWidth = (int) (maxSize * 1f / height * width);
                } else {
                    finalHeight = maxSize;
                    finalWidth = minSize;
                }
            }
        }
        ViewGroup.LayoutParams params = view.getLayoutParams();
//        params.height = ScreenUtils.dip2px(view.getContext(), finalHeight / 2);
//        params.height = ScreenUtils.dip2px(view.getContext(), 149);
//        params.width = ScreenUtils.dip2px(view.getContext(), finalWidth / 2);
//        params.width = ScreenUtils.dip2px(view.getContext(), 112);
        view.setLayoutParams(params);
    }

    @Override
    public Spannable getSummarySpannable(Context context, ImageMessage imageMessage) {
        return new SpannableString(
                context.getString(R.string.rc_conversation_summary_content_image));
    }

    private class RecallEditViewHolder extends ViewHolder {
        public String messageId;

        public RecallEditViewHolder(Context context, View itemView) {
            super(context, itemView);
        }
    }

    private class RecallEditCountDownListener implements RecallEditCountDownCallBack {
        private WeakReference<RecallEditViewHolder> mHolder;

        public RecallEditCountDownListener(RecallEditViewHolder holder) {
            mHolder = new WeakReference<>(holder);
        }

        @Override
        public void onFinish(String messageId, Message mMessage) {
            RecallEditViewHolder viewHolder = mHolder.get();
            if (viewHolder != null && messageId.equals(viewHolder.messageId)) {
                viewHolder.getConvertView().findViewById(R.id.rc_image).setVisibility(View.GONE);
                viewHolder.getConvertView().findViewById(R.id.read_container).setVisibility(View.GONE);
                View imgDestroyedText = viewHolder.getConvertView().findViewById(R.id.img_destroyed_text);
                imgDestroyedText.setVisibility(View.VISIBLE);
                imgDestroyedText.setBackgroundResource(mMessage.getMessageDirection().getValue() == 2?R.drawable.icon_left_private_photo_destroyed:R.drawable.icon_private_photo_destroyed);
                imgDestroyedText.setPadding(mMessage.getMessageDirection().getValue() == 2? DesignUtil.dp2px(viewHolder.getContext(), 20f):0,0,mMessage.getMessageDirection().getValue() == 2?0:DesignUtil.dp2px(viewHolder.getContext(), 20f),0);

            }
        }

        @Override
        public void onTick(long untilFinished, String messageId) {
            RecallEditViewHolder viewHolder = mHolder.get();
            if (viewHolder != null && messageId.equals(viewHolder.messageId)) {
                ((TextView) viewHolder.getConvertView().findViewById(R.id.countdown_time)).setText(TimeUtils.formatTimeSeconds(untilFinished * 1000));
            }
        }
    }

}
