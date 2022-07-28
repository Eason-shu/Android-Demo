package com.shu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.shu.Adapter.GuideViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 引导页
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener ,ViewPager.OnPageChangeListener{

    private ViewPager viewpager;
    // 适配器
    private GuideViewPagerAdapter adapter;
    //View数据
    private List<View> views;
    //引导页图片、布局资源
    private int[] pics = {R.layout.loading_view1, R.layout.loading_view2, R.layout.loading_view3};
    //引导点资源
    private ImageView[] imageViews;
    //引导点
    private ImageView imageOne, imageTwo, imageThree;
    private Button button_01;
    private TextView textView_01;



    /**
     * 初始化
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences preferences = this.getSharedPreferences( "data_init", Context.MODE_PRIVATE );
        // 未引导页过
        if(preferences.getString("flag", "").equals("")){
            viewpager = this.findViewById(R.id.viewpager);
            imageOne = this.findViewById(R.id.imageOne);
            imageTwo = this.findViewById(R.id.imageTwo);
            imageThree = this.findViewById(R.id.imageThree);
            initView();
            textView_01=findViewById(R.id.skip_home);
            textView_01.setOnClickListener(this);
        }
        else {
            GoToLogin();
        }
    }


    /**
     * 初始化布局
     */
    private void initView() {
        if (views == null) {
            views = new ArrayList<>();
        }
        if (imageViews == null) {
            imageViews = new ImageView[3];
        }
        //初始化引导点,赋值
        imageViews[0] = imageOne;
        imageViews[1] = imageTwo;
        imageViews[2] = imageThree;
        //默认全部为未选中
        setPointSelect(true, 0);
        //初始化引导页视图列表,将3个布局View添加至list中
        for (int i = 0; i < pics.length; i++) {
            View view = LayoutInflater.from(this).inflate(pics[i], null);
            views.add(view);
        }
        //初始化adapter
        adapter = new GuideViewPagerAdapter(views);
        //将adapter设置到viewpager中
        viewpager.setAdapter(adapter);
        //ViewPager切换事件
        viewpager.addOnPageChangeListener((ViewPager.OnPageChangeListener) this);
    }


    /**
     * ViewPager滑动事件
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //当前页面被滑动时调用
        //position :当前页面，及你点击滑动的页面
        //positionOffset:当前页面偏移的百分比
        //positionOffsetPixels:当前页面偏移的像素位置
    }



    /**
     * 那个页面被选择
     * @param position
     */
    @Override
    public void onPageSelected(int position) {
        //先设置为未选中，在将当前设置为选中状态
        setPointSelect(false, position);
        // 最后一个位置时
        if(position==2){
            button_01=findViewById(R.id.start_home);
            button_01.setOnClickListener(this);
        }
    }



    @Override
    public void onPageScrollStateChanged(int state) {
        //当滑动状态改变时调用
        //state ==1 的时候默示正在滑动
        //state ==2 的时候默示滑动完毕了
        //state ==0 的时候默示什么都没做
    }


    /**
     * 设置引导点为为选中状态
     */
    private void setPointSelect(boolean state, int position) {
        for (int i = 0; i < imageViews.length; i++) {
            imageViews[i].setBackgroundResource(R.drawable.unselect);
        }
        if (state) {
            //第一张为选中状态
            imageViews[0].setBackgroundResource(R.drawable.select);
        } else {
            imageViews[position].setBackgroundResource(R.drawable.select);
        }
    }


    /**
     * 点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        // 来到登录页面
        if(v.getId() == R.id.skip_home){
            @SuppressLint("CommitPrefEdits")
            SharedPreferences.Editor editor=getSharedPreferences("data_init",MODE_PRIVATE).edit();
            editor.putString("flag","qwaszx123@A");
            editor.apply();
            GoToLogin();
        }
        // 来到登录页面
        if(v.getId()==R.id.start_home){
            @SuppressLint("CommitPrefEdits")
            SharedPreferences.Editor editor=getSharedPreferences("data_init",MODE_PRIVATE).edit();
            editor.putString("flag","qwaszx123@A");
            editor.apply();
            GoToLogin();
        }
    }


    /**
     * 跳转登录页面
     */
    public void GoToLogin(){
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
//        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }


//    @Override
//    public void finish() {
//        Log.e("info","监听事件");
////        DialogFragment mLocationDialog = new AlertDialogFragment().setTitle("启动位置服务")
////                .setMessage("为了您能更好地使用本功能，请开启定位服务，否则无法使用此功能。")
////                .setButtons("返回", "设置")
////                .setOnButtonClickListener(new DialogInterface.OnClickListener() {
////                    @Override
////                    public void onClick(DialogInterface dialogInterface, int i) {
////                        switch (i) {
////                            case 0: //"取消"按钮直接退出
////                                getActivity().finish();
////                                return;
////                            case 1: //"设置"按钮设置GPS
////                                LocationServiceUtils.gotoLocServiceSettings(getAppContext());
////                                return;
////                            default:
////                                return;
////                        }
////                    }
////                });
//    }
}