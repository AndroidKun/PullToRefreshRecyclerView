package com.androidkun.callback;

/**
 * Created by Kun on 2017/2/7.
 * GitHub: https://github.com/AndroidKun
 * CSDN: http://blog.csdn.net/a1533588867
 * Description:下拉刷新回调接口
 */

public interface PullToRefreshListener {
    void onRefresh();
    void onLoadMore();
}
