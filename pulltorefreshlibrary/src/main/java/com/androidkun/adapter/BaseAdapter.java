package com.androidkun.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kun on 2016/12/14.
 * GitHub: https://github.com/AndroidKun
 * CSDN: http://blog.csdn.net/a1533588867
 * Description: 通用Adapter基类
 */

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<ViewHolder> {

    protected Context context;
    protected  int layoutId;
    protected List<T> datas;
    protected LayoutInflater inflater;

    protected final static int GET = 1;
    protected final static int POST = 2;
    /**
     * 设置是否显示EmptyView
     */
    protected boolean showEmptyView = false;
    /**
     * 标识是否显示EmptyView
     */
    protected boolean isShowEmptyView = false;
    /**
     * 全部加载完毕是否显示底部View
     */
    protected boolean isShowEndView = false;
    /**
     * 是否添加了显示底部View的数据
     */
    protected boolean isAddShowEndViewData = false;

    public BaseAdapter(Context context, int layoutId, List<T> datas) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.layoutId = layoutId;
        this.datas = datas;
        if(this.datas == null){
            this.datas = new ArrayList<>();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ViewHolder.get(context,parent,layoutId);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        convert(holder,datas.get(position));
    }

    public abstract void convert(ViewHolder holder, T t);

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public List<T> getDatas(){
        return datas;
    }

    public void setDatas(List<T> datas){
        this.datas = datas;
        if(this.datas == null){
            this.datas = new ArrayList<>();
            isShowEmptyView = true;
        }else{
            isShowEmptyView = false;
        }
        notifyDataSetChanged();
    }

    public void insertDataAtTop(T newData){
        if(datas!=null){
            datas.add(0,newData);
        }else{
            datas =  new ArrayList<>();
            datas.add(newData);
        }
        notifyDataSetChanged();
    }

    public void addDatas(List<T> newDatas){
        if(this.datas == null){
            this.datas = new ArrayList<>();
        }
        int last = datas.size();
        this.datas.addAll(newDatas);
        notifyItemInserted(last + 1);
    }
    public void addData(T newData){
        if(this.datas == null){
            this.datas = new ArrayList<>();
        }
        this.datas.add(newData);
        notifyDataSetChanged();
    }
    public void addData(T newData,int index){
        if(this.datas == null){
            this.datas = new ArrayList<>();
        }
        this.datas.add(index,newData);
        notifyDataSetChanged();
    }

    public void setShowEmptyView(boolean showEmptyView) {
        this.showEmptyView = showEmptyView;
    }

    public void setIsShowEmptyView(boolean show) {
        if(!showEmptyView) return;
        isShowEmptyView = show;
        if(isShowEmptyView){
            this.datas.clear();
            insertDataAtTop((T) new Object());
        }

    }

    public boolean isShowEndView() {
        return isShowEndView;
    }

    public void setShowEndView(boolean showEndView) {
        isShowEndView = showEndView;
    }

    public void addEndViewData(){
        if(isAddShowEndViewData || !isShowEndView) return;
        isAddShowEndViewData = true;
        if(datas==null){
            datas = new ArrayList<>();
        }
        datas.add((T) new Object());
    }

    public void removeEndViewData(){
        if(!isAddShowEndViewData || !isShowEndView) return;
        isAddShowEndViewData = false;
        if(datas!=null && datas.size()>0){
            datas.remove(datas.size()-1);
        }
    }


    public void removeItem(int position){
        if(datas != null && datas.size() > position){
            datas.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clearDatas(){
        this.datas.clear();
        notifyDataSetChanged();
    }

}
