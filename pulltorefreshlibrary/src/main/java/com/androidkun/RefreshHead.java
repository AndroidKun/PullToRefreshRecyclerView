package com.androidkun;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidkun.callback.PullToRefreshListener;

import java.util.Date;
/**
 * Created by Kun on 2017/2/7.
 * GitHub: https://github.com/AndroidKun
 * CSDN: http://blog.csdn.net/a1533588867
 * Description:下拉刷新
 */

public class RefreshHead extends LinearLayout {

    public static final int STATE_NORMAL = 0;
    public static final int STATE_RELEASE_TO_REFRESH = 1;
    public static final int STATE_REFRESHING = 2;
    public static final int STATE_DONE = 3;
    public static final int STATE_FAIL = 4;

    private int refreshState = 0;
    /**
     * 触发刷新操作最小的高度
     */
    private int refreshLimitHeight;
    private View refreshContentView;
    private TextView textTip;
    private TextView textLastRefreshTime;
    private ImageView imageArrow;
    private ImageView imageRefreshing;

    private PullToRefreshListener pullToRefreshListener;
    /**
     * 上次刷新的时间
     */
    private Date lastRefreshDate;
    private boolean showLastRefreshTime;
    private ValueAnimator animator;

    public RefreshHead(Context context) {
        this(context, null);
    }

