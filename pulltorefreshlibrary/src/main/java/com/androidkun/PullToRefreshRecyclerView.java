package com.androidkun;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.androidkun.callback.PullToRefreshListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kun on 2017/2/7.
 * GitHub: https://github.com/AndroidKun
 * CSDN: http://blog.csdn.net/a1533588867
 * Description:PullToRefreshRecyclerView
 */

public class PullToRefreshRecyclerView extends RecyclerView {
    private static final int TYPE_REFRESH_HEADER = 10000;
    private RefreshHead refreshHeader;
    private static final int TYPE_LOAD_MORE_FOOTER = 10001;
    private LoadMoreView loadMoreView;

    private static final int TYPE_EMPTY_VIEW = 10002;
    private View emptyView;

    /**
     * 标识HeadView的ItemType的起始值
     */
    private static final int TYPE_HEADER_VIEWS_INIT = 10003;
    private static List<Integer> headerTypes = new ArrayList<>();
    private List<View> headViews = new ArrayList<>();
    /**
     * 标识FooterView的ItemType的起始值
     */
    private static final int TYPE_FOOTER_VIEW_INIT = 11000;
    private static List<Integer> footerTypes = new ArrayList<>();
    private List<View> footerViews = new ArrayList<>();
    /**
     * 摩擦力
     */
    private static final int DRAG_RATE = 3;
    private final AdapterDataObserver dataObserver = new DataObserver();

    private boolean pullRefreshEnabled = true;
    private boolean loadingMoreEnabled = true;

    private PullToRefreshRecyclerViewAdapter pullToRefreshRecyclerViewAdapter;

    private PullToRefreshListener pullToRefreshListener;


    public PullToRefreshRecyclerView(Context context) {
        this(context, null);
    }

