package com.system.mconcentrator.mconcentrator;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.system.mconcentrator.mconcentrator.application.MyApplication;

public class Meter130ManagementActivity extends Activity implements View.OnClickListener {

    private MyApplication myApplication;

    //控件
    private FrameLayout menu_titleup;
    // private FrameLayout menu_titleend;
    //表头控件
    private TextView Back_tv;
    // private TextView tv_connect;
    // private TextView tv_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_meter130_management);

        initViews();
        initData();
        initLinstener();
    }

    private void initViews() {
        //顶部条控件
        menu_titleup = (FrameLayout) findViewById(R.id.menu_titleup);
        Back_tv = (TextView) findViewById(R.id.Back_tv);

    }

    private void initData() {

        //设置表头透明
        menu_titleup.getBackground().setAlpha(0);
        //显示左边的组件
        Back_tv.setVisibility(View.VISIBLE);
        Back_tv.setText("返回");

    }

    private void initLinstener() {
        Back_tv.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.Back_tv:
                finish();
                break;
        }
    }
}
