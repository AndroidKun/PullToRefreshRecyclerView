# PullToRefreshRecyclerView
![image](https://github.com/AndroidKun/PullToRefreshRecyclerView/blob/master/gif/GIF.gif)   

## 1. 在Module下的build.gradle中添加依赖
   
    compile 'com.androidkun:pulltorefreshrecyclerview:1.0.4'
   
##2. 在布局文件中添加PullToRefreshRecyclerView控件
   
    <com.androidkun.PullToRefreshRecyclerView
        android:id="@+id/pullToRefreshRV"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
     
##3. 初始化PullToRefreshRecyclerView并设置属性和回调

    pullToRefreshRV = (PullToRefreshRecyclerView) findViewById(R.id.pullToRefreshRV);
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    pullToRefreshRV.setLayoutManager(layoutManager);
    adapter = new ModeAdapter(this, R.layout.item_mode,data);
    pullToRefreshRV.setAdapter(adapter);
    //设置是否显示上次刷新的时间
    pullToRefreshRV.displayLastRefreshTime(true);
    //设置刷新回调
    pullToRefreshRV.setPullToRefreshListener(this);
    //主动触发下拉刷新操作
    //pullToRefreshRV.onRefresh();
     
### <font color=#f00>如果想使用网格列表，则相应设置布局管理者为网格布局管理者就行了</font>
     
    GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
    recyclerView.setLayoutManager(gridLayoutManager);
     
### 此外也可以通过 <font color=#f00>setRefreshingResource（int resId）</font>和<font color=#f00>setLoadMoreResource（int resId）</font>自定义刷新箭头和加载的图标。

## 4.处理刷新加载逻辑

```
 @Override
    public void onRefresh() {
        pullToRefreshRV.postDelayed(new Runnable() {
            @Override
            public void run() {
                pullToRefreshRV.setRefreshComplete();
                //模拟没有数据的情况
                data.clear();
                adapter.notifyDataSetChanged();
            }
        }, 3000);
    }

    @Override
    public void onLoadMore() {
        pullToRefreshRV.postDelayed(new Runnable() {
            @Override
            public void run() {
                pullToRefreshRV.setLoadMoreComplete();
                //模拟加载数据的情况
                int size = data.size();
                for (int i = size; i < size + 4; i++) {
                    data.add("" + i + i + i + i);
                }
                adapter.notifyDataSetChanged();
            }
        }, 3000);
    }
```

### 框架中提供封装好的BaseAdapter，减少编写相同的代码，提高开发效率，Demo中的ModeAdapter就是继承了BaseAdapter，代码如下：

```
public class ModeAdapter extends BaseAdapter {

    public ModeAdapter(Context context, int layoutId, List datas) {
        super(context, layoutId, datas);
    }

    @Override
    public void convert(ViewHolder holder, Object o) {
        holder.setText(R.id.textView, (String) o);
    }

}
```
 
 
    
  
