package com.shu;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * @Author shu
 * @Version 1.0
 * @Date: 2022/06/27/ 14:58
 * @Description
 **/
public class DropDownMenuListAdapter<T> extends BaseAdapter {
    private Context context;
    private List<T> list;
    private int layoutId;//单布局
    private int variableId;

    public DropDownMenuListAdapter(Context context, List<T> list, int layoutId, int variableId) {
        this.context = context;
        this.list = list;
        this.layoutId = layoutId;
        this.variableId = variableId;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(layoutId, null);
            holder = new ViewHolder();
            holder.tv = convertView.findViewById(variableId);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // 强制转换成你的数据类型
        String item = (String) getItem(position);
        holder.tv.setText(item);
        // 菜单项的背景色
        if(position%2 == 0) {
            holder.tv.setBackgroundColor(Color.parseColor("#f7f7f8"));
        } else {
            holder.tv.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        return convertView;
    }

    private static class ViewHolder {
        public TextView tv;
    }
}