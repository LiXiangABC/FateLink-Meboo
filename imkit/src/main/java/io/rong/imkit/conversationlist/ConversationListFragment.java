package io.rong.imkit.conversationlist;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.custom.base.config.BaseConfig;
import com.gyf.immersionbar.ImmersionBar;
import com.sunday.eventbus.SDBaseEvent;
import com.sunday.eventbus.SDEventManager;
import com.sunday.eventbus.SDEventObserver;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.rong.common.CollectionUtils;
import io.rong.common.RLog;
import io.rong.imkit.IMCenter;
import io.rong.imkit.R;
import io.rong.imkit.RongIM;
import io.rong.imkit.SpName;
import io.rong.imkit.config.ConversationListBehaviorListener;
import io.rong.imkit.config.RongConfigCenter;
import io.rong.imkit.conversationlist.model.BaseUiConversation;
import io.rong.imkit.conversationlist.model.GatheredConversation;
import io.rong.imkit.conversationlist.viewmodel.ConversationListViewModel;
import io.rong.imkit.event.EnumEventTag;
import io.rong.imkit.event.Event;
import io.rong.imkit.event.FirebaseEventTag;
import io.rong.imkit.model.NoticeContent;
import io.rong.imkit.utils.FirebaseEventUtils;
import io.rong.imkit.utils.RouteUtils;
import io.rong.imkit.widget.FixedLinearLayoutManager;
import io.rong.imkit.widget.adapter.BaseAdapter;
import io.rong.imkit.widget.adapter.ViewHolder;
import io.rong.imkit.widget.dialog.OptionsPopupDialog;
import io.rong.imkit.widget.refresh.SmartRefreshLayout;
import io.rong.imkit.widget.refresh.api.RefreshLayout;
import io.rong.imkit.widget.refresh.constant.RefreshState;
import io.rong.imkit.widget.refresh.listener.OnLoadMoreListener;
import io.rong.imkit.widget.refresh.listener.OnRefreshListener;
import io.rong.imkit.widget.refresh.wrapper.RongRefreshHeader;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

public class ConversationListFragment extends Fragment implements BaseAdapter.OnItemClickListener, SDEventObserver {
    /*
     * 连接通知状态延迟显示时间。
     * 为了防止连接闪断，不会在断开连接时立即显示连接通知状态，而是在延迟一定时间后显示。
     */
    protected final long NOTICE_SHOW_DELAY_MILLIS = 4000L;
    private final String TAG = ConversationListFragment.class.getSimpleName();
    protected ConversationListAdapter mAdapter;
    protected SwipeRecyclerView mList;
    protected View mNoticeContainerView;
    protected TextView mNoticeContentTv;
    protected ImageView mNoticeIconIv;
    protected ConversationListViewModel mConversationListViewModel;
    protected SmartRefreshLayout mRefreshLayout;
    protected Handler mHandler = new Handler(Looper.getMainLooper());
    protected int mNewState = RecyclerView.SCROLL_STATE_IDLE;
    protected boolean delayRefresh = false;

