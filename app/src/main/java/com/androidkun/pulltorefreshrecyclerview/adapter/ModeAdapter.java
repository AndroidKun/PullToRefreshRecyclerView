package com.androidkun.pulltorefreshrecyclerview.adapter;

import android.content.Context;

import com.androidkun.pulltorefreshlibrary.adapter.BaseAdapter;
import com.androidkun.pulltorefreshlibrary.adapter.ViewHolder;
import com.androidkun.pulltorefreshrecyclerview.R;

import java.util.List;

/**
 * Created by Kun on 2016/12/14.
 * GitHub: https://github.com/AndroidKun
 * CSDN: http://blog.csdn.net/a1533588867
 * Description:模版
 */

public class ModeAdapter extends BaseAdapter {

    public ModeAdapter(Context context, List datas) {
        super(context, R.layout.item_mode, datas);
    }

    @Override
    public void convert(ViewHolder holder, Object o) {
        holder.setText(R.id.textView,(String)o);
    }
}
