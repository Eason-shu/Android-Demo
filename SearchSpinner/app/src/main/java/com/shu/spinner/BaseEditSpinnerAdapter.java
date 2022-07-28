package com.shu.spinner;

import android.widget.BaseAdapter;

/**
 * @Author shu
 * @Version 1.0
 * @Date: 2022/07/27/ 16:22
 * @Description 基础的SpinnerAdapter
 */

public abstract class BaseEditSpinnerAdapter extends BaseAdapter {
    /**
     * editText输入监听
     *
     * @return
     */
    public abstract EditSpinnerFilter getEditSpinnerFilter();

    /**
     * 获取需要填入editText的字符串
     * @param position
     * @return
     */
    public abstract String getItemString(int position);

}
