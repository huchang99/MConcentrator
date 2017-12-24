package com.system.mconcentrator.mconcentrator.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.system.mconcentrator.mconcentrator.R;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

//deng   wo  xia

/**
 * Created by huchang on 2017/10/24.
 */
public class SelectTimePopupWindow extends PopupWindow {

    //标记错误时间
    public static String SET_DATA_ERROR = "set_data_error";
    private View mMenuView;
    private PickerView year_pv, month_pv, day_pv, hour_pv, Minute_pv;
    private List<String> yearList, monthList, dayList, hourList, MinuteList;
    private TextView set_date_sure_tv;
    private String year_str, month_str, day_str, hour_str, minute_str;
    private Timestamp timeStamp;


    public SelectTimePopupWindow(Activity context, View.OnClickListener clickListener) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.set_time_window, null);

        year_pv = (PickerView) mMenuView.findViewById(R.id.year_pv);
        month_pv = (PickerView) mMenuView.findViewById(R.id.month_pv);
        day_pv = (PickerView) mMenuView.findViewById(R.id.day_pv);
        hour_pv = (PickerView) mMenuView.findViewById(R.id.hour_pv);
        Minute_pv = (PickerView) mMenuView.findViewById(R.id.Minute_pv);
        set_date_sure_tv = (TextView) mMenuView.findViewById(R.id.set_date_sure_tv);
        initPickViewData();

        //设置按钮监听
        set_date_sure_tv.setOnClickListener(clickListener);

        year_pv.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                year_str = text;

            }
        });
        month_pv.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                month_str = text;
            }
        });
        day_pv.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                day_str = text;
            }
        });
        hour_pv.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                hour_str = text;
            }
        });
        Minute_pv.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                minute_str = text;
            }
        });

        // 设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(500);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.pick_view_layout)
                        .getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });


    }

    /**
     * @des 初始化pickView的数据
     */
    private void initPickViewData() {
        yearList = new ArrayList<String>();
        monthList = new ArrayList<String>();
        dayList = new ArrayList<String>();
        hourList = new ArrayList<String>();
        MinuteList = new ArrayList<String>();

        for (int i = 1900; i < 2040; i++) {
            yearList.add("" + i);
        }
        for (int i = 1; i < 13; i++) {
            monthList.add("" + i);
        }
        for (int i = 1; i < 32; i++) {
            dayList.add("" + i);
        }
        for (int i = 0; i < 24; i++) {
            hourList.add("" + i);
        }
        for (int i = 0; i < 60; i++) {
            MinuteList.add("" + i);
        }

        year_pv.setData(yearList);
        month_pv.setData(monthList);
        day_pv.setData(dayList);
        hour_pv.setData(hourList);
        Minute_pv.setData(MinuteList);

        // 设置初始显示值为当前时间

      //  Time time = new Time(); // or
        // Time t=new Time("GMT+8"); 加上Time Zone资料
       // time.setToNow(); // 取得系统时间

        timeStamp = new Timestamp(System.currentTimeMillis());

        year_pv.setSelected(timeStamp.getYear());
        month_pv.setSelected(timeStamp.getMonth());
        day_pv.setSelected(timeStamp.getDate() - 1);
        hour_pv.setSelected(timeStamp.getHours());
        Minute_pv.setSelected(timeStamp.getMinutes());


    }


}
