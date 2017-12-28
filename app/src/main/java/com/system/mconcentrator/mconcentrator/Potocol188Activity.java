package com.system.mconcentrator.mconcentrator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.system.mconcentrator.mconcentrator.application.MyApplication;
import com.system.mconcentrator.mconcentrator.utils.Conversion;
import com.system.mconcentrator.mconcentrator.utils.LogHelper;

import java.io.IOException;


import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import static android.content.Intent.ACTION_MEDIA_MOUNTED;
import static android.content.Intent.ACTION_MEDIA_UNMOUNTED;
import static android.content.Intent.ACTION_TIME_TICK;
import static com.system.mconcentrator.mconcentrator.utils.Conversion.StringToStringArrayToASCII;
import static com.system.mconcentrator.mconcentrator.utils.Conversion.hexStr2Bytes;
import static com.system.mconcentrator.mconcentrator.utils.protocol.DeviceInfoProtocolEnd;
import static com.system.mconcentrator.mconcentrator.utils.protocol.OpengateCommand;
import static com.system.mconcentrator.mconcentrator.utils.protocol.PageOne;
import static com.system.mconcentrator.mconcentrator.utils.protocol.ReadCommand;
import static com.system.mconcentrator.mconcentrator.utils.protocol.SendDeviceInfoProtocolHead;
import static com.system.mconcentrator.mconcentrator.utils.protocol.TcpIpCommand;

public class Potocol188Activity extends SerialPortActivity implements View.OnClickListener {

    private static final String TAG = "Potocol188Activity";
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

    //界面控制按钮
    private Button readConcentratorVesionBt; //读设备信息
    private TextView ConcentratorNumTv; //读集中器编号
    private TextView ConcentratorSwVersionTv; //集中器软件版本
    private TextView ConcentratorHWVersionTv; //集中器硬件版本
    private TextView readTcpIpTv;     //TCP/IP
    private CheckBox FisrttimeDayselect;
    private CheckBox TwotimeDayselect;
    private TextView FirsttimeDay;  //第一行时间day
    private TextView TwotimeDay;    //第二行时间day
    private TextView timeHour;      //小时选择
    private TextView timeMinute;    //分选择
    private Button copyMeterBt; //读抄表按钮
    private Button opengatebt;//开阀
    private Button offatebt;//关阀


    //数据处理
    StringBuffer txtsb;
    String ConcentratorNumStr;       //设备编号字符串
    String ConcentratorSwVersionStr; //设备软件版本字符串
    String ConcentratorHWVersionStr; //集中器硬件版本字符串
    String readTcpIpStr;             //TCP/IP字符串

    String TimerStr; //定时抄表时间总字符
    String FisrttimeDayselectStr;
    String TwotimeDayselectStr;
    String FirsttimeDayStr;          //第一行时间day字符串
    String TwotimeDayStr;            //第二行时间day字符串
    String timeHourStr;              //小时选择字符串
    String timeMinuteStr;            //分选择字符串

    //取出集中器的地址
  //  private SharedPreferences SpMeterAddr;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_potocol188);
        initViews();
        initData();
        initLinstener();
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

        readConcentratorVesionBt = (Button) findViewById(R.id.readConcentratorVesionBt);

        ConcentratorNumTv = (TextView) findViewById(R.id.ConcentratorNumTv); //读集中器编号
        ConcentratorSwVersionTv = (TextView) findViewById(R.id.ConcentratorSwVersionTv); //集中器软件版本
        ConcentratorHWVersionTv = (TextView) findViewById(R.id.ConcentratorHWVersionTv); //集中器硬件版本
        readTcpIpTv = (TextView) findViewById(R.id.readTcpIpTv);     //TCP/IP
        FisrttimeDayselect = (CheckBox) findViewById(R.id.FisrttimeDayselect);
        TwotimeDayselect = (CheckBox) findViewById(R.id.TwotimeDayselect);
        FirsttimeDay = (TextView) findViewById(R.id.FirsttimeDay);  //第一行时间day
        TwotimeDay = (TextView) findViewById(R.id.TwotimeDay);    //第二行时间day
        timeHour = (TextView) findViewById(R.id.timeHour);      //小时选择
        timeMinute = (TextView) findViewById(R.id.timeMinute);    //分选择

