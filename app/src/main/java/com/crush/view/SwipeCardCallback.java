package com.crush.view;

import android.graphics.Canvas;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.crush.R;
import com.crush.adapter.WhoLikeMeAdapter;
import io.rong.imkit.entity.WLMListBean;

import com.crush.callback.EmptyRefreshCallBack;
import com.crush.callback.WLMSwipedCallBack;
import com.crush.util.HttpRequest;
import com.custom.base.manager.SDActivityManager;

import java.util.List;

import io.rong.imkit.event.FirebaseEventTag;
import io.rong.imkit.utils.FirebaseEventUtils;

public class SwipeCardCallback extends ItemTouchHelper.SimpleCallback {
    private WhoLikeMeAdapter adapter;
    private List<WLMListBean> mDatas;
    private WLMSwipedCallBack wlmSwipedCallBack;
    public SwipeCardCallback(WhoLikeMeAdapter adapter,WLMSwipedCallBack wlmSwipedCallBack) {
        super(0, 5);
        this.adapter = adapter;
        this.mDatas = adapter.getDataList();
        this.wlmSwipedCallBack = wlmSwipedCallBack;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        HttpRequest.INSTANCE.get(direction,adapter.getDataList().get(viewHolder.getLayoutPosition()).getUserCodeFriend(), SDActivityManager.Companion.getInstance().getLastActivity(), new EmptyRefreshCallBack() {
            @Override
            public void OnRefreshListener() {
                wlmSwipedCallBack.swipedRefresh();
            }

            @Override
            public void OnSuccessListener() {
                viewHolder.itemView.findViewById(R.id.item_right_overlay).setAlpha(0);
                viewHolder.itemView.findViewById(R.id.item_left_overlay).setAlpha(0);

                ((ImageView)viewHolder.itemView.findViewById(R.id.item_user_dislike)).setImageResource(R.drawable.selector_user_dislike_click_status_img_wlm);
                viewHolder.itemView.findViewById(R.id.item_user_dislike).setBackgroundResource(R.drawable.selector_user_dislike_click_status_transfer);

                ((ImageView)viewHolder.itemView.findViewById(R.id.item_user_like)).setImageResource(R.drawable.selector_user_like_click_status_img);
                viewHolder.itemView.findViewById(R.id.item_user_like).setBackgroundResource(R.drawable.selector_user_like_click_status_transfer);

                FirebaseEventUtils.INSTANCE.logEvent(direction == ItemTouchHelper.START? FirebaseEventTag.WLM_Pass.name():FirebaseEventTag.WLM_Like.name());
                wlmSwipedCallBack.swipedCallback(direction,mDatas.get(viewHolder.getLayoutPosition()));

            }

            @Override
            public void OnFailListener() {
                viewHolder.itemView.findViewById(R.id.item_right_overlay).setAlpha(0);
                viewHolder.itemView.findViewById(R.id.item_left_overlay).setAlpha(0);

                ((ImageView)viewHolder.itemView.findViewById(R.id.item_user_dislike)).setImageResource(R.drawable.selector_user_dislike_click_status_img_wlm);
                viewHolder.itemView.findViewById(R.id.item_user_dislike).setBackgroundResource(R.drawable.selector_user_dislike_click_status_transfer);

                ((ImageView)viewHolder.itemView.findViewById(R.id.item_user_like)).setImageResource(R.drawable.selector_user_like_click_status_img);
                viewHolder.itemView.findViewById(R.id.item_user_like).setBackgroundResource(R.drawable.selector_user_like_click_status_transfer);


                adapter.notifyDataSetChanged();

            }
        });

        ((ImageView)viewHolder.itemView.findViewById(R.id.item_user_dislike)).setImageResource(R.drawable.selector_user_dislike_click_status_img_wlm);
        viewHolder.itemView.findViewById(R.id.item_user_dislike).setBackgroundResource(R.drawable.selector_user_dislike_click_status_transfer);

        ((ImageView)viewHolder.itemView.findViewById(R.id.item_user_like)).setImageResource(R.drawable.selector_user_like_click_status_img);
        viewHolder.itemView.findViewById(R.id.item_user_like).setBackgroundResource(R.drawable.selector_user_like_click_status_transfer);

        viewHolder.itemView.findViewById(R.id.item_right_overlay).setAlpha(0);
        viewHolder.itemView.findViewById(R.id.item_left_overlay).setAlpha(0);
        viewHolder.itemView.setRotation(0);

//        mDatas.remove(viewHolder.getLayoutPosition());
//        adapter.notifyDataSetChanged();


    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        int dragFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//        viewHolder.itemView.setRotation(dX / 10);
        if (dX < 0) {
            ((ImageView)viewHolder.itemView.findViewById(R.id.item_user_dislike)).setImageResource(R.mipmap.icon_black_dislike);
            viewHolder.itemView.findViewById(R.id.item_user_dislike).setBackgroundResource(R.drawable.shape_dislike_move_bg);
            viewHolder.itemView.findViewById(R.id.item_left_overlay).setAlpha(dX * -1 / 100);


            ((ImageView)viewHolder.itemView.findViewById(R.id.item_user_like)).setImageResource(R.drawable.selector_user_like_click_status_img);
            viewHolder.itemView.findViewById(R.id.item_user_like).setBackgroundResource(R.drawable.selector_user_like_click_status_transfer);
            viewHolder.itemView.findViewById(R.id.item_right_overlay).setAlpha(0);
        } else if (dX == 0){
            ((ImageView)viewHolder.itemView.findViewById(R.id.item_user_dislike)).setImageResource(R.drawable.selector_user_dislike_click_status_img_wlm);
            viewHolder.itemView.findViewById(R.id.item_user_dislike).setBackgroundResource(R.drawable.selector_user_dislike_click_status_transfer);

            ((ImageView)viewHolder.itemView.findViewById(R.id.item_user_like)).setImageResource(R.drawable.selector_user_like_click_status_img);
            viewHolder.itemView.findViewById(R.id.item_user_like).setBackgroundResource(R.drawable.selector_user_like_click_status_transfer);
        }else{
            ((ImageView)viewHolder.itemView.findViewById(R.id.item_user_dislike)).setImageResource(R.drawable.selector_user_dislike_click_status_img_wlm);
            viewHolder.itemView.findViewById(R.id.item_user_dislike).setBackgroundResource(R.drawable.selector_user_dislike_click_status_transfer);
            viewHolder.itemView.findViewById(R.id.item_left_overlay).setAlpha(0);


            viewHolder.itemView.findViewById(R.id.item_right_overlay).setAlpha(dX / 100);
            ((ImageView)viewHolder.itemView.findViewById(R.id.item_user_like)).setImageResource(R.mipmap.icon_white_like);
            viewHolder.itemView.findViewById(R.id.item_user_like).setBackgroundResource(R.drawable.shape_like_move_bg);

        }
    }
}