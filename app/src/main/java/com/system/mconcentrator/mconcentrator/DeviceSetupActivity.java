package com.system.mconcentrator.mconcentrator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.system.mconcentrator.mconcentrator.application.MyApplication;
import com.system.mconcentrator.mconcentrator.selfdefineclass.customDialog;
import com.system.mconcentrator.mconcentrator.utils.Conversion;
import com.system.mconcentrator.mconcentrator.utils.LogHelper;
import com.system.mconcentrator.mconcentrator.view.SelectTimePopupWindow;

import java.io.IOException;

import static android.view.Window.FEATURE_NO_TITLE;
import static com.system.mconcentrator.mconcentrator.utils.Conversion.StringAddOne;
import static com.system.mconcentrator.mconcentrator.utils.Conversion.StringToHEXAsciiString;
import static com.system.mconcentrator.mconcentrator.utils.Conversion.StringToStringArrayToASCII;
import static com.system.mconcentrator.mconcentrator.utils.Conversion.hexStr2Bytes;
import static com.system.mconcentrator.mconcentrator.utils.protocol.DeviceInfoProtocolEnd;
import static com.system.mconcentrator.mconcentrator.utils.protocol.PageOne;
import static com.system.mconcentrator.mconcentrator.utils.protocol.ReadCommand;
import static com.system.mconcentrator.mconcentrator.utils.protocol.SendDeviceInfoProtocolHead;
import static com.system.mconcentrator.mconcentrator.utils.protocol.SetRemarksInfoCommand;
import static com.system.mconcentrator.mconcentrator.utils.protocol.SetRemarksInfodefault1;
import static com.system.mconcentrator.mconcentrator.utils.protocol.SetRemarksInfodefault2;
import static com.system.mconcentrator.mconcentrator.utils.protocol.SetRemarksInfodefault3;
import static com.system.mconcentrator.mconcentrator.utils.protocol.TcpIpCommand;

public class DeviceSetupActivity extends Activity implements View.OnClickListener {

    private final static String TAG = "DeviceSetupActivity";


    private MyApplication myApplication;

    //存储管理员帐号和密码
    private SharedPreferences adminsp;
    private SharedPreferences Meteradminsp;

    //控件
    private FrameLayout menu_titleup;
    //  private FrameLayout menu_titleend;
    //表头控件
    private TextView Back_tv;
    // private TextView tv_connect;
    // private TextView tv_right;

    //设置日期时间
    private SelectTimePopupWindow TimePopupWindow;

    private Button setTerminalPassWord;
    private Button setCopyMeterPassWord;
    private Button PotocolSet;
    private Button TimeSet;
    private Button DataInit;
    private Button RestoreFactory;
//    private TextView readAddrinfotv;
//    private EditText setAddrInfo;

    //数据处理
    String Receivedtxt;
    String showtext;
    StringBuffer txtsb;

    //协议选择
    private int selectedPotocolIndex = 0;
    private SharedPreferences savePotocol;//用来存储协议
    final String[] Potocol = new String[]{"188", "130"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(FEATURE_NO_TITLE);
        setContentView(R.layout.activity_devicesetup);

        initViews();
        initData();
        initLinstener();

    }


    private void initData() {
        //设置表头透明
        menu_titleup.getBackground().setAlpha(0);
        //显示左边的组件
        Back_tv.setVisibility(View.VISIBLE);
        Back_tv.setText("返回");

        txtsb = new StringBuffer();

        //
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

    }

    private void initViews() {
        //顶部条控件
        menu_titleup = (FrameLayout) findViewById(R.id.menu_titleup);
        Back_tv = (TextView) findViewById(R.id.Back_tv);

        setTerminalPassWord = (Button) findViewById(R.id.setTerminalPassWord);
        setCopyMeterPassWord = (Button) findViewById(R.id.setCopyMeterPassWord);
        PotocolSet = (Button) findViewById(R.id.PotocolSet);
        TimeSet = (Button) findViewById(R.id.TimeSet);
        DataInit = (Button) findViewById(R.id.DataInit);
        RestoreFactory = (Button) findViewById(R.id.RestoreFactory);



    }

