package io.rong.imkit.conversation;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.custom.base.config.BaseConfig;
import com.custom.base.http.SDOkHttpResoutCallBack;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.makeramen.roundedimageview.RoundedImageView;
import com.sunday.eventbus.SDEventManager;

import io.rong.common.CollectionUtils;
import io.rong.common.RLog;
import io.rong.imkit.IMCenter;
import io.rong.imkit.R;
import io.rong.imkit.RongIM;
import io.rong.imkit.SpName;
import io.rong.imkit.activity.Activities;
import io.rong.imkit.config.RongConfigCenter;
import io.rong.imkit.conversation.extension.InputMode;
import io.rong.imkit.conversation.extension.RongExtension;
import io.rong.imkit.conversation.extension.RongExtensionViewModel;
import io.rong.imkit.conversation.messgelist.processor.IConversationUIRenderer;
import io.rong.imkit.conversation.messgelist.status.MessageProcessor;
import io.rong.imkit.conversation.messgelist.viewmodel.MessageViewModel;
import io.rong.imkit.dialog.CustomDialog;
import io.rong.imkit.dialog.UserProfileOperationDialog;
import io.rong.imkit.dialog.UserProfileOperationReportDialog;
import io.rong.imkit.entity.BaseResutlEntity;
import io.rong.imkit.entity.OpenChatEntity;
import io.rong.imkit.entity.TipsEntity;
import io.rong.imkit.entity.TipsPopEntity;
import io.rong.imkit.event.EnumEventTag;
import io.rong.imkit.event.Event;
import io.rong.imkit.event.FirebaseEventTag;
import io.rong.imkit.event.uievent.PageDestroyEvent;
import io.rong.imkit.event.uievent.PageEvent;
import io.rong.imkit.event.uievent.ScrollEvent;
import io.rong.imkit.event.uievent.ScrollMentionEvent;
import io.rong.imkit.event.uievent.ScrollToEndEvent;
import io.rong.imkit.event.uievent.ShowLoadMessageDialogEvent;
import io.rong.imkit.event.uievent.ShowLongClickDialogEvent;
import io.rong.imkit.event.uievent.ShowWarningDialogEvent;
import io.rong.imkit.event.uievent.SmoothScrollEvent;
import io.rong.imkit.event.uievent.ToastEvent;
import io.rong.imkit.feature.location.LocationUiRender;
import io.rong.imkit.feature.reference.ReferenceManager;
import io.rong.imkit.http.HttpRequest;
import io.rong.imkit.manager.MessageProviderPermissionHandler;
import io.rong.imkit.manager.hqvoicemessage.HQVoiceMsgDownloadManager;
import io.rong.imkit.model.UiMessage;
import io.rong.imkit.picture.PictureSelector;
import io.rong.imkit.picture.config.PictureConfig;
import io.rong.imkit.picture.entity.LocalMedia;
import io.rong.imkit.picture.tools.DoubleUtils;
import io.rong.imkit.picture.tools.FileUtils;
import io.rong.imkit.picture.tools.ToastUtils;
import io.rong.imkit.userinfo.RongUserInfoManager;
import io.rong.imkit.utils.ChatMessageNumber;
import io.rong.imkit.utils.FirebaseEventUtils;
import io.rong.imkit.utils.PermissionCheckUtil;
import io.rong.imkit.utils.RongViewUtils;
import io.rong.imkit.utils.RouteUtils;
import io.rong.imkit.utils.UploadPhoto;
import io.rong.imkit.widget.FixedLinearLayoutManager;
import io.rong.imkit.widget.TagCloudView;
import io.rong.imkit.widget.adapter.BaseAdapter;
import io.rong.imkit.widget.adapter.IViewProviderListener;
import io.rong.imkit.widget.adapter.ViewHolder;
import io.rong.imkit.widget.refresh.SmartRefreshLayout;
import io.rong.imkit.widget.refresh.api.RefreshLayout;
import io.rong.imkit.widget.refresh.constant.RefreshState;
import io.rong.imkit.widget.refresh.listener.OnLoadMoreListener;
import io.rong.imkit.widget.refresh.listener.OnRefreshListener;
import io.rong.imkit.widget.refresh.wrapper.RongRefreshHeader;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.ConversationIdentifier;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.TextMessage;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author lvz
 */
