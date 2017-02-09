package com.androidkun.pulltorefreshrecyclerview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.androidkun.PullToRefreshRecyclerView;
import com.androidkun.callback.PullToRefreshListener;
import com.androidkun.pulltorefreshrecyclerview.adapter.ModeAdapter;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements PullToRefreshListener {

    private PullToRefreshRecyclerView recyclerView;
    private List<String> data;
    private ModeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        data = new ArrayList<>();
        data.add("0000");
        data.add("1111");
        data.add("2222");
        data.add("3333");
        data.add("4444");
        recyclerView = (PullToRefreshRecyclerView) findViewById(R.id.recyclerView);
        //添加HeaderView
         View headView = View.inflate(this, R.layout.layout_head_view, null);
        recyclerView.addHeaderView(headView);
        //添加FooterView
        View footerView = View.inflate(this, R.layout.layout_foot_view, null);
        recyclerView.addFooterView(footerView);
        //设置EmptyView
        View emptyView = View.inflate(this, R.layout.layout_empty_view, null);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        recyclerView.setEmptyView(emptyView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ModeAdapter(this, data);
        recyclerView.setAdapter(adapter);
        //设置是否显示上次刷新的时间
        recyclerView.displayLastRefreshTime(true);
        //设置刷新回调
        recyclerView.setPullToRefreshListener(this);
        //主动触发下拉刷新操作
        recyclerView.onRefresh();
    }

    @Override
    public void onRefresh() {
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.setRefreshComplete();
                //模拟没有数据的情况
                data.clear();
                adapter.notifyDataSetChanged();
            }
        }, 3000);
    }

    @Override
    public void onLoadMore() {
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.setLoadMoreComplete();
                //模拟加载数据的情况
                int size = data.size();
                for (int i = size; i < size + 4; i++) {
                    data.add("" + i + i + i + i);
                }
                adapter.notifyDataSetChanged();
            }
        }, 3000);
    }
}
