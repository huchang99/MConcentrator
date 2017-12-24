package com.system.mconcentrator.mconcentrator;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.system.mconcentrator.mconcentrator.application.MyApplication;

import static android.content.Intent.ACTION_MEDIA_MOUNTED;
import static android.content.Intent.ACTION_MEDIA_UNMOUNTED;
import static android.content.Intent.ACTION_TIME_TICK;

public class Protocol130Activity extends Activity implements View.OnClickListener {

    private static final String TAG = "Protocol130Activity";
    private final static String ACTION_USB_STATE = "android.hardware.usb.action.USB_STATE";

    private FrameLayout menu_titleup;
    private FrameLayout menu_titleend;
    //表头控件
    private TextView Back_tv;
    private TextView tv_connect;
    private TextView tv_right;

    //表尾控件
    private TextView DiskRemain_tv;
    private TextView CommunicationMode_tv;
    private TextView uDisk_tv;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_protocol130);
        initViews();
        initData();
        initLinstener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_TIME_TICK);
        filter.addAction(ACTION_MEDIA_MOUNTED);
        filter.addAction(ACTION_MEDIA_UNMOUNTED);
        filter.addDataScheme("file");
        registerReceiver(myDetectReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myDetectReceiver);
    }

    private void initViews() {

        //顶部条控件
        menu_titleup = (FrameLayout) findViewById(R.id.menu_titleup);
        tv_right = (TextView) findViewById(R.id.tv_right);
        Back_tv = (TextView) findViewById(R.id.Back_tv);
        tv_connect = (TextView) findViewById(R.id.tv_connect);

        //尾部条控件
        menu_titleend = (FrameLayout) findViewById(R.id.menu_titleend);
        DiskRemain_tv = (TextView) findViewById(R.id.DiskRemain_tv);
        CommunicationMode_tv = (TextView) findViewById(R.id.CommunicationMode_tv);
        uDisk_tv = (TextView) findViewById(R.id.uDisk_tv);



    }
    private void initData() {

        //设置表头透明
        menu_titleup.getBackground().setAlpha(0);
        //显示右侧的组件
        tv_right.setVisibility(View.VISIBLE);
        String date = MyApplication.myApplication.getSystemTime();
        tv_right.setText(date);


        //显示中间的组件
        tv_connect.setVisibility(View.VISIBLE);
        tv_connect.setText("板卡连接状态");

        //显示左边的组件
        Back_tv.setVisibility(View.VISIBLE);
        Back_tv.setText("返回");

        //设置表尾透明
        menu_titleend.getBackground().setAlpha(0);

        //显示右侧的组件
        uDisk_tv.setVisibility(View.VISIBLE);
        uDisk_tv.setText("U盘未挂载");
        //显示左边的组件
        DiskRemain_tv.setVisibility(View.VISIBLE);
        String Diskvalue = MyApplication.myApplication.readSDCard();
        DiskRemain_tv.setText(Diskvalue+"G");

        //显示中间的组件
        CommunicationMode_tv.setVisibility(View.VISIBLE);
        CommunicationMode_tv.setText("GPRG通讯");
    }
    private void initLinstener() {
        Back_tv.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Back_tv:
                finish();
                break;
        }

    }


    private BroadcastReceiver myDetectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_TIME_TICK.equals(intent.getAction())) {
                //获取系统的时间
                String date = MyApplication.myApplication.getSystemTime();
                tv_right.setText(date);
            }
            else if(ACTION_MEDIA_MOUNTED.equals(intent.getAction()))
            {
                // Toast.makeText(MenuActivity.this,"ACTION_MEDIA_MOUNTED", Toast.LENGTH_SHORT).show();
                uDisk_tv.setText("U盘已挂载");
            }
            else if(ACTION_MEDIA_UNMOUNTED.equals(intent.getAction()))
            {
                // Toast.makeText(MenuActivity.this,"ACTION_MEDIA_UNMOUNTED", Toast.LENGTH_SHORT).show();
                uDisk_tv.setText("U盘未挂载");
            }
            else{}

        }
    };
}
