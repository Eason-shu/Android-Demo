package com.shu.spinner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;

import com.shu.R;
import com.shu.model.Select;

import java.util.List;

/**
 * @Author shu
 * @Version 1.0
 * @Date: 2022/07/27/ 16:21
 * @Description 可搜索的下拉框
 **/
public class SearchSpinner extends RelativeLayout implements View.OnClickListener, AdapterView.OnItemClickListener, TextWatcher {

    private static final String TAG = "SearchSpinner";
    private EditText editText;
    private ImageView mRightIv;
    private View mRightImageTopView;
    private Context mContext;
    private ListPopupWindow popupWindow;
    BaseEditSpinnerAdapter adapter;
    private TypedArray tArray;
    private boolean isPopupWindowShowing;
    private Animation mAnimation;
    private Animation mResetAnimation;
    private List<Select> datas;



    public SearchSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView(attrs);
        initAnimation();
    }


    /**
     * 设置数据源
     * @param data
     */
    public void setItemData(List<Select> data) {
        datas = data;
        adapter = new SimpleAdapter(mContext, data);
        setAdapter(adapter);
    }


    /**
     * 反显绑定数据
     * @param text
     */
    public void setText(String text) {
        for (Select data : datas) {
            if (data.getValue().equals(text)) {
                editText.setText(data.getName());
                popupWindow.dismiss();
                break;
            }
        }
    }


    /**
     * 设置文本颜色
     * @param color
     */
    public void setTextColor(@ColorInt int color) {
        editText.setTextColor(color);
    }


    /**
     * 获取文本
     * @return
     */
    public String getText() {
        return editText.getText().toString();
    }


    /**
     * 返回现在的数据
     * @return
     */
    public Select getSelect() {
        for (Select data : datas) {
            if (data.getName().equals(editText.getText().toString())) {
                return data;
            }
        }
        return null;
    }


    /**
     * 设置提示文本
     * @param hint
     */
    public void setHint(String hint) {
        editText.setHint(hint);
    }


    public void setRightImageDrawable(Drawable drawable) {
        mRightIv.setImageDrawable(drawable);
    }

    public void setRightImageResource(@DrawableRes int res) {
        mRightIv.setImageResource(res);
    }

    /**
     * 设置设配器
     * @param adapter
     */
    public void setAdapter(BaseEditSpinnerAdapter adapter) {
        this.adapter = adapter;
        setBaseAdapter(this.adapter);
    }

    /**
     * 初始化动画
     */
    private void initAnimation() {
        mAnimation = new RotateAnimation(0, -90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mAnimation.setDuration(300);
        mAnimation.setFillAfter(true);
        mResetAnimation = new RotateAnimation(-90, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mResetAnimation.setDuration(300);
        mResetAnimation.setFillAfter(true);
    }


    /**
     * 初始化布局
     */
    private void initView(AttributeSet attrs) {
        LayoutInflater.from(mContext).inflate(R.layout.edit_spinner, this);
        editText = (EditText) findViewById(R.id.edit_sipnner_edit);
        editText.setOnClickListener(this);
        mRightIv = (ImageView) findViewById(R.id.edit_spinner_expand);
        mRightImageTopView = findViewById(R.id.edit_spinner_expand_above);
        mRightImageTopView.setOnClickListener(this);
        mRightImageTopView.setClickable(false);
        mRightIv.setOnClickListener(this);
        mRightIv.setRotation(90);
        editText.addTextChangedListener(this);
        tArray = mContext.obtainStyledAttributes(attrs, R.styleable.EditSpinner);
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showFilterData("");
                } else {

                }
            }
        });

        editText.setHint(tArray.getString(R.styleable.EditSpinner_hint));
        int imageId = tArray.getResourceId(R.styleable.EditSpinner_rightImage, 0);
        if (imageId != 0) {
            mRightIv.setImageResource(imageId);
        }
        int bg = tArray.getResourceId(R.styleable.EditSpinner_Background, 0);
        if (bg != 0) {
            editText.setBackgroundResource(bg);
        }
        tArray.recycle();



    }

    /**
     * 设置适配器
     *
     * @param adapter
     */
    private final void setBaseAdapter(BaseAdapter adapter) {
        if (popupWindow == null) {
            initPopupWindow();
        }
        popupWindow.setAdapter(adapter);
    }



    /**
     * 初始化PopupWindow
     */
    private void initPopupWindow() {
        popupWindow = new ListPopupWindow(mContext) {
            @Override
            public boolean isShowing() {
                return isPopupWindowShowing;
            }

            @Override
            public void show() {
                super.show();
                isPopupWindowShowing = true;
                mRightImageTopView.setClickable(true);
                mRightIv.startAnimation(mAnimation);
            }
        };
        popupWindow.setOnItemClickListener(this);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.setPromptPosition(ListPopupWindow.POSITION_PROMPT_BELOW);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setAnchorView(editText);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                isPopupWindowShowing = false;
                mRightIv.startAnimation(mResetAnimation);
            }
        });
    }


    /**
     * 监听点击事件
     *
     * @param v
     */
    @Override
    public final void onClick(View v) {
        Log.e(TAG, "onClick: ");
        if (adapter == null || popupWindow == null) {
            return;
        }
        if (v.getId() == R.id.edit_spinner_expand_above) {
            v.setClickable(false);
            return;
        }

        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            showFilterData("");
        }


        if (v.getId() == R.id.edit_sipnner_edit) {
            Log.e(TAG, "点击事件");

        }
    }

    /**
     * 显示筛选数据
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public final void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        editText.setText(((BaseEditSpinnerAdapter) parent.getAdapter()).getItemString(position));
        mRightImageTopView.setClickable(false);
        popupWindow.dismiss();
    }

    /**
     * 文本变化监听
     *
     * @param s
     */
    @Override
    public final void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    /**
     * 文本变化监听
     *
     * @param s
     * @param start
     * @param before
     * @param count
     */
    @Override
    public final void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    /**
     * 文本变化监听
     *
     * @param s
     */
    @Override
    public final void afterTextChanged(Editable s) {
        String key = s.toString();
        editText.setSelection(key.length());
        if (!TextUtils.isEmpty(key)) {
            showFilterData(key);
        } else {
            popupWindow.dismiss();
        }
    }




    /**
     * 过滤筛选数据
     *
     * @param key
     */
    private void showFilterData(String key) {
        if (popupWindow == null || adapter == null || adapter.getEditSpinnerFilter() == null) {
            if (popupWindow != null) {
                popupWindow.dismiss();
            }
            return;
        }
        if (adapter.getEditSpinnerFilter().onFilter(key)) {
            popupWindow.dismiss();
        } else {
            popupWindow.show();
        }

    }


}
