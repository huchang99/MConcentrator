package com.system.mconcentrator.mconcentrator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.system.mconcentrator.mconcentrator.utils.LogHelper;

public class LunchActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "LunchActivity";

    private Button loginBt;
    private SharedPreferences loginsp;//用来管理存储帐号和密码，记住密码状态
    private SharedPreferences savePotocol;
    private EditText usrnameedit;
    private EditText passwordedit;
    private CheckBox savedAccountCheckBox; //记住密码


    //帐号密码
    private String accountValue;
    private String passwordValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_lunch);
        initViews();
        initData();
        initLinstener();

        /**
         * 判断app是否第一次登录
         */
        loginsp = getSharedPreferences("sp_login", Context.MODE_PRIVATE); //用来存帐号密码
        Boolean user_first = loginsp.getBoolean("FIRST", true);
        if (user_first) {//第一次
            loginsp.edit().putBoolean("FIRST", false).commit();
            Toast.makeText(LunchActivity.this, "第一次", Toast.LENGTH_LONG).show();
            /*设置最初密码*/
            loginsp.edit().putString("user", "huchang").putString("password", "123").putString("newaccount", "").putString("newpassword", "").commit();
        } else {
            Toast.makeText(LunchActivity.this, "不是第一次", Toast.LENGTH_LONG).show();
        }
        /**
         * 判断是不是第一次设置协议
         */
        savePotocol = getSharedPreferences("savePotocol", MODE_PRIVATE);
        Boolean save_first = savePotocol.getBoolean("save_first", true);
        if (save_first) {//第一次
            savePotocol.edit().putBoolean("save_first", false).commit();
            Toast.makeText(getApplicationContext(), "第一次设置协议", Toast.LENGTH_LONG).show();
            /*设置最初协议为188*/
            savePotocol.edit().putString("defalutsavePotocol", "188").commit();
        } else {
            Toast.makeText(getApplicationContext(), "不是第一次设置协议", Toast.LENGTH_LONG).show();
        }

        /**
         *判断是否记住密码
         */
        if (loginsp.getBoolean("ISCHECK", true)) { //选中
            savedAccountCheckBox.setChecked(true);

            try {
                accountValue = loginsp.getString("newaccount", "");
                usrnameedit.setText(accountValue);
            } catch (Exception e) {
            }
            try {
                passwordValue = loginsp.getString("newpassword", "");
                passwordedit.setText(passwordValue);
            } catch (Exception e) {
            }
        }



        /**
         * 改变密码记住状态
         */
        savedAccountCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    LogHelper.d("checkbox+++", "记住帐号选中");
                    loginsp.edit().putBoolean("ISCHECK", true).commit();

                } else {
                    LogHelper.d("checkbox---", "记住帐号未选中");
                    loginsp.edit().putBoolean("ISCHECK", false).commit();
                }
            }
        });

    }

    private void initViews() {
        loginBt = (Button) findViewById(R.id.login_btn);
        usrnameedit = (EditText) findViewById(R.id.login_user_et);
        passwordedit = (EditText) findViewById(R.id.login_user_pwd_et);
        savedAccountCheckBox = (CheckBox) findViewById(R.id.cb_mima);
    }

    private void initData() {
    }

    private void initLinstener() {
        loginBt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.login_btn:
                String inputuser = usrnameedit.getText().toString();
                String inputpassword = passwordedit.getText().toString();

                //判断记住密码状态，记住密码则写入存储
                if (savedAccountCheckBox.isChecked()) {
                    loginsp.edit().putString("newaccount", inputuser).putString("newpassword", inputpassword).commit();
                }
                else{
                    loginsp.edit().putString("newaccount", "").putString("newpassword", "").commit();
                }

                //判断用户名和密码是否正确

                String user = loginsp.getString("user", null);
                String password = loginsp.getString("password", null);
                if (inputuser.equals(user) && inputpassword.equals(password)) {
                    Toast.makeText(LunchActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                     intent.setClass(getApplicationContext(), MenuActivity.class);
                    //intent.setClass(getApplicationContext(), TestActivity.class);
                    startActivity(intent);
                    intent = null;
                } else {
                    Toast.makeText(LunchActivity.this, "登录失败", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


}
