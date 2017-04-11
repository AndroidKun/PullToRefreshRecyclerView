package com.androidkun;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Kun on 2017/2/8.
 * GitHub: https://github.com/AndroidKun
 * CSDN: http://blog.csdn.net/a1533588867
 * Description:加载更多
 */

public class LoadMoreView extends LinearLayout{
    private ImageView imageLoadMore;
    private TextView textTip;
    private ValueAnimator animator;

    public LoadMoreView(Context context) {
        this(context,null);
    }

    public LoadMoreView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadMoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View loadMoreContentView =  LayoutInflater.from(getContext()).inflate(
                R.layout.layout_load_more_view, null);
        imageLoadMore = (ImageView) loadMoreContentView.findViewById(R.id.imageLoadMore);
        textTip = (TextView) loadMoreContentView.findViewById(R.id.textTip);
        textTip.setVisibility(GONE);
        addView(loadMoreContentView);
    }

    public void setLoadMoreResource(int resId){
        imageLoadMore.setImageResource(resId);
    }

    public void startAnimation() {
        animator = ValueAnimator.ofFloat(imageLoadMore.getRotation()
                ,imageLoadMore.getRotation()+359);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                imageLoadMore.setRotation((Float) animation.getAnimatedValue());
            }
        });
        animator.start();
      /* RotateAnimation animation = new RotateAnimation(0f, 359f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(1000);
        animation.setRepeatCount(Animation.INFINITE);
        imageLoadMore.startAnimation(animation);*/
    }
    public void stopAnimation() {
        animator.end();
//        imageLoadMore.clearAnimation();
    }

    public void loadMoreComplete(PullToRefreshRecyclerView refreshRecyclerView){
        setVisibility(GONE);
        stopAnimation();
        refreshRecyclerView.scrollBy(0,-getHeight());
    }
    public void loadMoreFail(final PullToRefreshRecyclerView refreshRecyclerView){
        textTip.setVisibility(VISIBLE);
        imageLoadMore.setVisibility(INVISIBLE);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                setVisibility(GONE);
                stopAnimation();
                refreshRecyclerView.scrollBy(0,-getHeight());
                textTip.setVisibility(INVISIBLE);
                imageLoadMore.setVisibility(VISIBLE);
            }
        },800);
    }
}