        copyMeterBt = (Button)findViewById(R.id.copyMeterBt); //抄表按钮
        opengatebt = (Button)findViewById(R.id.opengatebt);
        offatebt = (Button)findViewById(R.id.offatebt);

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
        DiskRemain_tv.setText(Diskvalue + "G");

        //显示中间的组件
        CommunicationMode_tv.setVisibility(View.VISIBLE);
        CommunicationMode_tv.setText("GPRG通讯");

        //初始化数据
        txtsb = new StringBuffer();

        //初始化shareperfence
      //  SpMeterAddr = getSharedPreferences("savePotocol", MODE_PRIVATE);
    }

    private void initLinstener() {
        Back_tv.setOnClickListener(this);
        readConcentratorVesionBt.setOnClickListener(this);
        copyMeterBt.setOnClickListener(this);
        opengatebt.setOnClickListener(this);
        offatebt.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.readConcentratorVesionBt:
                txtsb.delete(0, txtsb.length());//删除所有的数据
                String readDeviceinfo = TcpIpCommand + SendDeviceInfoProtocolHead + ReadCommand + PageOne + "406A" + DeviceInfoProtocolEnd;
                LogHelper.d("readDeviceinfo", readDeviceinfo);
                try {
                    mOutputStream.write(hexStr2Bytes(readDeviceinfo));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.copyMeterBt:
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(),CopyMeter188Activity.class);
                startActivity(intent);
                intent = null;
               // finish();
                break;

            case R.id.opengatebt:
                txtsb.delete(0, txtsb.length());//删除所有的数据
//                String MeterAddr = SpMeterAddr.getString("SaveMeterAddr",null);
//                LogHelper.d(TAG+"MeterAddr",MeterAddr);
//               String Sendopengatestr1 =OpengateCommand+
////                        String Sendopengatestr2 =TcpIpCommand
                break;

            case R.id.offatebt:
                txtsb.delete(0, txtsb.length());//删除所有的数据
                break;

            case R.id.Back_tv:
                finish();
                break;
        }
    }

    @Override
    protected void onDataReceived(byte[] buffer, int size) {

        String txt = Conversion.BytetohexString(buffer, size);
        LogHelper.d(TAG+"onDataReceived+++txt", txt.toString());
        if (txt != null) {
            txtsb.append(txt);
            LogHelper.d(TAG+"onDataReceived+++txtsb", txtsb.toString());
        }

        if (txtsb.length() == 832) {

            ConcentratorNumStr = txtsb.substring(594, 616);
            ConcentratorSwVersionStr = txtsb.substring(644, 666);
            ConcentratorHWVersionStr = txtsb.substring(694, 718);
            readTcpIpStr = txtsb.substring(60, 92);
            TimerStr = txtsb.substring(122, 154);
            FisrttimeDayselectStr = TimerStr.substring(6, 8);
            TwotimeDayselectStr = TimerStr.substring(14, 16);
            FirsttimeDayStr = TimerStr.substring(8, 12);
            TwotimeDayStr = TimerStr.substring(16, 20);
            timeHourStr = TimerStr.substring(20, 24);
            timeMinuteStr = TimerStr.substring(24, 28);

            LogHelper.d("ConcentratorNumStr   ", ConcentratorNumStr);
            LogHelper.d("ConcentratorSwVersionStr   ", ConcentratorSwVersionStr);
            LogHelper.d("ConcentratorHWVersionStr   ", ConcentratorHWVersionStr);
            LogHelper.d("readTcpIpStr   ", readTcpIpStr);
            LogHelper.d("TimerStr   ", TimerStr);
            LogHelper.d("FisrttimeDayselectStr   ", FisrttimeDayselectStr);
            LogHelper.d("TwotimeDayselectStr   ", TwotimeDayselectStr);
            LogHelper.d("FirsttimeDayStr   ", FirsttimeDayStr);
            LogHelper.d("TwotimeDayStr   ", TwotimeDayStr);
            LogHelper.d("timeHourStr   ", timeHourStr);
            LogHelper.d("timeMinuteStr   ", timeMinuteStr);

            ConcentratorNumStr = StringToStringArrayToASCII(ConcentratorNumStr);
            ConcentratorSwVersionStr = StringToStringArrayToASCII(ConcentratorSwVersionStr);
            ConcentratorHWVersionStr = StringToStringArrayToASCII(ConcentratorHWVersionStr);
            readTcpIpStr = StringToStringArrayToASCII(readTcpIpStr);
            FirsttimeDayStr = StringToStringArrayToASCII(FirsttimeDayStr);
            TwotimeDayStr = StringToStringArrayToASCII(TwotimeDayStr);
            timeHourStr = StringToStringArrayToASCII(timeHourStr);
            timeMinuteStr = StringToStringArrayToASCII(timeMinuteStr);

            LogHelper.d("ConcentratorNumStr++   ", ConcentratorNumStr);
            LogHelper.d("ConcentratorSwVersionStr++   ", ConcentratorSwVersionStr);
            LogHelper.d("ConcentratorHWVersionStr++   ", ConcentratorHWVersionStr);
            LogHelper.d("readTcpIpStr++   ", readTcpIpStr);
            LogHelper.d("FirsttimeDayStr++   ", FirsttimeDayStr);
            LogHelper.d("TwotimeDayStr++   ", TwotimeDayStr);
            LogHelper.d("timeHourStr++   ", timeHourStr);
            LogHelper.d("timeMinuteStr++   ", timeMinuteStr);


            //saveTimecopy.edit().putString("SaveMeterAddr", showtext).commit();  //保存读出来的地址
            runOnUiThread(new Runnable() {
                public void run() {
                    ConcentratorNumTv.setText(ConcentratorNumStr);
                    ConcentratorSwVersionTv.setText(ConcentratorSwVersionStr);
                    ConcentratorHWVersionTv.setText(ConcentratorHWVersionStr);
                    readTcpIpTv.setText(readTcpIpStr);
//                    FisrttimeDayselect.setText();
//                    TwotimeDayselect.setText();
                    FirsttimeDay.setText(FirsttimeDayStr);
                    TwotimeDay.setText(TwotimeDayStr);
                    timeHour.setText(timeHourStr);
                    timeMinute.setText(timeMinuteStr);
                    if("59".equals(FisrttimeDayselectStr))
                    {
                        FisrttimeDayselect.setChecked(true);
                    }
                    if("59".equals(TwotimeDayselectStr))
                    {
                        TwotimeDayselect.setChecked(true);
                    }

                }
            });
        }

    }


    private BroadcastReceiver myDetectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_TIME_TICK.equals(intent.getAction())) {
                //获取系统的时间
                String date = MyApplication.myApplication.getSystemTime();
                tv_right.setText(date);
            } else if (ACTION_MEDIA_MOUNTED.equals(intent.getAction())) {
                // Toast.makeText(MenuActivity.this,"ACTION_MEDIA_MOUNTED", Toast.LENGTH_SHORT).show();
                uDisk_tv.setText("U盘已挂载");
            } else if (ACTION_MEDIA_UNMOUNTED.equals(intent.getAction())) {
                // Toast.makeText(MenuActivity.this,"ACTION_MEDIA_UNMOUNTED", Toast.LENGTH_SHORT).show();
                uDisk_tv.setText("U盘未挂载");
            } else {
            }

        }
    };
}