    {
        mAdapter = onResolveAdapter();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.rc_conversationlist_fragment, null, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!IMCenter.getInstance().isInitialized()) {
            RLog.e(TAG, "Please init SDK first!");
            return;
        }
        RongIM.getInstance().refreshUserInfoCache(new UserInfo(BaseConfig.Companion.getGetInstance().getString(SpName.INSTANCE.getUserCode(), ""), BaseConfig.Companion.getGetInstance().getString(SpName.INSTANCE.getNickName(), ""), Uri.parse(BaseConfig.Companion.getGetInstance().getString(SpName.INSTANCE.getAvatarUrl(), ""))));
        SDEventManager.register(this);
        mList = view.findViewById(R.id.rc_conversation_list);
        mRefreshLayout = view.findViewById(R.id.rc_refresh);
        mAdapter.setItemClickListener(this);
        LinearLayoutManager layoutManager = new FixedLinearLayoutManager(getActivity());
        mList.setLayoutManager(layoutManager);
        mList.addOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(
                            @NonNull RecyclerView recyclerView, int newState) {
                        Log.e(TAG, "onScrollStateChanged: ");
                        mNewState = newState;
                        if (mNewState == RecyclerView.SCROLL_STATE_IDLE
                                && delayRefresh
                                && mAdapter != null
                                && mConversationListViewModel != null) { // 滚动停止
                            delayRefresh = false;
                            mAdapter.setDataCollection(
                                    mConversationListViewModel
                                            .getConversationListLiveData()
                                            .getValue());
                        }
                    }
                });
        mNoticeContainerView = view.findViewById(R.id.rc_conversationlist_notice_container);
        mNoticeContentTv = view.findViewById(R.id.rc_conversationlist_notice_tv);
        mNoticeIconIv = view.findViewById(R.id.rc_conversationlist_notice_icon_iv);


        // 创建菜单：
        SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position) {
                if (getActivity()==null){
                    return;
                }
                if (position == 0) {
                    return;
                }


                if (!CollectionUtils.checkNullOrEmptyOrContainsNull(mAdapter.getData())){
                    if (mAdapter.getData().get(position-1).getConversationIdentifier().getTargetId().equals(BaseConfig.Companion.getGetInstance().getString(SpName.INSTANCE.getServerUserCode(), ""))) {
                        return;
                    }
                }

                int itemViewType = mAdapter.getItemViewType(position);
                if (itemViewType == -200) {
                    return;
                }
                int width = getResources().getDimensionPixelSize(R.dimen.dp_72);

                // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
                // 2. 指定具体的高，比如80;
                // 3. WRAP_CONTENT，自身高度，不推荐;
                int height = ViewGroup.LayoutParams.MATCH_PARENT;

                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity()).setBackgroundColor(Color.parseColor("#FF465D"))
                        .setText("Delete")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                rightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。
            }
        };
        // 设置监听器。
        mList.setSwipeMenuCreator(mSwipeMenuCreator);
        OnItemMenuClickListener mItemMenuClickListener = new OnItemMenuClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge, int position) {
                // 任何操作必须先关闭菜单，否则可能出现Item菜单打开状态错乱。
                menuBridge.closeMenu();
                FirebaseEventUtils.INSTANCE.logEvent(FirebaseEventTag.IM_Deletechat.name());

                // 菜单在Item中的Position：
                BaseUiConversation baseUiConversation = mAdapter.getData().get(position - 1);
                IMCenter.getInstance()
                        .removeConversation(
                                baseUiConversation.mCore.getConversationType(),
                                baseUiConversation.mCore.getTargetId(),
                                new RongIMClient.ResultCallback<Boolean>() {
                                    @Override
                                    public void onSuccess(Boolean aBoolean) {
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                mAdapter.setDataCollection(
                                                        mConversationListViewModel
                                                                .getConversationListLiveData()
                                                                .getValue());
                                            }
                                        }, 200);
                                    }

                                    @Override
                                    public void onError(RongIMClient.ErrorCode e) {
                                        Log.e(TAG, "onError: ");
                                    }
                                });

            }
        };

        mList.setSwipeItemMenuEnabled(0, false);

        // 菜单点击监听。
        mList.setOnItemMenuClickListener(mItemMenuClickListener);

        mList.setAdapter(mAdapter);

        initRefreshView();
        subscribeUi();
    }

    @Override
    public void onEvent(SDBaseEvent sdBaseEvent) {
    }

    @Override
    public void onEventMainThread(SDBaseEvent sdBaseEvent) {
        if (sdBaseEvent.getTagInt() == EnumEventTag.CHAT_LIST_ITEM.ordinal()) {
            for (int i = 0; i < mAdapter.getData().size(); i++) {
                if (mAdapter.getData().get(i).getConversationIdentifier().getTargetId().equals(sdBaseEvent.getData().toString())) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.setDataCollection(
                                    mConversationListViewModel
                                            .getConversationListLiveData()
                                            .getValue());
                        }
                    }, 200);
                }
            }
        }
    }

    @Override
    public void onEventBackgroundThread(SDBaseEvent sdBaseEvent) {
    }

    @Override
    public void onEventAsync(SDBaseEvent sdBaseEvent) {

    }


    @Override
    public void onResume() {
        super.onResume();
        if (mConversationListViewModel != null) {
            mConversationListViewModel.clearAllNotification();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SDEventManager.unregister(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ImmersionBar.with(this)
                    .statusBarDarkFont(true)
                    .init();
        }
    }

    /**
     * 初始化刷新模块
     */
    protected void initRefreshView() {
        if (mRefreshLayout == null || getActivity()==null) {
            RLog.d(TAG, "initRefreshView null");
            return;
        }
        mRefreshLayout.setNestedScrollingEnabled(false);
        mRefreshLayout.setRefreshHeader(new RongRefreshHeader(getActivity()));
        mRefreshLayout.setRefreshFooter(new RongRefreshHeader(getActivity()));
        mRefreshLayout.setOnRefreshListener(
                new OnRefreshListener() {
                    @Override
                    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                        onConversationListRefresh(refreshLayout);
                    }
                });
        mRefreshLayout.setOnLoadMoreListener(
                new OnLoadMoreListener() {
                    @Override
                    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                        onConversationListLoadMore();
                    }
                });
    }

    /**
     * 观察 view model 各数据以便进行页面刷新操作。
     */
    protected void subscribeUi() {
        // 会话列表数据监听
        mConversationListViewModel =
                new ViewModelProvider(this).get(ConversationListViewModel.class);
        mConversationListViewModel.getConversationList(false, false, 0);
        mConversationListViewModel
                .getConversationListLiveData()
                .observe(
                        getViewLifecycleOwner(),
                        new Observer<List<BaseUiConversation>>() {
                            @Override
                            public void onChanged(List<BaseUiConversation> uiConversations) {
                                RLog.e(TAG, "conversation list onChanged.");
                                if (mNewState == RecyclerView.SCROLL_STATE_IDLE) {
                                    mAdapter.setDataCollection(uiConversations);
                                } else {
                                    delayRefresh = true;
                                }
                            }
                        });
        // 连接状态监听
        mConversationListViewModel
                .getNoticeContentLiveData()
                .observe(
                        getViewLifecycleOwner(),
                        new Observer<NoticeContent>() {
                            @Override
                            public void onChanged(NoticeContent noticeContent) {
                                // 当连接通知没有显示时，延迟进行显示，防止连接闪断造成画面闪跳。
                                if (mNoticeContainerView.getVisibility() == View.GONE) {
                                    mHandler.postDelayed(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    // 刷新时使用最新的通知内容
                                                    updateNoticeContent(
                                                            mConversationListViewModel
                                                                    .getNoticeContentLiveData()
                                                                    .getValue());
                                                }
                                            },
                                            NOTICE_SHOW_DELAY_MILLIS);
                                } else {
                                    updateNoticeContent(noticeContent);
                                }
                            }
                        });
        // 刷新事件监听
        mConversationListViewModel
                .getRefreshEventLiveData()
                .observe(
                        getViewLifecycleOwner(),
                        new Observer<Event.RefreshEvent>() {
                            @Override
                            public void onChanged(Event.RefreshEvent refreshEvent) {
                                if (refreshEvent.state.equals(RefreshState.LoadFinish)) {
                                    if (mRefreshLayout != null) {
                                        mRefreshLayout.finishLoadMore();
                                    } else {
                                        RLog.d(TAG, "onChanged finishLoadMore error");
                                    }
                                } else if (refreshEvent.state.equals(RefreshState.RefreshFinish)) {
                                    if (mRefreshLayout != null) {
                                        mRefreshLayout.finishRefresh();
                                    } else {
                                        RLog.d(TAG, "onChanged finishRefresh error");
                                    }
                                }
                            }
                        });
    }

    protected void onConversationListRefresh(RefreshLayout refreshLayout) {
        if (mConversationListViewModel != null) {
            mConversationListViewModel.getConversationList(false, true, 0);
        }
    }

    protected void onConversationListLoadMore() {
        if (mConversationListViewModel != null) {
            mConversationListViewModel.getConversationList(true, true, 0);
        }
    }

    /**
     * 更新连接状态通知栏
     *
     * @param content
     */
    protected void updateNoticeContent(NoticeContent content) {
        if (content == null) return;

        if (content.isShowNotice()) {
            mNoticeContainerView.setVisibility(View.VISIBLE);
            mNoticeContentTv.setText(content.getContent());
            if (content.getIconResId() != 0) {
                mNoticeIconIv.setImageResource(content.getIconResId());
            }
        } else {
            mNoticeContainerView.setVisibility(View.GONE);
        }
    }

    /**
     * 获取 adapter. 可复写此方法实现自定义 adapter.
     *
     * @return 会话列表 adapter
     */
    protected ConversationListAdapter onResolveAdapter() {
        mAdapter = new ConversationListAdapter();
        mAdapter.setEmptyView(R.layout.rc_conversationlist_empty_view);
        return mAdapter;
    }

    /**
     * 会话列表点击事件回调
     *
     * @param view     点击 view
     * @param holder   {@link ViewHolder}
     * @param position 点击位置
     */
    @Override
    public void onItemClick(View view, ViewHolder holder, int position) {
        if (position < 0 || position >= mAdapter.getData().size()) {
            return;
        }
        if (getContext()==null){
            return;
        }
        BaseUiConversation baseUiConversation = mAdapter.getItem(position);
        ConversationListBehaviorListener listBehaviorListener =
                RongConfigCenter.conversationListConfig().getListener();
        if (listBehaviorListener != null
                && listBehaviorListener.onConversationClick(
                view.getContext(), view, baseUiConversation)) {
            RLog.d(TAG, "ConversationList item click event has been intercepted by App.");
            return;
        }
        Log.e(TAG, "onItemClick: " + baseUiConversation.mCore.getPortraitUrl());
        if (baseUiConversation != null && baseUiConversation.mCore != null) {
            if (baseUiConversation instanceof GatheredConversation) {
                RouteUtils.routeToSubConversationListActivity(
                        view.getContext(),
                        ((GatheredConversation) baseUiConversation).mGatheredType,
                        baseUiConversation.mCore.getConversationTitle());
            } else {
                RouteUtils.routeToConversationActivity(
                        view.getContext(), baseUiConversation.getConversationIdentifier());
            }
        } else {
            RLog.e(TAG, "invalid conversation.");
        }
    }

    /**
     * 会话列表长按事件回调
     *
     * @param view     点击 view
     * @param holder   {@link ViewHolder}
     * @param position 点击位置
     * @return 事件是否被消费
     */
    @Override
    public boolean onItemLongClick(final View view, ViewHolder holder, int position) {
        if (position < 0 || position >= mAdapter.getData().size()) {
            return false;
        }
        final BaseUiConversation baseUiConversation = mAdapter.getItem(position);
        ConversationListBehaviorListener listBehaviorListener =
                RongConfigCenter.conversationListConfig().getListener();
        if (listBehaviorListener != null
                && listBehaviorListener.onConversationLongClick(
                view.getContext(), view, baseUiConversation)) {
            RLog.d(TAG, "ConversationList item click event has been intercepted by App.");
            return true;
        }
//        final ArrayList<String> items = new ArrayList<>();
//        final String removeItem =
//                view.getContext()
//                        .getResources()
//                        .getString(R.string.rc_conversation_list_dialog_remove);
//        final String setTopItem =
//                view.getContext()
//                        .getResources()
//                        .getString(R.string.rc_conversation_list_dialog_set_top);
//        final String cancelTopItem =
//                view.getContext()
//                        .getResources()
//                        .getString(R.string.rc_conversation_list_dialog_cancel_top);
//
//        if (!(baseUiConversation instanceof GatheredConversation)) {
//            if (baseUiConversation.mCore.isTop()) {
//                items.add(cancelTopItem);
//            } else {
//                items.add(setTopItem);
//            }
//        }
//        items.add(removeItem);
//        int size = items.size();
//        OptionsPopupDialog.newInstance(view.getContext(), items.toArray(new String[size]))
//                .setOptionsPopupDialogListener(
//                        new OptionsPopupDialog.OnOptionsItemClickedListener() {
//                            @Override
//                            public void onOptionsItemClicked(final int which) {
//                                if (items.get(which).equals(setTopItem)
//                                        || items.get(which).equals(cancelTopItem)) {
//                                    setConversationToTop(baseUiConversation, items.get(which));
//                                } else if (items.get(which).equals(removeItem)) {
//                                    IMCenter.getInstance()
//                                            .removeConversation(
//                                                    baseUiConversation.mCore.getConversationType(),
//                                                    baseUiConversation.mCore.getTargetId(),
//                                                    null);
//                                }
//                            }
//                        })
//                .show();
        return true;
    }

    private void setConversationToTop(BaseUiConversation baseUiConversation, String text) {
        IMCenter.getInstance()
                .setConversationToTop(
                        baseUiConversation.getConversationIdentifier(),
                        !baseUiConversation.mCore.isTop(),
                        false,
                        new RongIMClient.ResultCallback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean value) {
                                Activity activity = getActivity();
                                if (activity == null
                                        || activity.isFinishing()
                                        || activity.isDestroyed()) {
                                    return;
                                }
//                                ToastUtils.show(activity, text, Toast.LENGTH_SHORT);
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {
                                // do nothing
                            }
                        });
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