    private void initLinstener() {

        Back_tv.setOnClickListener(this);
        setTerminalPassWord.setOnClickListener(this);
        setCopyMeterPassWord.setOnClickListener(this);
        PotocolSet.setOnClickListener(this);
        TimeSet.setOnClickListener(this);
        DataInit.setOnClickListener(this);
        RestoreFactory.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        //Intent intent = new Intent();
        switch (v.getId()) {

            case R.id.setTerminalPassWord: //修改终端登录密码
                ShowDialogsetTerminalPassWord();
                break;
            case R.id.setCopyMeterPassWord://修改抄表登录密码
                ShowDialogcopymeterPassWord();
                break;
            case R.id.PotocolSet:
                //先获取存储的当前协议
                int Potocolflag = 0;
                String  Potocolnow = savePotocol.getString("defalutsavePotocol",null);
                if("188".equals(Potocolnow))
                {
                    Potocolflag = 0;
                }
                if("130".equals(Potocolnow))
                {
                    Potocolflag = 1;
                }
                //创建对话框
                Dialog PotocolDialog = new AlertDialog.Builder(this).setTitle("协议选择").setSingleChoiceItems(Potocol, Potocolflag, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedPotocolIndex = which;
                        Toast.makeText(getApplicationContext(), Potocol[which], Toast.LENGTH_SHORT).show();
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //保存协议
                        if(selectedPotocolIndex == 0)
                        {
                            savePotocol.edit().putString("defalutsavePotocol", "188").commit();
                        }
                        if(selectedPotocolIndex == 1)
                        {
                            savePotocol.edit().putString("defalutsavePotocol", "130").commit();
                        }
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
                PotocolDialog.show();

                break;
            case R.id.TimeSet: //还需要修改
                View rootview = LayoutInflater.from(DeviceSetupActivity.this).inflate(R.layout.activity_devicesetup, null);
                TimePopupWindow = new SelectTimePopupWindow(DeviceSetupActivity.this, myClickLinstener);
                TimePopupWindow.showAtLocation(rootview, Gravity.CENTER, 0, 0);
                break;
            case R.id.DataInit:
                break;
            case R.id.RestoreFactory:
                break;
            case R.id.Back_tv:
                finish();
                break;

        }


    }

    public void ShowDialogsetTerminalPassWord(){

        final customDialog.Builder builder = new customDialog.Builder(this);
        builder.setTitle("修改终端维护登录密码");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String pwdstr = builder.getDialogcontent();
                if(pwdstr.length()<1){

                    Toast.makeText(getApplicationContext(),"请输入有效的密码",Toast.LENGTH_SHORT).show();
                }
                else{
                    adminsp = getSharedPreferences("adminsp", Context.MODE_PRIVATE);
                    adminsp.edit().putString("adminpsd", pwdstr).commit();
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(),"修改成功",Toast.LENGTH_SHORT).show();
                }

                //设置你的操作事项
            }
        });

        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
        builder.create().show();
    }

    public void ShowDialogcopymeterPassWord(){

        final customDialog.Builder builder = new customDialog.Builder(this);
        builder.setTitle("修改抄表管理登录密码");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String pwdstr = builder.getDialogcontent();
                if(pwdstr.length()<1){
                    Toast.makeText(getApplicationContext(),"请输入有效的密码",Toast.LENGTH_SHORT).show();
                }
                else{
                    Meteradminsp = getSharedPreferences("Meteradminsp", Context.MODE_PRIVATE);
                    Meteradminsp.edit().putString("Meteradminpsd",pwdstr).commit();
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(),"修改成功",Toast.LENGTH_SHORT).show();
                }

                //设置你的操作事项
            }
        });

        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }


    public View.OnClickListener myClickLinstener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.set_date_sure_tv:
                    TimePopupWindow.dismiss();
                    TimePopupWindow = null;
                    break;
            }

        }
    };
}
