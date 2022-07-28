package com.shu.spinner;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.shu.model.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author shu
 * @Version 1.0
 * @Date: 2022/07/27/ 16:22
 * @Description
 **/
public class SimpleAdapter extends BaseEditSpinnerAdapter implements EditSpinnerFilter {

    private Context mContext;
    private List<Select> mSpinnerData;
    private List<Select> mCacheData;
    private int[] indexs;

    /**
     * 构造器
     * @param context
     * @param spinnerData
     */
    public SimpleAdapter(Context context, List<Select> spinnerData) {
        this.mContext = context;
        this.mSpinnerData = spinnerData;
        mCacheData = new ArrayList<>(spinnerData);
        indexs = new int[mSpinnerData.size()];
    }


    @Override
    public EditSpinnerFilter getEditSpinnerFilter() {
        return this;
    }

    /**
     * 获取选择的字符串
     * @param position
     * @return
     */
    @Override
    public String getItemString(int position) {
        return mSpinnerData.get(indexs[position]).getName();
    }

    @Override
    public int getCount() {
        return mCacheData == null ? 0 : mCacheData.size();
    }

    /**
     * 返回选中的选项
     * @param position
     * @return
     */
    @Override
    public Select getItem(int position) {
        return mCacheData == null ? null : mCacheData.get(position) == null ? null : mCacheData.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = null;
        if (convertView == null) {
            textView = (TextView) LayoutInflater.from(mContext).inflate(android.R.layout.simple_spinner_dropdown_item, null);
        } else {
            textView = (TextView) convertView;
        }
        textView.setText(Html.fromHtml(getItem(position).getName()));
        return textView;
    }


    /**
     * 搜索框输入监听过滤
     * @param keyword
     * @return
     */
    @Override
    public boolean onFilter(String keyword) {
        mCacheData.clear();
        if (TextUtils.isEmpty(keyword)) {
            mCacheData.addAll(mSpinnerData);
            for (int i = 0; i < indexs.length; i++) {
                indexs[i] = i;
            }
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("[^\\s]*").append(keyword).append("[^\\s]*");
            for (int i = 0; i < mSpinnerData.size(); i++) {
                if (mSpinnerData.get(i).getName().replaceAll("\\s+", "|").matches(builder.toString())) {
                    indexs[mCacheData.size()] = i;
                    mCacheData.add(new Select(mSpinnerData.get(i).getName().replaceFirst(keyword, "<font color=\"#1661ab\">" + keyword + "</font>"),mSpinnerData.get(i).getValue()));
                }
            }
        }
        notifyDataSetChanged();
        return mCacheData.size() <= 0;
    }











}
