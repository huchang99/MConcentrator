package com.system.mconcentrator.mconcentrator;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.system.mconcentrator.mconcentrator.application.MyApplication;
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

public class DeviceSetupActivity extends SerialPortActivity implements View.OnClickListener {

    private final static String TAG = "DeviceSetupActivity";


    private MyApplication myApplication;

    //控件
    private FrameLayout menu_titleup;
    //  private FrameLayout menu_titleend;
    //表头控件
    private TextView Back_tv;
    // private TextView tv_connect;
    // private TextView tv_right;

    //设置日期时间
    private SelectTimePopupWindow TimePopupWindow;

    private Button readMeterAddr;
    private Button setMeterAddr;
    private Button PotocolSet;
    private Button TimeSet;
    private Button DataInit;
    private Button RestoreFactory;
    private TextView readAddrinfotv;
    private EditText setAddrInfo;

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

    @Override
    protected void onDataReceived(final byte[] buffer, final int size) {

        //String Receivedtxt = new String(buffer,0,size-1);

        String txt = Conversion.BytetohexString(buffer, size);
        LogHelper.d("onDataReceived+++txt", txt.toString());
        if (txt != null) {
            txtsb.append(txt);
        }
        // LogHelper.d("onDataReceived+++txtsb",txtsb.toString());
        LogHelper.d("onDataReceived+++txtsblength", Integer.toString(txtsb.length()));
        if (txtsb.length() == 832) {
            //int temp;
            Receivedtxt = txtsb.substring(594, 616);
            //txtsb.delete(0, txtsb.length());//删除所有的数据
            LogHelper.d("onDataReceived+++Receivedtxt", Receivedtxt);
            showtext = StringToStringArrayToASCII(Receivedtxt);
            savePotocol.edit().putString("SaveMeterAddr", showtext).commit();  //保存读出来的地址
            LogHelper.d("onDataReceived+++showtext", showtext);
            runOnUiThread(new Runnable() {
                public void run() {
                    readAddrinfotv.setText(showtext);

                }
            });
        }

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

        readMeterAddr = (Button) findViewById(R.id.readMeterAddr);
        setMeterAddr = (Button) findViewById(R.id.setMeterAddr);
        PotocolSet = (Button) findViewById(R.id.PotocolSet);
        TimeSet = (Button) findViewById(R.id.TimeSet);
        DataInit = (Button) findViewById(R.id.DataInit);
        RestoreFactory = (Button) findViewById(R.id.RestoreFactory);

        readAddrinfotv = (TextView) findViewById(R.id.readAddrinfotv);
        setAddrInfo = (EditText) findViewById(R.id.setAddrInfo);

    }

    private void initLinstener() {

        Back_tv.setOnClickListener(this);
        readMeterAddr.setOnClickListener(this);
        setMeterAddr.setOnClickListener(this);
        PotocolSet.setOnClickListener(this);
        TimeSet.setOnClickListener(this);
        DataInit.setOnClickListener(this);
        RestoreFactory.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        //Intent intent = new Intent();
        switch (v.getId()) {

            case R.id.readMeterAddr:
                txtsb.delete(0, txtsb.length());//删除所有的数据
                //发送读取地址的指令
                //String readDeviceinfoCom = protocol.SendDeviceInfoProtocolHead+protocol.ReadCommand;
                String readMeterAddr_CRC = Conversion.getCrc(ReadCommand + PageOne);
                LogHelper.d("readMeterAddr_Crc", readMeterAddr_CRC);
                String readDeviceinfoCom = SendDeviceInfoProtocolHead + ReadCommand + PageOne + readMeterAddr_CRC + DeviceInfoProtocolEnd;
                LogHelper.d("readDeviceinfoCom", readDeviceinfoCom);
                try {
                    mOutputStream.write(hexStr2Bytes(readDeviceinfoCom));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.setMeterAddr:
                txtsb.delete(0, txtsb.length());//删除所有的数据
                String setAddrInfotv = StringToHEXAsciiString(setAddrInfo.getText().toString()); //得到用户输入的地址值
                if (setAddrInfotv.length() < 11) {
                    Toast.makeText(getApplicationContext(), "请输入11位有效的数字", Toast.LENGTH_SHORT).show();
                    break;
                }
             //   savePotocol.edit().putString("SaveMeterAddr", setAddrInfotv).commit();  //保存读出来的地址

                String sendsetAddrInfotv1 = SetRemarksInfoCommand +
                        SetRemarksInfodefault1 + setAddrInfotv + SetRemarksInfodefault2 + SetRemarksInfodefault3;
                String sendsetAddrInfotv2 = StringAddOne(sendsetAddrInfotv1, 262 - sendsetAddrInfotv1.length()); //增加0的字符串， //需要校验Crcd的字符串
                LogHelper.d("+++++sendsetAddrInfotv2", sendsetAddrInfotv2);
                String sendsetAddrInfotv3 = TcpIpCommand + setAddrInfotv + SendDeviceInfoProtocolHead + sendsetAddrInfotv2 + Conversion.getCrc(sendsetAddrInfotv2) + DeviceInfoProtocolEnd;
                LogHelper.d("+++++sendsetAddrInfotv3", sendsetAddrInfotv3);
                //发送
                try {
                    mOutputStream.write(hexStr2Bytes(sendsetAddrInfotv3));
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
