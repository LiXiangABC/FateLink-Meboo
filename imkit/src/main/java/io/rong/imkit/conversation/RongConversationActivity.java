package io.rong.imkit.conversation;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.custom.base.config.BaseConfig;
import com.google.gson.JsonObject;
import com.sunday.eventbus.SDBaseEvent;
import com.sunday.eventbus.SDEventManager;
import com.sunday.eventbus.SDEventObserver;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imkit.R;
import io.rong.imkit.SpName;
import io.rong.imkit.activity.RongBaseActivity;
import io.rong.imkit.event.EnumEventTag;
import io.rong.imkit.http.HttpRequest;
import io.rong.imkit.model.TypingInfo;
import io.rong.imkit.userinfo.RongUserInfoManager;
import io.rong.imkit.userinfo.model.GroupUserInfo;
import io.rong.imkit.utils.RouteUtils;
import io.rong.imkit.widget.TitleBar;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.UserInfo;

import java.util.Locale;
import java.util.Objects;

public class RongConversationActivity extends RongBaseActivity implements SDEventObserver {
    protected String mTargetId;
    protected Conversation.ConversationType mConversationType;
    protected ConversationFragment mConversationFragment;
    private ConversationViewModel conversationViewModel;

    private boolean flashClick = false;

    @Override
    public boolean isShowNewTitleBar() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        SDEventManager.register(this);
        if (getIntent() != null) {
            mTargetId = getIntent().getStringExtra(RouteUtils.TARGET_ID);
            String type = getIntent().getStringExtra(RouteUtils.CONVERSATION_TYPE);
            if (!TextUtils.isEmpty(type)) {
                mConversationType =
                        Conversation.ConversationType.valueOf(type.toUpperCase(Locale.US));
            } else {
                return;
            }
        }
        setContentView(R.layout.rc_conversation_activity);
        mTitleBar.setVisibility(View.GONE);
        setTitle();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            flashClick = bundle.getBoolean("flashClick");
        }
        mConversationFragment =
                (ConversationFragment)
                        getSupportFragmentManager().findFragmentById(R.id.conversation);
        if (mConversationFragment != null && !mConversationFragment.onBackPressed()) {
            mConversationFragment.setTargetId(mTargetId);
        }
        mTitleBar.setOnBackClickListener(
                new TitleBar.OnBackClickListener() {
                    @Override
                    public void onBackClick() {
                        if (mConversationFragment != null
                                && !mConversationFragment.onBackPressed()) {
                            finish();
                        }
                    }
                });
        mTitleBar.getRightView().setVisibility(View.GONE);
        mTitleBar.setMoreViewVisibility(!BaseConfig.Companion.getGetInstance().getString("serverUserCode", "").equals(mTargetId));
        newTitleBar.getViewMore().setVisibility(!BaseConfig.Companion.getGetInstance().getString("serverUserCode", "").equals(mTargetId) ? View.VISIBLE : View.GONE);

        //获取谷歌评价相关配置
        if (BaseConfig.Companion.getGetInstance().getString("serverUserCode", "").equals(mTargetId)) {
            HttpRequest.INSTANCE.getEvaluateCheck();
        }

        mTitleBar.setOnRightMoreClickListener(new TitleBar.OnRightIconClickListener() {
            @Override
            public void onRightIconClick(View v) {
                if (mConversationFragment != null) {
                    mConversationFragment.onMoreClick();
                }
            }
        });
        mTitleBar.setOnTitleClickListener(v -> {
            if (mConversationFragment != null) {
                mConversationFragment.onTitleClick();
            }
        });
        initListener();
        initViewModel();
        observeUserInfoChange();
//        if (BaseConfig.Companion.getGetInstance().getInt(SpName.INSTANCE.getTrafficSource(), 0)==1){
            Objects.requireNonNull(newTitleBar.getNewTitleBar()).setBackgroundColor(ContextCompat.getColor(this,R.color.color_ADEEEF));
