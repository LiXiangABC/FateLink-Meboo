package io.rong.imkit.conversation.messgelist.provider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.custom.base.config.BaseConfig;
import com.custom.base.manager.SDActivityManager;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.common.RLog;
import io.rong.imkit.IMCenter;
import io.rong.imkit.R;
import io.rong.imkit.config.RongConfigCenter;
import io.rong.imkit.dialog.FirstClickPrivateDialog;
import io.rong.imkit.event.FirebaseEventTag;
import io.rong.imkit.feature.recallEdit.RecallEditCountDownCallBack;
import io.rong.imkit.feature.recallEdit.RecallEditManager;
import io.rong.imkit.feature.resend.ResendManager;
import io.rong.imkit.http.HttpRequest;
import io.rong.imkit.model.UiMessage;
import io.rong.imkit.picture.tools.ScreenUtils;
import io.rong.imkit.utils.FirebaseEventUtils;
import io.rong.imkit.utils.JsonUtils;
import io.rong.imkit.utils.RongOperationPermissionUtils;
import io.rong.imkit.utils.TimeUtils;
import io.rong.imkit.widget.CircleProgressView;
import io.rong.imkit.widget.RCMessageFrameLayout;
import io.rong.imkit.widget.adapter.IViewProviderListener;
import io.rong.imkit.widget.adapter.ViewHolder;
import io.rong.imkit.widget.refresh.util.DesignUtil;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.SightMessage;
import jp.wasabeef.glide.transformations.BlurTransformation;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SightMessageItemProvider extends BaseMessageItemProvider<SightMessage> {

    // 应用模糊效果
    int radius = 15; // 模糊半径
    int sampling = 4; // 采用率

    private Integer minShortSideSize;

    public SightMessageItemProvider() {
        mConfig.showReadState = true;
        mConfig.showContentBubble = false;
        mConfig.showProgress = false;
    }

    @Override
    protected ViewHolder onCreateMessageContentViewHolder(ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rc_item_sight_message, parent, false);
        return new RecallEditViewHolder(view.getContext(), view);
    }

    @Override
    protected void bindMessageContentViewHolder(
            ViewHolder holder,
            ViewHolder parentHolder,
            SightMessage sightMessage,
            UiMessage uiMessage,
            int position,
            List<UiMessage> list,
            IViewProviderListener<UiMessage> listener) {
        int progress = uiMessage.getProgress();
        final Message.SentStatus status = uiMessage.getMessage().getSentStatus();

        Uri thumbUri = sightMessage.getThumbUri();
        if (thumbUri != null && thumbUri.getPath() != null) {
            final ImageView sightThumb = holder.getView(R.id.rc_sight_thumb);
            final LinearLayout readContainer = holder.getView(R.id.read_container);
            final ImageView sightTag = holder.getView(R.id.rc_sight_tag);
            final RoundedImageView privateLogo = holder.getView(R.id.private_logo);
            final TextView imgDestroyedText = holder.getView(R.id.img_destroyed_text);
            final CircleProgressView sightProgress = holder.getView(R.id.rc_sight_progress);
            final ProgressBar compressVideoBar = holder.getView(R.id.compressVideoBar);
            final RCMessageFrameLayout rcMessage = holder.getView(R.id.rc_message);
            if (!checkViewsValid(sightThumb)) {
                RLog.e(TAG, "checkViewsValid error," + uiMessage.getObjectName());
                return;
            }
            RequestOptions options = RequestOptions.bitmapTransform(new RoundedCorners(ScreenUtils.dip2px(IMCenter.getInstance().getContext(), 6)))
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
            if (uiMessage.getExpansion() != null) {
                Map<String, String> expansion = uiMessage.getExpansion();
                if ("true".equals(expansion.get("isPrivate"))) {
                    if (expansion.get("destructTime") != null && !Objects.equals(expansion.get("destructTime"), "")) {
                        long validTime = RongConfigCenter.conversationConfig().rc_message_reading_burn_interval;
                        long countDownTime = System.currentTimeMillis() - Long.parseLong(Objects.requireNonNull(expansion.get("destructTime")));
                        if (countDownTime < validTime) {
                            readContainer.setVisibility(View.VISIBLE);
                            sightTag.setVisibility(View.VISIBLE);
                            sightThumb.setVisibility(View.VISIBLE);
                            imgDestroyedText.setVisibility(View.GONE);
                            privateLogo.setVisibility(View.GONE);
                            sightProgress.setVisibility(View.GONE);
                            compressVideoBar.setVisibility(View.GONE);
                            rcMessage.setVisibility(View.VISIBLE);

                            RecallEditViewHolder viewHolder = (RecallEditViewHolder) holder;
                            viewHolder.messageId = String.valueOf(uiMessage.getMessage().getMessageId());
                            RecallEditManager.getInstance()
                                    .startCountDown(
                                            uiMessage.getMessage(),
                                            validTime - countDownTime,
                                            new RecallEditCountDownListener(viewHolder));
                        } else {
                            imgDestroyedText.setVisibility(View.VISIBLE);
                            readContainer.setVisibility(View.GONE);
                            sightTag.setVisibility(View.GONE);
                            sightThumb.setVisibility(View.GONE);
                            privateLogo.setVisibility(View.GONE);
                            sightProgress.setVisibility(View.GONE);
                            compressVideoBar.setVisibility(View.GONE);
                            rcMessage.setVisibility(View.GONE);
                            imgDestroyedText.setBackgroundResource(uiMessage.getMessageDirection().getValue() == 2?R.drawable.icon_left_private_photo_destroyed:R.drawable.icon_private_photo_destroyed);
                            imgDestroyedText.setPadding(uiMessage.getMessageDirection().getValue() == 2?DesignUtil.dp2px(holder.getContext(), 20f):0,0,uiMessage.getMessageDirection().getValue() == 2?0:DesignUtil.dp2px(holder.getContext(), 20f),0);
                        }

                    }else {
                        sightThumb.setVisibility(View.VISIBLE);
                        privateLogo.setVisibility(View.VISIBLE);
                        rcMessage.setVisibility(View.VISIBLE);
                        readContainer.setVisibility(View.GONE);
                        sightTag.setVisibility(View.GONE);
                        imgDestroyedText.setVisibility(View.GONE);
                        sightProgress.setVisibility(View.GONE);
                        compressVideoBar.setVisibility(View.GONE);
                        //如果该条消息带有radius值，则去消息内的radius值，否则取本地值
                        if (expansion.get("radius")!= null){
                            radius = Integer.parseInt(Objects.requireNonNull(expansion.get("radius")));
                            sampling = Integer.parseInt(Objects.requireNonNull(expansion.get("radius")));
                        }
                        options = RequestOptions.bitmapTransform(new BlurTransformation(radius, sampling))
                                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                    }
                } else {
                    privateLogo.setVisibility(View.VISIBLE);
                    setViewVisible(rcMessage, sightTag, sightThumb, imgDestroyedText, readContainer, sightProgress, compressVideoBar);
                }
            } else {
                privateLogo.setVisibility(View.GONE);
                setViewVisible(rcMessage, sightTag, sightThumb, imgDestroyedText, readContainer, sightProgress, compressVideoBar);
            }


            // 融云的存储链接包含https，所以无法单独用https做融云视频与APP视频的区分，且融云的存储链接存在过期行为，直接用MediaUrl会导致视频消息不展示封面
            // https://sight-short-aws-or.ronghub.com/QVFTWwcHUQVBWgBCWwYIAgsGAQIKBAUGBQYBBwcFAAIDAAIBBAYJBgYyNzAy.mp4
            if (holder.getView(R.id.rc_sight_thumb).getVisibility()==View.VISIBLE) {
                File file = new File(thumbUri.getPath());
                Glide.with(sightThumb)
                        .load(sightMessage.getMediaUrl()!= null ?!sightMessage.getMediaUrl().toString().contains("ronghub.com")? sightMessage.getMediaUrl().toString():file : file)
                        .apply(options)
                        .into(sightThumb);
                holder.setText(R.id.rc_sight_duration, getSightDuration(sightMessage.getDuration()));
                CircleProgressView loadingProgress = holder.getView(R.id.rc_sight_progress);
                ProgressBar compressProgress = holder.getView(R.id.compressVideoBar);
                if (!checkViewsValid(loadingProgress, compressProgress)) {
                    RLog.e(TAG, "checkViewsValid error," + uiMessage.getObjectName());
                    return;
                }
                int visibility = holder.getView(R.id.rc_sight_tag).getVisibility();
                int thumbVisibility = holder.getView(R.id.rc_sight_thumb).getVisibility();
                if (visibility != View.GONE) {
                    if (progress > 0 && progress < 100) {
                        loadingProgress.setProgress(progress, true);
                        holder.setVisible(R.id.rc_sight_tag, false);
                        loadingProgress.setVisibility(View.VISIBLE);
                        compressProgress.setVisibility(View.GONE);
                        // 发送小视频时取消发送，这个功能暂时未打开
                        // handleSendingView(message, holder);
                    } else if (status.equals(Message.SentStatus.SENDING)) {
                        holder.setVisible(R.id.rc_sight_tag, false);
                        loadingProgress.setVisibility(View.GONE);
                        compressProgress.setVisibility(View.VISIBLE);
                    } else if (status.equals(Message.SentStatus.FAILED)
                            && ResendManager.getInstance().needResend(uiMessage.getMessage().getMessageId())) {
                        holder.setVisible(R.id.rc_sight_tag, false);
                        loadingProgress.setVisibility(View.GONE);
                        compressProgress.setVisibility(View.VISIBLE);
                    } else {
                        holder.setVisible(R.id.rc_sight_tag, true);
                        loadingProgress.setVisibility(View.GONE);
                        compressProgress.setVisibility(View.GONE);
                        // handleSendingView(message, holder);
                    }
                }
            }
        }
    }

    /**
     * 控件展示
     * @param rcMessage
     * @param sightTag
     * @param sightThumb
     * @param imgDestroyedText
     * @param readContainer
     * @param sightProgress
     * @param compressVideoBar
     */
    private static void setViewVisible(RCMessageFrameLayout rcMessage, ImageView sightTag, ImageView sightThumb, TextView imgDestroyedText, LinearLayout readContainer, CircleProgressView sightProgress, ProgressBar compressVideoBar) {
        rcMessage.setVisibility(View.VISIBLE);
        sightTag.setVisibility(View.VISIBLE);
        sightThumb.setVisibility(View.VISIBLE);
        imgDestroyedText.setVisibility(View.GONE);
        readContainer.setVisibility(View.GONE);
        sightProgress.setVisibility(View.GONE);
        compressVideoBar.setVisibility(View.GONE);
    }

    /**
     * 判断Activity是否Destroy
     * @param mActivity
     * @return
     */
    public static boolean isDestroy(Activity mActivity) {
        return mActivity == null || mActivity.isFinishing() || mActivity.isDestroyed();
    }
    @Override
    protected boolean onItemClick(ViewHolder holder, SightMessage sightMessage, UiMessage uiMessage, int position, List<UiMessage> list, IViewProviderListener<UiMessage> listener) {
        if (sightMessage != null) {
            if (!RongOperationPermissionUtils.isMediaOperationPermit(holder.getContext())) {
                return true;
            }
            InputMethodManager imm = (InputMethodManager) holder.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(holder.itemView.getWindowToken(), 0);

            if (uiMessage.getExpansion() != null) {
                Map<String, String> expansion = uiMessage.getExpansion();
                if (expansion.get("destructTime") != null && !Objects.equals(expansion.get("destructTime"), "")) {
                    long validTime = RongConfigCenter.conversationConfig().rc_message_reading_burn_interval;
                    long countDownTime = System.currentTimeMillis() - Long.parseLong(Objects.requireNonNull(expansion.get("destructTime")));
                    if (countDownTime < validTime) {
                        FirebaseEventUtils.INSTANCE.logEvent(FirebaseEventTag.Chat_PV.name());
                        return !startVideo(holder, sightMessage, uiMessage);
                    }
                    return true;
                } else {
                    if (uiMessage.getMessageDirection().getValue() == 2) {
                        if (expansion.get("isPrivate").equals("true")) {
                            FirebaseEventUtils.INSTANCE.logEvent(FirebaseEventTag.Chat_PV.name());
                            String imageCode=sightMessage.getMediaUrl().toString();
//                            if (uiMessage.getContent()!=null){
//                                if (uiMessage.getContent().getExtra()!= null){
//                                    String extra = uiMessage.getContent().getExtra();
//                                    if (JsonUtils.isJSON(extra)) {
//                                        try {
//                                            JSONObject jsonObject = new JSONObject(extra);
//                                            imageCode= jsonObject.getString("imageCode");
//                                        } catch (JSONException e) {
//                                            throw new RuntimeException(e);
//                                        }
//                                    }
//                                }
//                            }
                            if (!BaseConfig.Companion.getGetInstance().getBoolean("firstClickPrivateVideo", false)) {
                                String finalImageCode = imageCode;
                                new FirstClickPrivateDialog(holder.getContext(), 2, type -> {
                                    if (type == 1) {
                                        BaseConfig.Companion.getGetInstance().setBoolean("firstClickPrivatePhoto", true);
                                    } else {
                                        BaseConfig.Companion.getGetInstance().setBoolean("firstClickPrivateVideo", true);
                                    }
                                    clickHttpRequest(holder, sightMessage, uiMessage, expansion, finalImageCode);

                                }).showPopupWindow();
                                return true;
                            }
                            clickHttpRequest(holder, sightMessage, uiMessage, expansion, imageCode);
                            return true;
                        }
                    }
                }
            }
            // KNOTE: 2021/8/24  点击进入SightPlayerActivity下载播放,下载保存目录是应用私有目录  不需要存储权限
            //            String[] permissions = {
            //                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            //            };
            //            if (!PermissionCheckUtil.checkPermissions(holder.getContext(),
            // permissions)) {
            //                Activity activity = (Activity) holder.getContext();
            //                PermissionCheckUtil.requestPermissions(activity, permissions, 100);
            //                return true;
            //            }

            if (uiMessage.getMessageDirection().getValue() == 2) {
                FirebaseEventUtils.INSTANCE.logEvent(FirebaseEventTag.Chat_Video.name());
            }
            return !startVideo(holder, sightMessage, uiMessage);
        }
        return false;
    }

    private static boolean startVideo(ViewHolder holder, SightMessage sightMessage, UiMessage uiMessage) {
        Message message = uiMessage.getMessage();
        if (message == null || message.getContent() == null) {
            RLog.e(TAG, "onItemClick error, message or message content is null");
            return true;
        }
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("rong")
                .authority(holder.getContext().getPackageName())
                .appendPath("sight")
                .appendPath("player");
        String intentUrl = builder.build().toString();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(intentUrl));
        intent.setPackage(holder.getContext().getPackageName());
        intent.putExtra("SightMessage", sightMessage);
        intent.putExtra("Message", uiMessage.getMessage());
        intent.putExtra("Progress", uiMessage.getProgress());
        if (intent.resolveActivity(holder.getContext().getPackageManager()) != null) {
            holder.getContext().startActivity(intent);
        } else {
            Toast.makeText(holder.getContext(),"Sight Module does not exist.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void clickHttpRequest(ViewHolder holder, SightMessage sightMessage, UiMessage uiMessage, Map<String, String> expansion, String imageCode) {
        HttpRequest.INSTANCE.getMemberReduce(holder.getContext(), uiMessage.getTargetId(),uiMessage.getMessage().getUId(), new HttpRequest.RequestCallBack() {
            @Override
            public void onSuccess() {
                expansion.put("destructTime", String.valueOf(System.currentTimeMillis()));
                RongIMClient.getInstance().updateMessageExpansion(expansion, uiMessage.getMessage().getUId(), new RongIMClient.OperationCallback() {
                    @Override
                    public void onSuccess() {
                        if (!isDestroy((Activity) holder.getContext())) {

                            holder.setVisible(R.id.private_logo, false);
                            holder.setVisible(R.id.compressVideoBar, false);
                            holder.setVisible(R.id.rc_sight_progress, false);
                            holder.setVisible(R.id.rc_sight_tag, true);
                            holder.setVisible(R.id.read_container, true);
                            holder.setVisible(R.id.rc_sight_thumb, true);
                            ImageView view = holder.getView(R.id.rc_sight_thumb);
                            Uri thumbUri = sightMessage.getThumbUri();
                            File file = new File(thumbUri.getPath());
                            Log.e(TAG, "onSuccess: " + sightMessage.getMediaUrl());
                            if (view != null) {
                                Glide.with(view)
                                        .load(sightMessage.getMediaUrl() != null ? !sightMessage.getMediaUrl().toString().contains("ronghub.com") ? sightMessage.getMediaUrl().toString() : file : file)
                                        .into(view);
                            }

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

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }
                });
                Message message = uiMessage.getMessage();
                if (message == null || message.getContent() == null) {
                    RLog.e(TAG, "onItemClick error, message or message content is null");
                    return;
                }

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("rong")
                        .authority(holder.getContext().getPackageName())
                        .appendPath("sight")
                        .appendPath("player");
                String intentUrl = builder.build().toString();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(intentUrl));
                intent.setPackage(holder.getContext().getPackageName());
                intent.putExtra("SightMessage", sightMessage);
                intent.putExtra("Message", uiMessage.getMessage());
                intent.putExtra("Progress", uiMessage.getProgress());
                if (intent.resolveActivity(holder.getContext().getPackageManager()) != null) {
                    holder.getContext().startActivity(intent);
                } else {
                    Toast.makeText(holder.getContext(), "Sight Module does not exist.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int code, @NonNull String msg) {

            }
        }, 3,4,imageCode);
    }

    @Override
    protected boolean isMessageViewType(MessageContent messageContent) {
        return messageContent instanceof SightMessage && !messageContent.isDestruct();
    }

    private void measureLayoutParams(View view, ImageView readyButton, Drawable drawable) {
        float width = drawable.getIntrinsicWidth();
        float height = drawable.getIntrinsicHeight();
        int finalWidth;
        int finalHeight;
        int minSize = 100;
        if (minShortSideSize == null) {
            minShortSideSize = ScreenUtils.dip2px(view.getContext(), 140);
        }
        if (minShortSideSize > 0) {
            if (width >= minShortSideSize || height >= minShortSideSize) {
                float scale = width / height;

                if (scale > 1) {
                    finalHeight = (int) (minShortSideSize / scale);
                    if (finalHeight < minSize) {
                        finalHeight = minSize;
                    }
                    finalWidth = (int) minShortSideSize;
                } else {
                    finalHeight = (int) minShortSideSize;
                    finalWidth = (int) (minShortSideSize * scale);
                    if (finalWidth < minSize) {
                        finalWidth = minSize;
                    }
                }

                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.height = finalHeight;
                params.width = finalWidth;
                view.setLayoutParams(params);
                measureReadyButton(readyButton, drawable, finalWidth, finalHeight);
            } else {
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.height = (int) height;
                params.width = (int) width;
                view.setLayoutParams(params);
                measureReadyButton(readyButton, drawable, width, height);
            }
        }
    }

    private void measureReadyButton(
            ImageView readyButton, Drawable drawable, float finalWidth, float finalHeight) {
        if (readyButton == null || drawable == null) {
            return;
        }
        int intrinsicHeight = drawable.getIntrinsicHeight();
        int intrinsicWidth = drawable.getIntrinsicWidth();
        if (intrinsicHeight == 0 || intrinsicWidth == 0 || finalHeight == 0 || finalWidth == 0) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = readyButton.getLayoutParams();
        int readyButtonSize;
        if ((intrinsicWidth / (finalWidth * 1.0)) > (intrinsicHeight / (finalHeight * 1.0))) {
            readyButtonSize = (int) (finalHeight * (intrinsicHeight / (intrinsicWidth * 1.0)));
        } else {
            readyButtonSize = (int) (finalWidth * (intrinsicWidth / (intrinsicHeight * 1.0)));
        }
        int min =
                Math.min(
                        readyButtonSize,
                        readyButton
                                .getResources()
                                .getDimensionPixelSize(R.dimen.rc_sight_play_size));
        layoutParams.width = min;
        layoutParams.height = min;
        readyButton.setLayoutParams(layoutParams);
    }

    private String getSightDuration(int time) {
        String recordTime;
        int hour, minute, second;
        if (time <= 0) {
            return "00:00";
        } else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                recordTime = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99) {
                    return "99:59:59";
                }
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                recordTime = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return recordTime;
    }

    private String unitFormat(int time) {
        String formatTime;
        if (time >= 0 && time < 10) {
            formatTime = "0" + time;
        } else {
            formatTime = "" + time;
        }
        return formatTime;
    }

    @Override
    public Spannable getSummarySpannable(Context context, SightMessage sightMessage) {
        return new SpannableString(
                context.getString(R.string.rc_conversation_summary_content_sight));
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
                viewHolder.getConvertView().findViewById(R.id.rc_message).setVisibility(View.GONE);
                viewHolder.getConvertView().findViewById(R.id.read_container).setVisibility(View.GONE);
                viewHolder.getConvertView().findViewById(R.id.img_destroyed_text).setVisibility(View.VISIBLE);
                viewHolder.getConvertView().findViewById(R.id.img_destroyed_text).setBackgroundResource(mMessage.getMessageDirection().getValue() == 2?R.drawable.icon_left_private_photo_destroyed:R.drawable.icon_private_photo_destroyed);
                viewHolder.getConvertView().findViewById(R.id.img_destroyed_text).setPadding(mMessage.getMessageDirection().getValue() == 2?DesignUtil.dp2px(viewHolder.getContext(), 20f):0,0,mMessage.getMessageDirection().getValue() == 2?0:DesignUtil.dp2px(viewHolder.getContext(), 20f),0);

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
