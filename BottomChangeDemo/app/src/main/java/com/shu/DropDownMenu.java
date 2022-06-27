package com.shu;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * @Author shu
 * @Version 1.0
 * @Date: 2022/06/27/ 14:48
 * @Description
 **/
public class DropDownMenu extends RelativeLayout {
    // 菜单显示栏字体颜色
    private int textColor = 0xaa333333;
    // 默认显示字
    private String placeholder = "请选择";
    // 菜单显示栏字体大小
    private int menuTextSize = 14;
    // 弹窗宽度（建议与显示框等长）
    private int popupWidth = 150;
    // 图标（一般是箭头）
    private int menuIcon;
    //菜单项被选中位置 初始没有菜单被选中记为-1
    private int selectPosition = -1;
    // 文本显示位置
    private Integer textGravity;
    // 菜单项显示个数，默认4个
    int showMenuItemCount = 4;

    // 菜单项适配器
    private BaseAdapter adapter;

    // 弹窗位置偏移，因为会存在弹窗存在初始margin
    int xoff = 10;
    int yoff = 10;

    // 主容器，DropDownMenu自身的 RelativeLayout
    public RelativeLayout mainLy;

    public DropDownMenu(Context context) {
        this(context, null);
    }

    public DropDownMenu(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    // xml传属性,自定义属性attrs
    public DropDownMenu(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DropDownMenu);
        textColor = a.getColor(R.styleable.DropDownMenu_textColor, textColor);
        menuTextSize = a.getDimensionPixelSize(R.styleable.DropDownMenu_menuTextSize, menuTextSize);
        menuIcon = a.getResourceId(R.styleable.DropDownMenu_menuIcon, menuIcon);
        placeholder = a.getString(R.styleable.DropDownMenu_placeholder);
        textGravity = a.getInt(R.styleable.DropDownMenu_textGravity, CENTER_HORIZONTAL);
        popupWidth = a.getDimensionPixelSize(R.styleable.DropDownMenu_popupWidth, popupWidth);
        showMenuItemCount = a.getInt(R.styleable.DropDownMenu_showMenuItemCount,showMenuItemCount);
        a.recycle();

        // 初始化布局
        initViews(context);
    }
    public void setAdapter(BaseAdapter madapter) {
        adapter = madapter;
    }
    private void initViews(Context context) {
        // LinearLayout容器布局
//        setOrientation(LinearLayout.HORIZONTAL);
        setPadding(dp2Px(10), dp2Px(0), dp2Px(10), dp2Px(0));
//        setGravity(Gravity.CENTER_VERTICAL);
        // 容器内容
        addContentView();

        // 点击事件
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mainLy == null){
                    mainLy = (RelativeLayout) v;
                }
                if(v.isActivated()) {
                    v.setActivated(false);
                } else {
                    v.setActivated(true);
                }
                parentClick();
            }
        });
    }

    /**
     * 容器内容
     */
    private void addContentView() {
        // 文本
        TextView tv = new TextView(getContext());
        tv.setSingleLine();
        tv.setEllipsize(TextUtils.TruncateAt.END);
//        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuTextSize);
//        tv.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1));
        tv.setTextColor(textColor);
        tv.setText(placeholder);
        tv.setGravity(textGravity);
        tv.setPadding(dp2Px(0), dp2Px(8), dp2Px(5), dp2Px(8));
        //设置第一个块位置
        LayoutParams paramsOne = new  LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        paramsOne.addRule(RelativeLayout.CENTER_IN_PARENT);
        tv.setLayoutParams(paramsOne);
        addView(tv,0);
        // 箭头图标
        ImageView imageView = new ImageView(getContext());
        imageView.setImageDrawable(getResources().getDrawable(menuIcon));
        //设置第二个块位置
        LayoutParams paramsTwo = new  LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        paramsTwo.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        paramsTwo.addRule(RelativeLayout.CENTER_VERTICAL);
        imageView.setLayoutParams(paramsTwo);
//        imageView.setLayoutParams(new LayoutParams(dp2Px(10),dp2Px(10)));
        addView(imageView,1);
    }

    public void parentClick() {
        View tv = getChildAt(0);
        // 开关菜单
        switchPopupWindow(tv);
    }

    PopupWindow mBankPopupwindow ;

    /**
     * 开关菜单，箭头动画
     * @param targetView 弹窗显示的位置在targetView下方
     */
    private void switchPopupWindow(View targetView) {
        if (mBankPopupwindow != null && mBankPopupwindow.isShowing() ) {
            mBankPopupwindow.dismiss();
        } else {
            if (mBankPopupwindow == null) {
                mBankPopupwindow = initmPopupWindowView();
            }
            ImageView iv = (ImageView) getChildAt(1);
            // 反方向
            iv.animate().setDuration(300).rotation(180).start();
            // 原方向
//            iv.animate().setDuration(300).rotation(0).start();//旋转0度是复位

            // dp转px，解决不同手机错位问题，Popupwindow设置宽度后左边有空隙所以用-，上边距自己设为10
            mBankPopupwindow.showAsDropDown(targetView, -dp2Px(xoff), yoff);
        }
    }


    /**
     * 初始化popupWindow 弹窗
     * @return PopupWindow
     */
    private PopupWindow initmPopupWindowView() {
        // 构造popwindow布局
        LinearLayout ly = new LinearLayout(getContext());
        ly.setOrientation(LinearLayout.HORIZONTAL);
        View customView = View.inflate(getContext() ,R.layout.listview_drop_down_menu, null);
        ly.addView(customView);
        ListView mListView = customView.findViewById(R.id.lv);
        // 简单测试
//        String[] site_list = new String[]{"ss","dd","saa"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.listview_item, R.id.tv, site_list);//创建arrayAdapter，第一个参数是context ，第二个参数是item的样式，第三个参数是数据
        mListView.setAdapter(adapter);
        setListViewHeightBasedOnChildren(mListView,showMenuItemCount);
        mListView.setOnItemClickListener((parent, view, position, id) -> {
            if(mBankPopupwindow!=null) {
                mBankPopupwindow.dismiss();
                onListViewItemClick(((TextView)view.findViewById(R.id.content)).getText().toString(),position);
            }
        });
        // 生成PopupWindow
        final PopupWindow popupwindow = generatePopupWindow(ly);
        popupwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //改变显示的按钮图片为正常状态
                if(mainLy!=null){
                    if(mainLy.isActivated()) {
                        mainLy.setActivated(false);
                    } else {
                        mainLy.setActivated(true);
                    }
                }
                closeMenu();
            }
        });
        return popupwindow;
    }

    /**
     * 生成PopupWindow
     */
    public PopupWindow generatePopupWindow(View customView) {
        final PopupWindow popupwindow = new PopupWindow(customView,
                popupWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupwindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupwindow.setOutsideTouchable(true); // 点击外面消失
        popupwindow.setAnimationStyle(R.style.popwin_anim); // 动画
        popupwindow.setFocusable(false);
        popupwindow.setFocusable(true); // 设置这个，可正常点击的消失和显示
        popupwindow.setClippingEnabled(false); // 设置这个，popupwindow的高度就不会被dialog或者其他容器限制了。
        return popupwindow;
    }

    /**
     * 关闭菜单，箭头动画
     */
    public void closeMenu() {
        ImageView iv = (ImageView) getChildAt(1);
        // 反方向
//        iv.animate().setDuration(300).rotation(180).start();
        // 原方向
        iv.animate().setDuration(300).rotation(0).start();//旋转0度是复位
    }

    /**
     * 菜单项点击事件
     * @param s
     * @param position
     */
    public void onListViewItemClick(String s,int position) {
        TextView tv = (TextView) getChildAt(0);
        tv.setText(s);
        selectPosition = position;
    }

    public int getSelectPosition() {
        return selectPosition;
    }

    private int dp2Px(float value) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, dm);
//        return ViewUtil.dipTopx(getContext(),value);
    }

    /**
     * 动态改变listview的最大高度
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView,int showMenuItemCount) {

        ListAdapter listAdapter = listView.getAdapter();

        if (listAdapter == null) {

            return;
        }

        int totalHeight = 0;

        int newCount = listAdapter.getCount() > showMenuItemCount ? showMenuItemCount : listAdapter.getCount();

        for (int i = 0; i < newCount; i++) {

            View listItem = listAdapter.getView(i, null, listView);

            listItem.measure(0, 0);

            totalHeight += listItem.getMeasuredHeight();

        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight + (listView.getDividerHeight() * (newCount - 1));

        listView.setLayoutParams(params);

    }
}