    public PullToRefreshRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullToRefreshRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        refreshHeader = new RefreshHead(getContext());
        loadMoreView = new LoadMoreView(getContext());
        loadMoreView.setVisibility(GONE);
//        loadMoreView = View.inflate(getContext(), R.layout.layout_load_more_view, null);
    }

    public void setPullRefreshEnabled(boolean pullRefreshEnabled) {
        this.pullRefreshEnabled = pullRefreshEnabled;
    }

    public void setLoadingMoreEnabled(boolean loadingMoreEnabled) {
        this.loadingMoreEnabled = loadingMoreEnabled;
    }

    public void setPullToRefreshListener(PullToRefreshListener pullToRefreshListener) {
        this.pullToRefreshListener = pullToRefreshListener;
        if (refreshHeader != null) {
            refreshHeader.setPullToRefreshListener(pullToRefreshListener);
        }
    }

    /**
     * 设置箭头资源图标
     * @param resId
     */
    public void setRefreshArrowResource(int resId){
        refreshHeader.setRefreshArrowResource(resId);
    }
    /**
     * 设置刷新资源图标
     * @param resId
     */
    public void setRefreshingResource(int resId){
        refreshHeader.setRefreshingResource(resId);
    }/**
     * 设置加载更多资源图标
     * @param resId
     */
    public void setLoadMoreResource(int resId){
        loadMoreView.setLoadMoreResource(resId);
    }
    /**
     * 设置是否显示上次刷新时间
     */
    public void displayLastRefreshTime(boolean display){
        refreshHeader.displayLastRefreshTime(display);
    }

    /**
     * 判断是否是PullToRefreshRecyclerView保留的itemViewType
     */
    private boolean isReservedItemViewType(int itemViewType) {
        if (itemViewType == TYPE_REFRESH_HEADER || itemViewType == TYPE_LOAD_MORE_FOOTER || headerTypes.contains(itemViewType) || footerTypes.contains(itemViewType)) {
            return true;
        } else {
            return false;
        }
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

    /**
     * 添加HeadView
     */
    public void addHeaderView(View view) {
        headerTypes.add(TYPE_HEADER_VIEWS_INIT + headViews.size());
        headViews.add(view);
        dataObserver.onChanged();
        if (pullToRefreshRecyclerViewAdapter != null) {
            pullToRefreshRecyclerViewAdapter.adapter.notifyDataSetChanged();
        }
    }

    public void removeAllHeaderViews() {
        headViews.clear();
        headerTypes.clear();
        dataObserver.onChanged();
        if (pullToRefreshRecyclerViewAdapter != null
                &&pullToRefreshRecyclerViewAdapter.adapter!=null) {
            pullToRefreshRecyclerViewAdapter.adapter.notifyDataSetChanged();
        }
    }

    public void removeHeaderViewByIndex(int index) {
        if (index >= headViews.size()) {
            throw new IndexOutOfBoundsException("Invalid index "+index+", headerViews size is "+ headViews.size());
        }
        headViews.remove(index);
        headerTypes.remove(headerTypes.get(index));
        dataObserver.onChanged();
        if (pullToRefreshRecyclerViewAdapter != null
                &&pullToRefreshRecyclerViewAdapter.adapter!=null) {
            pullToRefreshRecyclerViewAdapter.adapter.notifyDataSetChanged();
        }
    }

    public List<View> getHeaderViews() {
        return headViews;
    }

    public View getHeaderViewByIndex(int index) {
        if (index >= headViews.size()) return null;
        return headViews.get(index);
    }

    public void addFooterView(View view) {
        footerTypes.add(TYPE_FOOTER_VIEW_INIT + footerViews.size());
        footerViews.add(view);
        dataObserver.onChanged();
        if (pullToRefreshRecyclerViewAdapter != null) {
            pullToRefreshRecyclerViewAdapter.adapter.notifyDataSetChanged();
        }
    }

    public List<View> getFooterViews() {
        return footerViews;
    }

    public View getFooterViewByIndex(int index) {
        if (index >= footerViews.size()) return null;
        return footerViews.get(index);
    }

    public void removeAllFooterViews() {
        if(footerViews.size()==0) return;
        footerTypes.clear();
        footerViews.clear();
        dataObserver.onChanged();
        if (pullToRefreshRecyclerViewAdapter != null
                &&pullToRefreshRecyclerViewAdapter.adapter!=null) {
            pullToRefreshRecyclerViewAdapter.adapter.notifyDataSetChanged();
        }
    }

    public void removeFooterViewByIndex(int index) {
        if (index >= footerViews.size()) {
            throw new IndexOutOfBoundsException("Invalid index "+index+", footerView size is "+footerViews.size());
        }
        footerViews.remove(index);
        footerTypes.remove(headerTypes.get(index));
        dataObserver.onChanged();
        if (pullToRefreshRecyclerViewAdapter != null
                &&pullToRefreshRecyclerViewAdapter.adapter!=null) {
            pullToRefreshRecyclerViewAdapter.adapter.notifyDataSetChanged();
        }
    }

    public void setRefreshComplete() {
        if (refreshHeader != null) {
            refreshHeader.setRefreshComplete();
        }
    }

    public void setRefreshFail() {
        if (refreshHeader != null) {
            refreshHeader.setRefreshFail();
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        pullToRefreshRecyclerViewAdapter = new PullToRefreshRecyclerViewAdapter(adapter);
        super.setAdapter(pullToRefreshRecyclerViewAdapter);
        adapter.registerAdapterDataObserver(dataObserver);
        dataObserver.onChanged();
    }

    @Override
    public Adapter getAdapter() {
        if (pullToRefreshRecyclerViewAdapter != null) {
            return pullToRefreshRecyclerViewAdapter.getAdapter();
        }
        return null;
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        if (pullToRefreshRecyclerViewAdapter != null) {
            if (layout instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) layout);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return (pullToRefreshRecyclerViewAdapter.isHeader(position)
                                || pullToRefreshRecyclerViewAdapter.isLoadMoreFooter(position)
                                || pullToRefreshRecyclerViewAdapter.isRefreshHeader(position))
                                || pullToRefreshRecyclerViewAdapter.isFooter(position)
                                || pullToRefreshRecyclerViewAdapter.isEmptyView(position)
                                ? gridManager.getSpanCount() : 1;
                    }
                });

            }
        }
    }

    private float lastY = -1;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = e.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = e.getRawY() - lastY;
                lastY = e.getRawY();
                if (refreshHeader.getVisibleHeight() == 0 && moveY < 0) {
                    return super.onTouchEvent(e);
                }
                if (isOnTop() && pullRefreshEnabled && refreshHeader.getRefreshState() != RefreshHead.STATE_REFRESHING) {
                    refreshHeader.onMove((int) (moveY / DRAG_RATE));
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                refreshHeader.checkRefresh();
                break;
        }
        return super.onTouchEvent(e);
    }

    /**
     * 判断列表是否滑到顶部
     *
     * @return
     */
    private boolean isOnTop() {
        if (refreshHeader.getParent() != null) {
            return true;
        } else {
            return false;
        }
    }

    int lastVisibleItemPosition;

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == RecyclerView.SCROLL_STATE_IDLE && pullToRefreshListener != null
                && loadingMoreEnabled && loadMoreView.getVisibility() != View.VISIBLE) {
            LayoutManager layoutManager = getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager)
                        .findLastVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
                lastVisibleItemPosition = findMax(into);
            } else {
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager)
                        .findLastVisibleItemPosition();
            }
            if (layoutManager.getChildCount() > 0 &&
                    lastVisibleItemPosition >= pullToRefreshRecyclerViewAdapter.getItemCount() - 1
//                    && layoutManager.getItemCount() > layoutManager.getChildCount()
                    && refreshHeader.getRefreshState() != RefreshHead.STATE_REFRESHING) {
                loadMoreView.setVisibility(VISIBLE);
                loadMoreView.startAnimation();
                pullToRefreshListener.onLoadMore();
            }
       }
    }


    public void setLoadMoreComplete() {
        loadMoreView.loadMoreComplete(this);
    }


    public void setLoadMoreFail() {
        loadMoreView.loadMoreFail(this);
    }


    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    public void onRefresh() {
        refreshHeader.setRefreshing();
    }

    private class PullToRefreshRecyclerViewAdapter extends Adapter<ViewHolder> {

        private Adapter adapter;

        public PullToRefreshRecyclerViewAdapter(Adapter adapter) {
            this.adapter = adapter;
        }

        public Adapter getAdapter() {
            return adapter;
        }

        public boolean isHeader(int position) {
            return position >= 1 && position < headViews.size() + 1;
        }

        public boolean isFooter(int position) {
            int count = 0;
            if(shouldDisplayEmptyView()) count = 1;
            return position >= 1 && !isLoadMoreFooter(position) && position >= 1+headViews.size()+adapter.getItemCount()+count;
        }

        public boolean isLoadMoreFooter(int position) {
            if (loadingMoreEnabled) {
                return position == getItemCount() - 1;
            } else {
                return false;
            }
        }

        public boolean isRefreshHeader(int position) {
            return position == 0;
        }

        public int getHeadersCount() {
            return headViews.size();
        }

        public int getFootersCount(){
            return footerViews.size();
        }

        /**
         * 根据ItemType获取对应的HeadView
         */
        private View getHeaderViewByType(int itemType) {
            if (!isHeaderType(itemType)) {
                return null;
            }
            return headViews.get(itemType - TYPE_HEADER_VIEWS_INIT);
        }

        /**
         * 判断ItemType是否为HeaderType
         */
        private boolean isHeaderType(int itemViewType) {
            return headViews.size() > 0 && headerTypes.contains(itemViewType);
        }


        /**
         * 根据ItemType获取对应的FooterView
         */
        private View getFooterViewByType(int itemType) {
            if (!isFooterType(itemType)) {
                return null;
            }
            return footerViews.get(itemType - TYPE_FOOTER_VIEW_INIT);
        }
        /**
         * 判断ItemType是否为FooterType
         */
        private boolean isFooterType(int itemViewType) {
            return footerTypes.size() > 0 && footerTypes.contains(itemViewType);
        }

        private boolean isEmptyView(int position){
            return shouldDisplayEmptyView() && position==1+headViews.size();
        }
        /**
         * 是否需要显示EmptyView
         * @return
         */
        private boolean shouldDisplayEmptyView(){
            return adapter.getItemCount()==0 && emptyView!=null;
        }
        @Override
        public int getItemCount() {
            int count;
            if (loadingMoreEnabled) {
                if (adapter != null) {
                    count = getHeadersCount() + getFootersCount()+adapter.getItemCount() + 2;
                } else {
                    count = getHeadersCount()+ getFootersCount() + 2;
                }
            } else {
                if (adapter != null) {
                    count = getHeadersCount()+ getFootersCount() + adapter.getItemCount() + 1;
                } else {
                    count = getHeadersCount()+ getFootersCount() + 1;
                }
            }
            //如果Adapter中没有数据 则多加1用于显示EmptyView
            if(shouldDisplayEmptyView()){
                count+=1;
            }
            return count;
        }

        @Override
        public int getItemViewType(int position) {
            int adjPosition = position - (getHeadersCount() +getFootersCount()+ 1);
            if(shouldDisplayEmptyView()) adjPosition--;
            if (isRefreshHeader(position)) {
                return TYPE_REFRESH_HEADER;
            }
            if (isHeader(position)) {
                position = position - 1;
                return headerTypes.get(position);
            }
            if(shouldDisplayEmptyView()&& position==getHeadersCount()+1){
                return TYPE_EMPTY_VIEW;
            }
            if(isFooter(position)){
                position = position-1-headViews.size()-adapter.getItemCount();
                if(shouldDisplayEmptyView()) position--;
                return footerTypes.get(position);
            }
            if (isLoadMoreFooter(position)) {
                return TYPE_LOAD_MORE_FOOTER;
            }
            int adapterCount;
            if (adapter != null) {
                adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    int type = adapter.getItemViewType(adjPosition);
                    if (isReservedItemViewType(type)) {
                        throw new IllegalStateException("PullToRefreshRecyclerView require itemViewType in adapter should be less than 10000 ");
                    }
                    return type;
                }
            }
            return 0;
        }



        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_REFRESH_HEADER) {
                return new SimpleViewHolder(refreshHeader);
            } else if (isHeaderType(viewType)) {
                return new SimpleViewHolder(getHeaderViewByType(viewType));
            } else if (viewType == TYPE_EMPTY_VIEW ){
                return new SimpleViewHolder(emptyView);
            } else if(isFooterType(viewType)){
                return new SimpleViewHolder(getFooterViewByType(viewType));
            } else if (viewType == TYPE_LOAD_MORE_FOOTER) {
                return new SimpleViewHolder(loadMoreView);
            }
            return adapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (isHeader(position) || isRefreshHeader(position) || isFooter(position)
                    ||isEmptyView(position) || isLoadMoreFooter(position)) {
                return;
            }
            int adjPosition = position - (getHeadersCount() + 1);
            int adapterCount;
            if (adapter != null) {
                adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    adapter.onBindViewHolder(holder, adjPosition);
                }
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
            if (isHeader(position) || isRefreshHeader(position) || isFooter(position)
                    ||isEmptyView(position) || isLoadMoreFooter(position)) {
                return;
            }
            int adjPosition = position - (getHeadersCount() + 1);
            int adapterCount;
            if (adapter != null) {
                adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    if (payloads.isEmpty()) {
                        adapter.onBindViewHolder(holder, adjPosition);
                    } else {
                        adapter.onBindViewHolder(holder, adjPosition, payloads);
                    }
                }
            }
        }

        @Override
        public long getItemId(int position) {
            if (adapter != null && position >= getHeadersCount() + 1) {
                int adjPosition = position - (getHeadersCount() + 1);
                if (adjPosition < adapter.getItemCount()) {
                    return adapter.getItemId(adjPosition);
                }
            }
            return -1;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return (isHeader(position) || isLoadMoreFooter(position)
                                || isRefreshHeader(position) || isFooter(position)
                                || isEmptyView(position))
                                ? gridManager.getSpanCount() : 1;
                    }
                });
            }
            adapter.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            adapter.onDetachedFromRecyclerView(recyclerView);
        }

        @Override
        public void onViewAttachedToWindow(ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null
                    && lp instanceof StaggeredGridLayoutManager.LayoutParams
                    && (isHeader(holder.getLayoutPosition()) || isRefreshHeader(holder.getLayoutPosition()) || isLoadMoreFooter(holder.getLayoutPosition()))) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
            adapter.onViewAttachedToWindow(holder);
        }

        @Override
        public void onViewDetachedFromWindow(ViewHolder holder) {
            adapter.onViewDetachedFromWindow(holder);
        }

        @Override
        public void onViewRecycled(ViewHolder holder) {
            adapter.onViewRecycled(holder);
        }

        @Override
        public boolean onFailedToRecycleView(ViewHolder holder) {
            return adapter.onFailedToRecycleView(holder);
        }

        @Override
        public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
            adapter.unregisterAdapterDataObserver(observer);
        }

        @Override
        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            adapter.registerAdapterDataObserver(observer);
        }

        private class SimpleViewHolder extends ViewHolder {
            public SimpleViewHolder(View itemView) {
                super(itemView);
            }
        }

    }

    private class DataObserver extends AdapterDataObserver {

        @Override
        public void onChanged() {
            if (pullToRefreshRecyclerViewAdapter != null) {
                pullToRefreshRecyclerViewAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            pullToRefreshRecyclerViewAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            pullToRefreshRecyclerViewAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            pullToRefreshRecyclerViewAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            pullToRefreshRecyclerViewAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            pullToRefreshRecyclerViewAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    }

}