    public RefreshHead(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshHead(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        refreshLimitHeight = getScreenHeight() / 6;

        refreshContentView = LayoutInflater.from(getContext()).inflate(
                R.layout.layout_refresh_head_view, null);
        textTip = (TextView) refreshContentView.findViewById(R.id.textTip);
        textLastRefreshTime = (TextView) refreshContentView.findViewById(R.id.textLastRefreshTime);
        imageArrow = (ImageView) refreshContentView.findViewById(R.id.imageArrow);
        imageRefreshing = (ImageView) refreshContentView.findViewById(R.id.imageRefreshing);
        //一开始设置高度为0 不显示刷新布局
        addView(refreshContentView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
        setGravity(Gravity.BOTTOM);


    }

    /**
     * 设置触发刷新的高度
     * 
     * @param height
     */
    public void setRefreshLimitHeight(int height){
        refreshLimitHeight = height;
    }

    public void setRefreshArrowResource(int resId){
        imageArrow.setImageResource(resId);
    }

    public void setRefreshingResource(int resId){
        imageRefreshing.setImageResource(resId);
    }
    public void displayLastRefreshTime(boolean display){
        showLastRefreshTime = display;
    }

    public void onMove(int move) {
        int newVisibleHeight = getVisibleHeight() + move;
        if (newVisibleHeight >= refreshLimitHeight && refreshState != STATE_RELEASE_TO_REFRESH) {
            if(imageArrow.getVisibility()!=VISIBLE ) imageArrow.setVisibility(VISIBLE);
            refreshState = STATE_RELEASE_TO_REFRESH;
            textTip.setText(R.string.release_refresh);
            rotationAnimator(180f);
        }
        if (newVisibleHeight < refreshLimitHeight && refreshState != STATE_NORMAL) {
            if(imageArrow.getVisibility()!=VISIBLE ) imageArrow.setVisibility(VISIBLE);
            refreshState = STATE_NORMAL;
            textTip.setText(R.string.pull_to_refresh);
            if(lastRefreshDate==null){
                textLastRefreshTime.setVisibility(GONE);
            }else{
                if(showLastRefreshTime) {
                    textLastRefreshTime.setVisibility(VISIBLE);
                    textLastRefreshTime.setText(friendlyTime(lastRefreshDate));
                }
            }
            rotationAnimator(0);
        }
        setVisibleHeight(getVisibleHeight() + move);
    }

    /**
     * 触摸事件结束后检查是否需要刷新
     */
    public void checkRefresh() {
        if (getVisibleHeight() <= 0) return;
        if (refreshState == STATE_NORMAL) {
            smoothScrollTo(0);
            refreshState = STATE_DONE;
        } else if (refreshState == STATE_RELEASE_TO_REFRESH) {
            setState(STATE_REFRESHING);
        }
    }

    public void setRefreshing() {
        refreshState = STATE_REFRESHING;
        imageArrow.setVisibility(GONE);
        textLastRefreshTime.setVisibility(GONE);
        imageRefreshing.setVisibility(VISIBLE);
        startRefreshingAnimation();
        textTip.setText(R.string.refreshing);
        smoothScrollTo(getScreenHeight() / 9);
        if (pullToRefreshListener != null) {
            pullToRefreshListener.onRefresh();
        }
    }

    public void startRefreshingAnimation() {
        animator = ValueAnimator.ofFloat(imageRefreshing.getRotation(), imageRefreshing.getRotation()+359);
        animator.setDuration(1000).start();
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                imageRefreshing.setRotation((Float) animation.getAnimatedValue());
            }
        });
        animator.start();
/*
        RotateAnimation animation = new RotateAnimation(0f, 359f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(1000);
        animation.setRepeatCount(Animation.INFINITE);
        imageRefreshing.startAnimation(animation);*/
    }

    public void setState(int state) {
        if (getVisibleHeight() <= 0 || refreshState == state) return;
        switch (state) {
            case STATE_REFRESHING://切换到刷新状态
                imageArrow.setVisibility(GONE);
                textLastRefreshTime.setVisibility(GONE);
                imageRefreshing.setVisibility(VISIBLE);
                startRefreshingAnimation();
                textTip.setText(R.string.refreshing);
                smoothScrollTo(getScreenHeight() / 9);

                if (pullToRefreshListener != null) {
                    pullToRefreshListener.onRefresh();
                }
                break;
            case STATE_DONE://切换到刷新完成或者加载成功的状态
                if (refreshState == STATE_REFRESHING) {
                    imageRefreshing.setVisibility(GONE);
                    textTip.setText(R.string.refresh_success);
                    lastRefreshDate = new Date();
                    animator.end();
//                    imageRefreshing.clearAnimation();
                    smoothScrollTo(0);
                }
                break;
            case STATE_FAIL://切换到刷新失败或者加载失败的状态
                if (refreshState == STATE_REFRESHING) {
                    imageRefreshing.setVisibility(GONE);
                    imageArrow.setVisibility(VISIBLE);
                    textTip.setText(R.string.refresh_fail);
                    animator.end();
//                    imageRefreshing.clearAnimation();
                    smoothScrollTo(0);
                }
                break;
        }
        refreshState = state;
    }

    public int getRefreshState() {
        return refreshState;
    }

    public void setRefreshComplete() {
        setState(STATE_DONE);
    }

    public void setRefreshFail() {
        setState(STATE_FAIL);
    }

    public int getVisibleHeight() {
        LayoutParams lp = (LayoutParams) refreshContentView.getLayoutParams();
        return lp.height;
    }

    public void setVisibleHeight(int height) {
        if (height < 0) height = 0;
        LayoutParams lp = (LayoutParams) refreshContentView.getLayoutParams();
        lp.height = height;
        refreshContentView.setLayoutParams(lp);
    }

    public void setPullToRefreshListener(PullToRefreshListener pullToRefreshListener) {
        this.pullToRefreshListener = pullToRefreshListener;
    }

    private void rotationAnimator(float rotation) {
        ValueAnimator animator = ValueAnimator.ofFloat(imageArrow.getRotation(), rotation);
        animator.setDuration(200).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                imageArrow.setRotation((Float) animation.getAnimatedValue());
            }
        });
        animator.start();
    }

    private void smoothScrollTo(int destHeight) {
        ValueAnimator animator = ValueAnimator.ofInt(getVisibleHeight(), destHeight);
        animator.setDuration(300).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setVisibleHeight((int) animation.getAnimatedValue());
            }
        });
        animator.start();
    }


    /**
     * 获取屏幕高度
     *
     * @return
     */
    private int getScreenHeight() {
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }


    public static String friendlyTime(Date time) {
        //获取time距离当前的秒数
        int ct = (int)((System.currentTimeMillis() - time.getTime())/1000);

        if(ct == 0) {
            return "刚刚";
        }

        if(ct > 0 && ct < 60) {
            return ct + "秒前";
        }

        if(ct >= 60 && ct < 3600) {
            return Math.max(ct / 60,1) + "分钟前";
        }
        if(ct >= 3600 && ct < 86400)
            return ct / 3600 + "小时前";
        if(ct >= 86400 && ct < 2592000){ //86400 * 30
            int day = ct / 86400 ;
            return day + "天前";
        }
        if(ct >= 2592000 && ct < 31104000) { //86400 * 30
            return ct / 2592000 + "月前";
        }
        return ct / 31104000 + "年前";
    }

}
