package com.shu.Adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

/**
 * @Author shu
 * @Version 1.0
 * @Date: 2022/06/26/ 12:54
 * @Description 引导页面适配器
 **/
public class GuideViewPagerAdapter extends PagerAdapter {
    private List<View> views;

    public GuideViewPagerAdapter(List<View> views) {
        super();
        this.views = views;
    }



    /**
     * 返回显示多少个页面
     * @return
     */
    @Override
    public int getCount() {
        if (views != null) {
            return views.size();
        }
        return 0;
    }


    /**
     *判断初始化返回的Object是不是一个View对象
     * @param view
     * @param object
     * @return
     */
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (object);
    }

    /**
     * 销毁对象
     * @param container
     * @param position
     * @param object
     */
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(views.get(position));
    }

    /**
     * 初始化对象
     * @param container
     * @param position
     * @return
     */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        (container).addView(views.get(position), 0);
        return views.get(position);
    }

}