public class ConversationFragment extends Fragment
        implements OnRefreshListener,
        View.OnClickListener,
        OnLoadMoreListener,
        IViewProviderListener<UiMessage> {
    /**
     * 开启合并转发的选择会话界面
     */
    public static final int REQUEST_CODE_FORWARD = 104;

    private static final int REQUEST_MSG_DOWNLOAD_PERMISSION = 1000;
    private final String TAG = ConversationFragment.class.getSimpleName();
    protected SmartRefreshLayout mRefreshLayout;
    protected RecyclerView mList;
    protected RecyclerView.LayoutManager mLinearLayoutManager;
    protected MessageListAdapter mAdapter;
    protected MessageViewModel mMessageViewModel;
    protected RongExtensionViewModel mRongExtensionViewModel;
    protected RongExtension mRongExtension;
    protected TextView mRcEmptyText;
    protected TextView mNewMessageNum;
    protected TextView mUnreadHistoryMessageNum;
    protected TextView mUnreadMentionMessageNum;
    protected int activitySoftInputMode = 0;
    // 滑动结束是否
    protected boolean onScrollStopRefreshList = false;
    private boolean bindToConversation = false;
    //头部view
    private View inflate;
    private boolean isfirstLoad = true;
    //openChat数据
    private OpenChatEntity.Data openChatData;
    //会话id
    private String mTargetId;

    public OpenChatEntity.Data getOpenChatData() {
        return openChatData;
    }

    public void setOpenChatData(OpenChatEntity.Data openChatData) {
        this.openChatData = openChatData;
    }

    public String getTargetId() {
        return mTargetId;
    }

    public void setTargetId(String mTargetId) {
        this.mTargetId = mTargetId;
    }

    Observer<List<UiMessage>> mListObserver =
            new Observer<List<UiMessage>>() {
                @Override
                public void onChanged(List<UiMessage> uiMessages) {
                    refreshList(uiMessages);
                    if (isfirstLoad) {
                        mList.scrollToPosition(mAdapter.getItemCount() - 1);
                        mList.setVisibility(View.VISIBLE);
                        isfirstLoad = false;
                    } else {
                        if (uiMessages != null && uiMessages.size() > 0) {
                            UiMessage uiMessage = uiMessages.get(uiMessages.size() - 1);
                            boolean isSender = uiMessage.getMessage().getMessageDirection().equals(Message.MessageDirection.SEND);
                            if (isSender && !DoubleUtils.isFastDoubleClick(500)) {
                                ChatMessageNumber.INSTANCE.setUserMessageNum(ChatMessageNumber.INSTANCE.getUserMessageNum() + 1);
                                ChatMessageNumber.INSTANCE.saveTodayMessageNumber(uiMessage.getMessage().getTargetId());
                                showTipsDialog(getOpenChatData());
                                sendTips(getOpenChatData());
                            }
                        }
                    }
                }
            };
    Observer<Integer> mNewMessageUnreadObserver =
            new Observer<Integer>() {
                @Override
                public void onChanged(Integer count) {
                    if (RongConfigCenter.conversationConfig()
                            .isShowNewMessageBar(mMessageViewModel.getCurConversationType())) {
                        if (count != null && count > 0) {
                            mNewMessageNum.setVisibility(View.VISIBLE);
                            mNewMessageNum.setText(count > 99 ? "99+" : String.valueOf(count));
                        } else {
                            mNewMessageNum.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            };

    Observer<Integer> mHistoryMessageUnreadObserver =
            new Observer<Integer>() {
                @Override
                public void onChanged(Integer count) {
                    if (RongConfigCenter.conversationConfig()
                            .isShowHistoryMessageBar(mMessageViewModel.getCurConversationType())) {
                        if (count != null && count > 0) {
                            mUnreadHistoryMessageNum.setVisibility(View.VISIBLE);
                            mUnreadHistoryMessageNum.setText(
                                    MessageFormat.format(
                                            getString(R.string.rc_unread_message),
                                            count > 99 ? "99+" : count));
                        } else {
                            mUnreadHistoryMessageNum.setVisibility(View.GONE);
                        }
                    }
                }
            };
    Observer<Integer> mNewMentionMessageUnreadObserver =
            new Observer<Integer>() {
                @Override
                public void onChanged(Integer count) {
                    if (RongConfigCenter.conversationConfig()
                            .isShowNewMentionMessageBar(
                                    mMessageViewModel.getCurConversationType())) {
                        if (count != null && count > 0) {
                            mUnreadMentionMessageNum.setVisibility(View.VISIBLE);
                            mUnreadMentionMessageNum.setText(
                                    getString(R.string.rc_mention_messages, "(" + count + ")"));
                        } else {
                            mUnreadMentionMessageNum.setVisibility(View.GONE);
                        }
                    }
                }
            };
    Observer<PageEvent> mPageObserver =
            new Observer<PageEvent>() {
                @Override
                public void onChanged(PageEvent event) {
                    // 优先透传给各模块的 view 处理中心进行处理，如果返回 true, 代表事件被消费，不再处理。
                    for (IConversationUIRenderer processor :
                            RongConfigCenter.conversationConfig().getViewProcessors()) {
                        if (processor.handlePageEvent(event)) {
                            return;
                        }
                    }
                    if (event instanceof Event.RefreshEvent) {
                        if (((Event.RefreshEvent) event).state.equals(RefreshState.RefreshFinish)) {
                            mRefreshLayout.finishRefresh();
                        } else if (((Event.RefreshEvent) event)
                                .state.equals(RefreshState.LoadFinish)) {
                            mRefreshLayout.finishLoadMore();
                        }
                    } else if (event instanceof ToastEvent) {
                        String msg = ((ToastEvent) event).getMessage();
                        if (!TextUtils.isEmpty(msg)) {
                            if (Activities.Companion.get().getTop()!=null){
                                Toast.makeText(Activities.Companion.get().getTop(), msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else if (event instanceof ScrollToEndEvent) {
                        mList.scrollToPosition(mAdapter.getItemCount() - 1);
                    } else if (event instanceof ScrollMentionEvent) {
                        mMessageViewModel.onScrolled(
                                mList,
                                0,
                                0,
                                mAdapter.getHeadersCount(),
                                mAdapter.getFootersCount());
                    } else if (event instanceof ScrollEvent) {
                        if (mList.getLayoutManager() instanceof LinearLayoutManager) {
                            ((LinearLayoutManager) mList.getLayoutManager())
                                    .scrollToPositionWithOffset(
                                            mAdapter.getHeadersCount()
                                                    + ((ScrollEvent) event).getPosition(),
                                            0);
                        }
                    } else if (event instanceof SmoothScrollEvent) {
                        if (mList.getLayoutManager() instanceof LinearLayoutManager) {
                            ((LinearLayoutManager) mList.getLayoutManager())
                                    .scrollToPositionWithOffset(
                                            mAdapter.getHeadersCount()
                                                    + ((SmoothScrollEvent) event).getPosition(),
                                            0);
                        }
                    } else if (event instanceof ShowLongClickDialogEvent) {
//                        final MessageItemLongClickBean bean =
//                                ((ShowLongClickDialogEvent) event).getBean();
//                        final List<MessageItemLongClickAction> messageItemLongClickActions =
//                                bean.getMessageItemLongClickActions();
//                        Collections.sort(
//                                messageItemLongClickActions,
//                                new Comparator<MessageItemLongClickAction>() {
//                                    @Override
//                                    public int compare(
//                                            MessageItemLongClickAction lhs,
//                                            MessageItemLongClickAction rhs) {
//                                        // desc sort
//                                        return rhs.priority - lhs.priority;
//                                    }
//                                });
//                        List<String> titles = new ArrayList<>();
//                        for (MessageItemLongClickAction action : messageItemLongClickActions) {
//                            titles.add(action.getTitle(getContext()));
//                        }
//
//                        OptionsPopupDialog dialog =
//                                OptionsPopupDialog.newInstance(
//                                                getContext(),
//                                                titles.toArray(new String[titles.size()]))
//                                        .setOptionsPopupDialogListener(
//                                                new OptionsPopupDialog
//                                                        .OnOptionsItemClickedListener() {
//                                                    @Override
//                                                    public void onOptionsItemClicked(int which) {
//                                                        messageItemLongClickActions
//                                                                .get(which)
//                                                                .listener
//                                                                .onMessageItemLongClick(
//                                                                        getContext(),
//                                                                        bean.getUiMessage());
//                                                    }
//                                                });
//                        MessageItemLongClickActionManager.getInstance().setLongClickDialog(dialog);
//                        MessageItemLongClickActionManager.getInstance()
//                                .setLongClickMessage(bean.getUiMessage().getMessage());
//                        dialog.setOnDismissListener(
//                                new DialogInterface.OnDismissListener() {
//                                    @Override
//                                    public void onDismiss(DialogInterface dialog) {
//                                        MessageItemLongClickActionManager.getInstance()
//                                                .setLongClickDialog(null);
//                                        MessageItemLongClickActionManager.getInstance()
//                                                .setLongClickMessage(null);
//                                    }
//                                });
//                        dialog.show();
                    } else if (event instanceof PageDestroyEvent) {
                        FragmentManager fm = getChildFragmentManager();
                        if (fm.getBackStackEntryCount() > 0) {
                            fm.popBackStack();
                        } else {
                            if (!isDestroy(getActivity())) {
                                getActivity().finish();
                            }
                        }
                    } else if (event instanceof ShowWarningDialogEvent) {
                        onWarningDialog(((ShowWarningDialogEvent) event).getMessage());
                    } else if (event instanceof ShowLoadMessageDialogEvent) {
                        showLoadMessageDialog(
                                ((ShowLoadMessageDialogEvent) event).getCallback(),
                                ((ShowLoadMessageDialogEvent) event).getList());
                    }
                }
            };
    private LinearLayout mNotificationContainer;
    private ImageView mImgConversationBg;
    private boolean onViewCreated = false;
    private boolean mDisableSystemEmoji;
    private Bundle mBundle;
    private ConversationIdentifier conversationIdentifier;

    private boolean historyMessageListEmpty = false;
    private final RecyclerView.OnScrollListener mScrollListener =
            new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    mMessageViewModel.onScrolled(
                            recyclerView,
                            dx,
                            dy,
                            mAdapter.getHeadersCount(),
                            mAdapter.getFootersCount());
                }

                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && onScrollStopRefreshList) {
                        onScrollStopRefreshList = false;
                        RLog.d(TAG, "onScrollStateChanged refresh List");
                        refreshList(mMessageViewModel.getUiMessageLiveData().getValue());
                    }
                }
            };

    {
        mAdapter = onResolveAdapter();
    }

    public void initConversation(
            String targetId, Conversation.ConversationType conversationType, Bundle bundle) {
        if (onViewCreated) {
            bindConversation(
                    ConversationIdentifier.obtain(conversationType, targetId, ""), false, bundle);
        } else {
            conversationIdentifier = ConversationIdentifier.obtain(conversationType, targetId, "");
            mBundle = bundle;
        }
    }

    private void bindConversation(
            ConversationIdentifier conversationIdentifier,
            boolean disableSystemEmoji,
            Bundle bundle) {
        if (conversationIdentifier.getType() != null
                && !TextUtils.isEmpty(conversationIdentifier.getTargetId())) {
            for (IConversationUIRenderer processor :
                    RongConfigCenter.conversationConfig().getViewProcessors()) {
                processor.init(
                        this,
                        mRongExtension,
                        conversationIdentifier.getType(),
                        conversationIdentifier.getTargetId());
            }
            mRongExtension.bindToConversation(this, conversationIdentifier, disableSystemEmoji);
            mMessageViewModel.bindConversation(conversationIdentifier, bundle);
            subscribeUi();
            bindToConversation = true;
        } else {
            RLog.e(
                    TAG,
                    "Invalid intent data !!! Must put targetId and conversation type to intent.");
        }
    }

    private void subscribeUi() {
        RongIMClient.getInstance().setMessageExpansionListener(new RongIMClient.MessageExpansionListener() {
            @Override
            public void onMessageExpansionUpdate(Map<String, String> expansion, Message message) {
                Log.e(TAG, "onMessageExpansionUpdate: " + expansion + "----" + mMessageViewModel.getUiMessages());
                IMCenter.getInstance().refreshMessage(message);

            }

            @Override
            public void onMessageExpansionRemove(List<String> keyArray, Message message) {

            }
        });

        mMessageViewModel.getPageEventLiveData().observeForever(mPageObserver);
        mMessageViewModel.getUiMessageLiveData().observeForever(mListObserver);
        mMessageViewModel
                .getNewMessageUnreadLiveData()
                .observe(getViewLifecycleOwner(), mNewMessageUnreadObserver);
        mMessageViewModel
                .getHistoryMessageUnreadLiveData()
                .observe(getViewLifecycleOwner(), mHistoryMessageUnreadObserver);
        mMessageViewModel
                .getNewMentionMessageUnreadLiveData()
                .observe(getViewLifecycleOwner(), mNewMentionMessageUnreadObserver);
        mRongExtensionViewModel
                .getExtensionBoardState()
                .observe(
                        getViewLifecycleOwner(),
                        new Observer<Boolean>() {
                            @Override
                            public void onChanged(final Boolean value) {
                                RLog.d(TAG, "scroll to the bottom:" + mAdapter.getData().size());
                                if (historyMessageListEmpty) {
                                    mRcEmptyText.setVisibility(value ? View.GONE : mAdapter.getData().size() == 0 ? View.VISIBLE : View.GONE);
                                }
                                mList.postDelayed(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                InputMode inputMode =
                                                        mRongExtensionViewModel
                                                                .getInputModeLiveData()
                                                                .getValue();
                                                if (!Objects.equals(
                                                        inputMode, InputMode.MoreInputMode)
                                                        && Boolean.TRUE.equals(value)) {
                                                    if (mMessageViewModel.isNormalState()) {
                                                        mList.scrollToPosition(
                                                                mAdapter.getItemCount() - 1);
                                                    } else if (!mMessageViewModel
                                                            .isHistoryState()) {
                                                        mMessageViewModel.newMessageBarClick();
                                                    }
                                                }
                                            }
                                        },
                                        150);
                            }
                        });

    }

    @Override
    public void onViewClick(int clickType, UiMessage data) {
        if (MessageProviderPermissionHandler.getInstance()
                .handleMessageClickPermission(data, this)) {
            return;
        }
        if (Activities.Companion.get().getTop()!= null){
            mMessageViewModel.onViewClick(clickType, data,Activities.Companion.get().getTop());
        }
    }

    @Override
    public boolean onViewLongClick(int clickType, UiMessage data) {
        return mMessageViewModel.onViewLongClick(clickType, data);
    }

    /**
     * 获取顶部通知栏容器
     *
     * @return 通知栏容器
     */
    public LinearLayout getNotificationContainer() {
        return mNotificationContainer;
    }

    /**
     * 隐藏调用showNotificationView所显示的通知view
     *
     * @param notificationView 通知栏 view
     */
    public void hideNotificationView(View notificationView) {
        if (notificationView == null) {
            return;
        }
        View view = mNotificationContainer.findViewById(notificationView.getId());
        if (view != null) {
            mNotificationContainer.removeView(view);
            if (mNotificationContainer.getChildCount() == 0) {
                mNotificationContainer.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 在通知区域显示一个view
     */
    public void showNotificationView(View notificationView) {
        if (notificationView == null) {
            return;
        }
        mNotificationContainer.removeAllViews();
        RongViewUtils.addView(mNotificationContainer, notificationView);
        mNotificationContainer.setVisibility(View.VISIBLE);
    }

    private void refreshList(final List<UiMessage> data) {
        if (!mList.isComputingLayout()
                && mList.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
            mAdapter.setDataCollection(data);
        } else {
            onScrollStopRefreshList = true;
        }

    }

    public boolean onBackPressed() {
        boolean result = false;
        for (IConversationUIRenderer processor :
                RongConfigCenter.conversationConfig().getViewProcessors()) {
            boolean temp = processor.onBackPressed();
            if (temp) {
                result = true;
            }
        }
        if (mMessageViewModel != null) {
            boolean temp = mMessageViewModel.onBackPressed();
            if (temp) {
                result = true;
            }
        }
        if (mRongExtensionViewModel != null) {
            mRongExtensionViewModel.exitMoreInputMode(Activities.Companion.get().getTop());
            mRongExtensionViewModel.collapseExtensionBoard();
        }
        return result;
    }

    public void onTitleClick() {
        if (RongConfigCenter.conversationConfig().getConversationClickListener() != null) {
            if (isDestroy(getActivity())) {
                return;
            }
            RongConfigCenter.conversationConfig()
                    .getConversationClickListener()
                    .onUserTitleClick(
                            getActivity(),
                            conversationIdentifier.getType(),
                            conversationIdentifier.getTargetId());

        }
    }

    public void onMoreClick() {
        FirebaseEventUtils.INSTANCE.logEvent(FirebaseEventTag.Chat_More.name());
        closeExpand();
        if (!isDestroy(getActivity())) {
            new UserProfileOperationDialog(getActivity(), conversationIdentifier.getTargetId(), new UserProfileOperationDialog.UserActionListener() {
                @Override
                public void userReport() {
                    FirebaseEventUtils.INSTANCE.logEvent(FirebaseEventTag.Chat_More_Report.name());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!isDestroy(getActivity())) {
                                new UserProfileOperationReportDialog(getActivity(), conversationIdentifier.getTargetId(), false, () -> {
                                    FirebaseEventUtils.INSTANCE.logEvent(FirebaseEventTag.Chat_More_Reportsuccess.name());
                                }).showPopupWindow();
                            }
                        }
                    }, 200);

                }

                @Override
                public void userBlack() {
                    FirebaseEventUtils.INSTANCE.logEvent(FirebaseEventTag.Chat_More_Block.name());
                    IMCenter.getInstance()
                            .removeConversation(
                                    conversationIdentifier.getType(),
                                    conversationIdentifier.getTargetId(),
                                    new RongIMClient.ResultCallback<Boolean>() {
                                        @Override
                                        public void onSuccess(Boolean aBoolean) {
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (!isDestroy(getActivity())) {
                                                        getActivity().finish();
                                                    }
                                                }
                                            }, 200);
                                        }

                                        @Override
                                        public void onError(RongIMClient.ErrorCode e) {
                                            Log.e(TAG, "onError: ");
                                        }
                                    });
                }
            }).showPopupWindow();
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        if (mMessageViewModel != null && bindToConversation) {
            mMessageViewModel.onRefresh();
        }
    }

    public RongExtension getRongExtension() {
        return mRongExtension;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            ReferenceManager.getInstance().hideReferenceView();
        }
        if (requestCode == REQUEST_CODE_FORWARD) {
            if (mMessageViewModel != null) mMessageViewModel.forwardMessage(data);
            return;
        }
        if (mRongExtension != null) {
            List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
            if (selectList != null && selectList.size() > 0) {
                boolean sendPrivate = data.getBooleanExtra(PictureConfig.PRIVATE_PHOTO_PREVIEW_TRANSFORM, false);
                if (sendPrivate) {
                    if (Activities.Companion.get().getTop()!= null){
                        LocalMedia localMedia = selectList.get(0);
                        String path = FileUtils.getPath(Activities.Companion.get().getTop(), Uri.parse(localMedia.getPath()));
                        UploadPhoto.INSTANCE.uploadFileNew(Activities.Companion.get().getTop(), path, new UploadPhoto.OnLister() {
                            @Override
                            public void onSuccess(@NonNull String successPath) {
                                Log.e(TAG, "onSuccess: " + successPath);
                                HttpRequest.INSTANCE.addAlbums(successPath, localMedia.getDuration());
                            }

                            @Override
                            public void fail() {
                            }
                        });
                    }

                }
            }
            if (mRongExtension != null) {
                mRongExtension.onActivityPluginResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (PermissionCheckUtil.checkPermissionResultIncompatible(permissions, grantResults)) {
            if (Activities.Companion.get().getTop() != null) {
                ToastUtils.s(Activities.Companion.get().getTop(), getString(R.string.rc_permission_request_failed));
            }
            return;
        }

        if (requestCode == REQUEST_MSG_DOWNLOAD_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                HQVoiceMsgDownloadManager.getInstance().resumeDownloadService();
            } else {
                PermissionCheckUtil.showRequestPermissionFailedAlter(
                        Activities.Companion.get().getTop(), permissions, grantResults);
            }
            return;
        } else if (requestCode == PermissionCheckUtil.REQUEST_CODE_LOCATION_SHARE) {
            if (PermissionCheckUtil.checkPermissions(getActivity(), permissions)) {
                LocationUiRender locationUiRender = null;
                for (IConversationUIRenderer processor :
                        RongConfigCenter.conversationConfig().getViewProcessors()) {
                    if (processor instanceof LocationUiRender) {
                        locationUiRender = (LocationUiRender) processor;
                        break;
                    }
                }

                if (locationUiRender != null) {
                    locationUiRender.joinLocation();
                }
            } else {
                if (!isDestroy(getActivity())) {
                    PermissionCheckUtil.showRequestPermissionFailedAlter(
                            getActivity(), permissions, grantResults);
                }
            }
        } else if (requestCode
                == MessageProviderPermissionHandler.REQUEST_CODE_ITEM_PROVIDER_PERMISSIONS) {
            MessageProviderPermissionHandler.getInstance()
                    .onRequestPermissionsResult(getActivity(), permissions, grantResults);
        }

        if (requestCode == PermissionCheckUtil.REQUEST_CODE_ASK_PERMISSIONS
                && grantResults.length > 0
                && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            PermissionCheckUtil.showRequestPermissionFailedAlter(
                    Activities.Companion.get().getTop(), permissions, grantResults);
        } else {
            mRongExtension.onRequestPermissionResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * findId，绑定监听
     */
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.rc_conversation_fragment, container, false);
        mList = rootView.findViewById(R.id.rc_message_list);
        mRcEmptyText = rootView.findViewById(R.id.rc_empty_text);
        mRongExtension = rootView.findViewById(R.id.rc_extension);
        mRefreshLayout = rootView.findViewById(R.id.rc_refresh);
        mNewMessageNum = rootView.findViewById(R.id.rc_new_message_number);
        mUnreadHistoryMessageNum = rootView.findViewById(R.id.rc_unread_message_count);
        mUnreadMentionMessageNum = rootView.findViewById(R.id.rc_mention_message_count);
        mNotificationContainer = rootView.findViewById(R.id.rc_notification_container);
//        mImgConversationBg = rootView.findViewById(R.id.img_conversation_bg);
//        mImgConversationBg.setVisibility(BaseConfig.Companion.getGetInstance().getInt(SpName.INSTANCE.getTrafficSource(), 0) == 1 ? GONE : VISIBLE);
        mNewMessageNum.setOnClickListener(this);
        mUnreadHistoryMessageNum.setOnClickListener(this);
        mUnreadMentionMessageNum.setOnClickListener(this);
        mLinearLayoutManager = createLayoutManager();
        if (mList != null) {
            mList.setLayoutManager(mLinearLayoutManager);
            ((SimpleItemAnimator) Objects.requireNonNull(mList.getItemAnimator())).setSupportsChangeAnimations(false);
        }

        mRefreshLayout.setOnTouchListener(
                new View.OnTouchListener() {
                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        closeExpand();
                        return false;
                    }
                });
        mAdapter.setItemClickListener(
                new BaseAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, ViewHolder holder, int position) {
                        closeExpand();
                    }

                    @Override
                    public boolean onItemLongClick(View view, ViewHolder holder, int position) {
                        return false;
                    }
                });
        // 关闭动画
        if (mList != null) {
            if (BaseConfig.Companion.getGetInstance().getInt(SpName.INSTANCE.getTrafficSource(), 0) != 1) {
                addHeaderView(addChatViewHead(null));
            }
            mList.setAdapter(mAdapter);
            mList.addOnScrollListener(mScrollListener);
//            mList.setItemAnimator(null);
            final GestureDetector gd =
                    new GestureDetector(
                            Activities.Companion.get().getTop(),
                            new GestureDetector.SimpleOnGestureListener() {
                                @Override
                                public boolean onScroll(
                                        MotionEvent e1,
                                        MotionEvent e2,
                                        float distanceX,
                                        float distanceY) {
                                    closeExpand();
                                    return super.onScroll(e1, e2, distanceX, distanceY);
                                }
                            });
            mList.addOnItemTouchListener(
                    new RecyclerView.OnItemTouchListener() {
                        @Override
                        public boolean onInterceptTouchEvent(
                                @NonNull RecyclerView rv, @NonNull MotionEvent e) {
                            return gd.onTouchEvent(e);
                        }

                        @Override
                        public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                            // Do nothing
                        }

                        @Override
                        public void onRequestDisallowInterceptTouchEvent(
                                boolean disallowIntercept) {
                            // Do nothing
                        }
                    });
        }

        mRefreshLayout.setNestedScrollingEnabled(false);
        mRefreshLayout.setRefreshHeader(new RongRefreshHeader(Activities.Companion.get().getTop()));
        mRefreshLayout.setRefreshFooter(new RongRefreshHeader(Activities.Companion.get().getTop()));
        mRefreshLayout.setEnableRefresh(true);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadMoreListener(this);

        return rootView;
    }

    private RecyclerView.LayoutManager createLayoutManager() {
        LinearLayoutManager linearLayoutManager = new FixedLinearLayoutManager(Activities.Companion.get().getTop());
        linearLayoutManager.setStackFromEnd(true);
        return linearLayoutManager;
    }

    /**
     * 判断Activity是否Destroy
     *
     * @param mActivity
     * @return
     */
    public static boolean isDestroy(Activity mActivity) {
        if (mActivity == null || mActivity.isFinishing() || mActivity.isDestroyed()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getActivity() == null || getActivity().getIntent() == null) {
            RLog.e(
                    TAG,
                    "Must put targetId and conversation type to intent when start conversation.");
            return;
        }
        if (!IMCenter.getInstance().isInitialized()) {
            RLog.e(TAG, "Please init SDK first!");
            return;
        }
        super.onViewCreated(view, savedInstanceState);
        initIntentExtra();
        //如果是系统会话，隐藏输入框
        if (Conversation.ConversationType.SYSTEM.equals(conversationIdentifier.getType()) || BaseConfig.Companion.getGetInstance().getString(SpName.INSTANCE.getServerUserCode(), "").equals(conversationIdentifier.getTargetId())) {
            mRongExtension.setVisibility(View.GONE);
        } else {
            mRongExtension.setVisibility(View.VISIBLE);
        }
        mMessageViewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        mRongExtensionViewModel = new ViewModelProvider(this).get(RongExtensionViewModel.class);
        bindConversation(conversationIdentifier, mDisableSystemEmoji, mBundle);
        HttpRequest.INSTANCE.chatOpen(conversationIdentifier.getTargetId(), 1, entity -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("city", entity.getCity() != null ? entity.getCity() + "," + entity.getState() : null);
            jsonObject.addProperty("userType", entity.getUserType());
            SDEventManager.post(jsonObject, EnumEventTag.IM_CHAT_LOCATION.ordinal());
            if (BaseConfig.Companion.getGetInstance().getInt(SpName.INSTANCE.getTrafficSource(), 0) != 1) {
                addChatViewHead(entity);
            }
            setOpenChatData(entity);
            if (!BaseConfig.Companion.getGetInstance().getString(SpName.INSTANCE.getServerUserCode(), "").equals(conversationIdentifier.getTargetId())) {
                RongIMClient.getInstance().getMessageCount(Conversation.ConversationType.PRIVATE, conversationIdentifier.getTargetId(), new RongIMClient.ResultCallback<Integer>() {
                    @Override
                    public void onSuccess(Integer count) {
                        if (count > 0) {
                            if (!isDestroy(getActivity())) {
                                new Handler().postDelayed(() -> {
                                    refreshList(mMessageViewModel.getUiMessageLiveData().getValue());
                                }, 500);
                            }
                            mRcEmptyText.setVisibility(View.GONE);

                        } else {
                            historyMessageListEmpty = true;
                            mRcEmptyText.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode e) {

                    }
                });
            }

        });

        // NOTE: 2021/8/25  当初解决高清语音自动下载，现在高清语音下载不需要申请存储权限，删除此处.
        onViewCreated = true;
    }

    /**
     * 会话添加头部
     *
     * @param entity
     * @return
     */
    private View addChatViewHead(OpenChatEntity.Data entity) {
        if (entity == null && !isDestroy(getActivity())) {
            inflate = LayoutInflater.from(getActivity()).inflate(R.layout.layout_conversation_title, null);
            return inflate;
        }
        boolean isMatch = entity.getMatchFlag() == 1;
        ConstraintLayout viewMatch = inflate.findViewById(R.id.viewMatch);
        ConstraintLayout wantContainer = inflate.findViewById(R.id.want_container);
        ConstraintLayout acceptContainer = inflate.findViewById(R.id.accept_container);
        LinearLayout acceptChildShowContainer = inflate.findViewById(R.id.accept_child_show_container);
        TextView imWant = inflate.findViewById(R.id.im_want);
        TextView noAcceptTip = inflate.findViewById(R.id.no_accept_tip);
        ConstraintLayout mChatTurnOnsContainer = inflate.findViewById(R.id.chat_turn_ons_container);
        ImageView mChatTurnOnsArrow = inflate.findViewById(R.id.chat_turn_ons_arrow);
        TextView mChatTurnOnsTitle = inflate.findViewById(R.id.chat_turn_ons_title);
        TagCloudView chatTurnOnsTag = inflate.findViewById(R.id.chat_turn_ons_tag);
        if (!BaseConfig.Companion.getGetInstance().getBoolean(SpName.INSTANCE.getChatTurnOnsShow() + conversationIdentifier.getTargetId(), false)) {
            if (CollectionUtils.checkNullOrEmptyOrContainsNull(entity.getTurnOnsList())) {
                mChatTurnOnsContainer.setVisibility(View.GONE);
            } else {
                mChatTurnOnsArrow.setImageResource(R.drawable.icon_full_down_arrow);
                mChatTurnOnsContainer.setVisibility(View.VISIBLE);
                mChatTurnOnsTitle.setText(entity.getTurnOnsList().size() + " Turn-Ons in common");
                chatTurnOnsTag.setTagBeans(entity.getTurnOnsList());
                mChatTurnOnsContainer.setOnClickListener(view -> {
                    chatTurnOnsTag.setVisibility(chatTurnOnsTag.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                    mChatTurnOnsArrow.setImageResource(chatTurnOnsTag.getVisibility() == View.VISIBLE ? R.drawable.icon_full_down_arrow : R.drawable.icon_full_up_arrow);
                });
            }
        }
        if (entity.getUserWant() != null) {
            imWant.setText(entity.getUserWant().getValue());
            noAcceptTip.setVisibility(entity.getYouAccept() != null ? View.GONE : View.VISIBLE);
        }
        wantContainer.setVisibility(entity.getUserWant() != null ? View.VISIBLE : View.GONE);
        acceptContainer.setVisibility(entity.getYouAccept() != null ? View.VISIBLE : View.GONE);
        if (!CollectionUtils.checkNullOrEmptyOrContainsNull(entity.getYouAccept())) {
            for (int i = 0; i < entity.getYouAccept().size(); i++) {
                if (Activities.Companion.get().getTop() != null) {
                    TextView textView = new TextView(Activities.Companion.get().getTop());
                    textView.setText(entity.getYouAccept().get(i).getValue());
                    textView.setTextColor(Color.parseColor("#0B6B9D"));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        textView.setTypeface(getResources().getFont(R.font.intermedium));
                    }
                    // 创建布局参数对象
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    // 设置topMargin
                    layoutParams.setMargins(0, 5, 0, 0);
                    textView.setLayoutParams(layoutParams);
                    textView.setTextSize(14);
                    acceptChildShowContainer.addView(textView);
                }
            }
        }

        viewMatch.setVisibility(isMatch ? View.VISIBLE : View.GONE);
        if (isMatch) {
            if (!isDestroy(getActivity())) {
                RongConversationActivity activity = (RongConversationActivity) getActivity();
                UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(activity.mTargetId);
                if (userInfo != null) {
                    if (userInfo.getPortraitUri() != null && !isDestroy(getActivity())) {
                        Glide.with(getActivity())
                                .load(userInfo.getPortraitUri())
                                .placeholder(R.drawable.ease_default_image)
                                .into((RoundedImageView) inflate.findViewById(R.id.ivOtherImage));
                    }
                }
            }
            String myImageUrl = BaseConfig.Companion.getGetInstance().getString(SpName.INSTANCE.getAvatarUrl(), "");

            if (!myImageUrl.isEmpty() && !isDestroy(getActivity()))
                Glide.with(getActivity())
                        .load(myImageUrl)
                        .into((RoundedImageView) inflate.findViewById(R.id.ivMyImage));

        }
        return inflate;
    }

    private void initIntentExtra() {
        if (isDestroy(getActivity())) {
            return;
        }
        Intent intent = getActivity().getIntent();

        if (intent.hasExtra(RouteUtils.CONVERSATION_IDENTIFIER)) {
            ConversationIdentifier identifier =
                    intent.getParcelableExtra(RouteUtils.CONVERSATION_IDENTIFIER);
            if (identifier != null) {
                conversationIdentifier = identifier;
            }
        }
        if (conversationIdentifier == null) {
            String typeValue = intent.getStringExtra(RouteUtils.CONVERSATION_TYPE);
            Conversation.ConversationType type =
                    !TextUtils.isEmpty(typeValue)
                            ? Conversation.ConversationType.valueOf(
                            typeValue.toUpperCase(Locale.US))
                            : Conversation.ConversationType.NONE;
            String targetId = intent.getStringExtra(RouteUtils.TARGET_ID);
            conversationIdentifier = ConversationIdentifier.obtain(type, targetId, "");
        }

        mDisableSystemEmoji = intent.getBooleanExtra(RouteUtils.DISABLE_SYSTEM_EMOJI, false);
        if (mBundle == null) {
            mBundle = intent.getExtras();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getView() == null) {
            return;
        }
        if (mMessageViewModel != null) mMessageViewModel.onResume();
        getView()
                .setOnKeyListener(
                        new View.OnKeyListener() {
                            @Override
                            public boolean onKey(View v, int keyCode, KeyEvent event) {
                                if (event.getAction() == KeyEvent.ACTION_UP
                                        && keyCode == KeyEvent.KEYCODE_BACK) {
                                    return onBackPressed();
                                }
                                return false;
                            }
                        });
        mRongExtension.onResume();

        if (BaseConfig.Companion.getGetInstance().getString(SpName.INSTANCE.getServerUserCode(), "").equals(conversationIdentifier.getTargetId()) && BaseConfig.Companion.getGetInstance().getBoolean(SpName.INSTANCE.getGoEvaluate(), false)) {
            HttpRequest.INSTANCE.getEvaluateBackApp();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMessageViewModel != null) mMessageViewModel.onPause();
        if (mRongExtension != null) mRongExtension.onPause();
    }


    @Override
    public void onStart() {
        super.onStart();
        // 保存 activity 的原 softInputMode
        FragmentActivity activity = getActivity();
        if (!isDestroy(getActivity())) {
            activitySoftInputMode = activity.getWindow().getAttributes().softInputMode;
            if (mRongExtension != null && mRongExtension.useKeyboardHeightProvider()) {
                resetSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
            } else {
                resetSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mMessageViewModel != null) mMessageViewModel.onStop();
        resetSoftInputMode(activitySoftInputMode);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        HttpRequest.INSTANCE.chatOpen(conversationIdentifier.getTargetId(), 2, entity -> {
        });
        for (IConversationUIRenderer processor :
                RongConfigCenter.conversationConfig().getViewProcessors()) {
            processor.onDestroy();
        }
        mList.removeOnScrollListener(mScrollListener);

        if (mMessageViewModel != null) {
            mMessageViewModel.getPageEventLiveData().removeObserver(mPageObserver);
            mMessageViewModel.getUiMessageLiveData().removeObserver(mListObserver);
            mMessageViewModel
                    .getNewMentionMessageUnreadLiveData()
                    .removeObserver(mNewMentionMessageUnreadObserver);
            mMessageViewModel.onDestroy();
        }

        if (mRongExtension != null) {
            mRongExtension.onDestroy();
            mRongExtension = null;
        }
        bindToConversation = false;
    }

    private void resetSoftInputMode(int mode) {
        FragmentActivity activity = getActivity();
        if (!isDestroy(getActivity())) {
            activity.getWindow().setSoftInputMode(mode);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rc_new_message_number) {
            if (mMessageViewModel != null) mMessageViewModel.newMessageBarClick();
        } else if (id == R.id.rc_unread_message_count) {
            if (mMessageViewModel != null) mMessageViewModel.unreadBarClick();
        } else if (id == R.id.rc_mention_message_count) {
            if (mMessageViewModel != null) mMessageViewModel.newMentionMessageBarClick();
        }
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        if (mMessageViewModel != null && bindToConversation) {
            mMessageViewModel.onLoadMore();
        }
    }

    /**
     * 提示dialog. 例如"加入聊天室失败"的dialog 用户自定义此dialog的步骤: 1.定义一个类继承自 ConversationFragment 2.重写
     * onWarningDialog
     *
     * @param msg dialog 提示
     */
    public void onWarningDialog(String msg) {
        if (isDestroy(getActivity())) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        if (window == null) {
            return;
        }
        window.setContentView(R.layout.rc_cs_alert_warning);
        TextView tv = window.findViewById(R.id.rc_cs_msg);
        tv.setText(msg);

        window.findViewById(R.id.rc_btn_ok)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                                if (!isAdded()) {
                                    return;
                                }
                                FragmentManager fm = getChildFragmentManager();
                                if (fm.getBackStackEntryCount() > 0) {
                                    fm.popBackStack();
                                } else {
                                    if (!isDestroy(getActivity())) {
                                        getActivity().finish();
                                    }
                                }
                            }
                        });
    }

    private void showLoadMessageDialog(
            final MessageProcessor.GetMessageCallback callback, final List<Message> list) {
        if (!isDestroy(getActivity())) {
            new AlertDialog.Builder(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                    .setMessage(getString(R.string.rc_load_local_message))
                    .setPositiveButton(
                            getString(R.string.rc_dialog_ok),
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (callback != null) {
                                        callback.onSuccess(list, true);
                                    }
                                }
                            })
                    .setNegativeButton(
                            getString(R.string.rc_cancel),
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (callback != null) {
                                        callback.onErrorAsk(list);
                                    }
                                }
                            })
                    .show();
        }
    }

    private void sendTips(OpenChatEntity.Data data) {
        if (data == null)
            return;
        List<Integer> imTipsCountList = data.getImTipsCountList() == null ? new ArrayList<>() : data.getImTipsCountList();
        for (int i = 0; i < imTipsCountList.size(); i++) {
            if (ChatMessageNumber.INSTANCE.getTodayMessageNumber(getTargetId()) == imTipsCountList.get(i)) {
                HttpRequest.requestTipsData(i + 1, new SDOkHttpResoutCallBack<BaseResutlEntity<TipsEntity>>() {
                    @Override
                    public void onSuccess(BaseResutlEntity<TipsEntity> tipsEntityBaseResutlEntity) {
                        Gson gson = new Gson();
                        TipsEntity tipsEntity = tipsEntityBaseResutlEntity.getData();
                        if (tipsEntity == null || tipsEntity.getContents() == null || tipsEntity.getContents().isEmpty()) {
                            return;
                        }
                        tipsEntity.setShow(true);
                        String json = gson.toJson(tipsEntity);
                        TextMessage textMessage = TextMessage.obtain(json);
                        textMessage.setExtra("[FateLink tips]");
                        RongIM.getInstance().insertIncomingMessage(Conversation.ConversationType.PRIVATE,
                                getTargetId(),
                                getTargetId(),
                                new io.rong.imlib.model.Message.ReceivedStatus(0),
                                textMessage, new RongIMClient.ResultCallback<Message>() {
                                    @Override
                                    public void onSuccess(Message message) {

                                    }

                                    @Override
                                    public void onError(RongIMClient.ErrorCode e) {

                                    }
                                });
                    }
                });
            }
        }
    }

    private void  showTipsDialog(OpenChatEntity.Data data) {
        if (data == null)
            return;
        List<Integer> imReadyCountList = data.getImReadyCountList();
        for (int count : imReadyCountList) {
            if (ChatMessageNumber.INSTANCE.getUserMessageNum() == count) {
                //达到条件 请求接口获取弹窗数据
                HttpRequest.requestChatTipsData(count, new SDOkHttpResoutCallBack<BaseResutlEntity<TipsPopEntity>>() {
                    @SuppressLint("CheckResult")
                    @Override
                    public void onSuccess(BaseResutlEntity<TipsPopEntity> tipsEntityBaseResutlEntity) {
                        if (tipsEntityBaseResutlEntity == null || tipsEntityBaseResutlEntity.getData() == null)
                            return;
                        TipsPopEntity tipsEntity = tipsEntityBaseResutlEntity.getData();
                        if (!TextUtils.isEmpty(tipsEntity.getPopPicUrl())) {
                            Glide.with(ConversationFragment.this)
                                    .load(tipsEntity.getPopPicUrl())
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .addListener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            showTipsDialog(tipsEntity);
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            showTipsDialog(tipsEntity);
                                            return false;
                                        }
                                    })
                                    .preload();
                        } else {
                            showTipsDialog(tipsEntity);
                        }
                    }
                });
            }
        }
    }

    private void showTipsDialog(TipsPopEntity tipsEntity) {
        if (TextUtils.isEmpty(tipsEntity.getPopTitle())) {
            return;
        }
        if (!isDestroy(getActivity())) {
            //现实弹窗
            new CustomDialog(getActivity())
                    .setLayoutId(R.layout.dialog_chat_tips)
                    .setControllerListener(customDialog -> {
                        ConstraintLayout viewMian = customDialog.findViewById(R.id.viewMian);
                        RoundedImageView ivImage = customDialog.findViewById(R.id.ivImage);
                        TextView tvTitle = customDialog.findViewById(R.id.tvTitle);
                        TextView tvContent = customDialog.findViewById(R.id.tvContent);
                        if (TextUtils.isEmpty(tipsEntity.getPopPicUrl())) {
                            viewMian.setBackgroundResource(R.mipmap.ic_min_bg_tips_dialog);
                            ivImage.setVisibility(GONE);
                        } else {
                            ivImage.setVisibility(VISIBLE);
                            Glide.with(getActivity())
                                    .load(tipsEntity.getPopPicUrl())
                                    .into(ivImage);
                            viewMian.setBackgroundResource(R.mipmap.ic_bg_tips_dialog);
                        }
                        tvTitle.setText(tipsEntity.getPopTitle());
                        tvContent.setText(tipsEntity.getPopContent());
                        return null;
                    })
                    .setOnClickListener(R.id.tvGotIt, (customDialog, view) -> {
                        customDialog.dismiss();
                        return null;
                    }).show();
        }
    }

    private void closeExpand() {
        if (mRongExtensionViewModel != null) {
            mRongExtensionViewModel.collapseExtensionBoard();
        }
    }

    /**
     * 获取 adapter. 可复写此方法实现自定义 adapter.
     *
     * @return 会话列表 adapter
     */
    protected MessageListAdapter onResolveAdapter() {
        return new MessageListAdapter(this);
    }

    /**
     * @param view 自定义列表 header view
     */
    public void addHeaderView(View view) {
        mAdapter.addHeaderView(view);
    }

    /**
     * @param view 自定义列表 footer view
     */
    public void addFooterView(View view) {
        mAdapter.addFootView(view);
    }

    /**
     * @param view 自定义列表 空数据 view
     */
    public void setEmptyView(View view) {
        mAdapter.setEmptyView(view);
    }

    /**
     * @param emptyId 自定义列表 空数据的 LayoutId
     */
    public void setEmptyView(@LayoutRes int emptyId) {
        mAdapter.setEmptyView(emptyId);
    }
}