//        }

    }

    private void initListener() {
        newTitleBar.getViewBack().setOnClickListener(view -> {
            if (mConversationFragment != null
                    && !mConversationFragment.onBackPressed()) {
                finish();
            }
        });
        newTitleBar.getViewMore().setOnClickListener(view -> {
            if (mConversationFragment != null) {
                mConversationFragment.onMoreClick();
            }
        });
        newTitleBar.getTitleContainer().setOnClickListener(view -> {
            if (mConversationFragment != null) {
                mConversationFragment.onTitleClick();
            }
        });
    }

    private void observeUserInfoChange() {
        if (!TextUtils.isEmpty(mTargetId)) {
            RongUserInfoManager.getInstance().addUserDataObserver(mUserDataObserver);
        }
    }

    private final RongUserInfoManager.UserDataObserver mUserDataObserver =
            new RongUserInfoManager.UserDataObserver() {
                @Override
                public void onUserUpdate(UserInfo info) {
                    if (TextUtils.equals(mTargetId, info.getUserId())) {
                        runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        setTitle();
                                    }
                                });
                    }
                }

                @Override
                public void onGroupUpdate(Group group) {
                    if (TextUtils.equals(mTargetId, group.getId())) {
                        runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        setTitle();
                                    }
                                });
                    }
                }

                @Override
                public void onGroupUserInfoUpdate(GroupUserInfo groupUserInfo) {
                    // do nothing
                }
            };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!TextUtils.isEmpty(mTargetId)) {
            RongUserInfoManager.getInstance().removeUserDataObserver(mUserDataObserver);
            if (flashClick) {
                SDEventManager.post(true, EnumEventTag.INDEX_LIKE_SWIPED.ordinal());
            }
        }
        SDEventManager.unregister(this);
    }

    private void setTitle() {
//        if (!TextUtils.isEmpty(mTargetId)
//                && mConversationType.equals(Conversation.ConversationType.GROUP)) {
//            Group group = RongUserInfoManager.getInstance().getGroupInfo(mTargetId);
//            mTitleBar.setTitle(group == null ? mTargetId : group.getName());
//        } else {
//            UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(mTargetId);
//            mTitleBar.setTitle(userInfo == null ? mTargetId : userInfo.getName());
//        }
        //只显示Name，不显示TargetId
        if (!TextUtils.isEmpty(mTargetId)
                && mConversationType.equals(Conversation.ConversationType.GROUP)) {
            Group group = RongUserInfoManager.getInstance().getGroupInfo(mTargetId);
            mTitleBar.setTitle(group == null ? "" : group.getName());
            newTitleBar.getTvTitle().setText(group == null ? "" : group.getName());
            if (group != null) {
                Glide.with(this).load(group.getPortraitUri())
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(newTitleBar.getAvatar());
            }
        } else {
            UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(mTargetId);
            if(userInfo==null){
                finish();
                return;
            }
            mTitleBar.setTitle(userInfo == null ? "" : userInfo.getName());
            newTitleBar.getTvTitle().setText(userInfo == null ? "" : userInfo.getName());
            if (userInfo != null) {
                Glide.with(this).load(userInfo.getPortraitUri())
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(newTitleBar.getAvatar());
            }
        }

        if (mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE)
                || mConversationType.equals(Conversation.ConversationType.CHATROOM)) {
            mTitleBar.setRightVisible(false);
        }
    }

    private void initViewModel() {
        conversationViewModel = new ViewModelProvider(this).get(ConversationViewModel.class);
        conversationViewModel
                .getTypingStatusInfo()
                .observe(
                        this,
                        new Observer<TypingInfo>() {
                            @Override
                            public void onChanged(TypingInfo typingInfo) {
                                if (typingInfo == null) {
                                    return;
                                }
                                if (typingInfo.conversationType == mConversationType
                                        && mTargetId.equals(typingInfo.targetId)) {
                                    if (typingInfo.typingList == null) {
                                        mTitleBar.getMiddleView().setVisibility(View.VISIBLE);
                                        mTitleBar.getTypingView().setVisibility(View.GONE);
                                    } else {
                                        mTitleBar.getMiddleView().setVisibility(View.GONE);
                                        mTitleBar.getTypingView().setVisibility(View.VISIBLE);
                                        TypingInfo.TypingUserInfo typing =
                                                typingInfo.typingList.get(
                                                        typingInfo.typingList.size() - 1);
                                        if (typing.type == TypingInfo.TypingUserInfo.Type.text) {
                                            mTitleBar.setTyping(
                                                    R.string.rc_conversation_remote_side_is_typing);
                                        } else if (typing.type
                                                == TypingInfo.TypingUserInfo.Type.voice) {
                                            mTitleBar.setTyping(
                                                    R.string.rc_conversation_remote_side_speaking);
                                        }
                                    }
                                }
                            }
                        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
            if (mConversationFragment != null && !mConversationFragment.onBackPressed()) {
                finish();
            }
        }
        return false;
    }

    @Override
    public void onEvent(SDBaseEvent sdBaseEvent) {

    }

    @Override
    public void onEventMainThread(SDBaseEvent event) {
        if (event.getTagInt() == EnumEventTag.IM_CHAT_LOCATION.ordinal()) {
            JsonObject data = (JsonObject) event.getData();
            if (data != null) {
                Objects.requireNonNull(newTitleBar.getTvLocation()).setVisibility(!data.get("city").toString().equals("null") ? View.VISIBLE : View.GONE);
                if (!data.get("city").toString().equals("null")) {
                    Objects.requireNonNull(newTitleBar.getTvLocation()).setText(data.get("city").getAsString() + " \uD83C\uDDFA\uD83C\uDDF8");
                }
                Objects.requireNonNull(newTitleBar.getImgMember()).setVisibility(data.get("userType").getAsInt() == 2 ? View.VISIBLE : View.GONE);
            }
        }
    }

    @Override
    public void onEventBackgroundThread(SDBaseEvent sdBaseEvent) {

    }

    @Override
    public void onEventAsync(SDBaseEvent sdBaseEvent) {

    }
}
