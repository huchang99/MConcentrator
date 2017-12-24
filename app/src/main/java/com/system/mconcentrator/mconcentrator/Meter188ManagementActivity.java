package com.system.mconcentrator.mconcentrator;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.system.mconcentrator.mconcentrator.application.MyApplication;
import com.system.mconcentrator.mconcentrator.excel.ExtraAsyncTask;
import com.system.mconcentrator.mconcentrator.excel.MeterData;
import com.system.mconcentrator.mconcentrator.excel.database;
import com.system.mconcentrator.mconcentrator.utils.Conversion;
import com.system.mconcentrator.mconcentrator.utils.LogHelper;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.IOException;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import static android.content.Intent.ACTION_MEDIA_MOUNTED;
import static android.content.Intent.ACTION_MEDIA_UNMOUNTED;
import static android.content.Intent.ACTION_TIME_TICK;
import static com.system.mconcentrator.mconcentrator.R.id.FirsttimeDay;
import static com.system.mconcentrator.mconcentrator.utils.Conversion.StringAddOne;
import static com.system.mconcentrator.mconcentrator.utils.Conversion.StringToHEXAsciiString;
import static com.system.mconcentrator.mconcentrator.utils.Conversion.StringToStringArrayToASCII;
import static com.system.mconcentrator.mconcentrator.utils.Conversion.getCrc;
import static com.system.mconcentrator.mconcentrator.utils.Conversion.hexStr2Bytes;
import static com.system.mconcentrator.mconcentrator.utils.protocol.DeviceInfoProtocolEnd;
import static com.system.mconcentrator.mconcentrator.utils.protocol.PageOne;
import static com.system.mconcentrator.mconcentrator.utils.protocol.ReadCommand;
import static com.system.mconcentrator.mconcentrator.utils.protocol.SendDeviceInfoProtocolHead;
import static com.system.mconcentrator.mconcentrator.utils.protocol.SetModemCommand;
import static com.system.mconcentrator.mconcentrator.utils.protocol.SetRemarksInfoCommand;
import static com.system.mconcentrator.mconcentrator.utils.protocol.SetRemarksInfodefault1;
import static com.system.mconcentrator.mconcentrator.utils.protocol.SetRemarksInfodefault2;
import static com.system.mconcentrator.mconcentrator.utils.protocol.SetRemarksInfodefault3;
import static com.system.mconcentrator.mconcentrator.utils.protocol.TcpIpCommand;
import static com.system.mconcentrator.mconcentrator.utils.protocol.TimingCollection;

public class Meter188ManagementActivity extends SerialPortActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "Meter188ManagementActivity";

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };

    private MyApplication myApplication;

    //控件
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

    //按钮
    private Button readMeterAddr;
    private Button setMeterAddr;
    private TextView readAddrinfotv;
    private EditText setAddrInfo;

    //定时设置抄表按钮
    private Button TimeCopyMeter;  //定时按钮
    private EditText FirsttimeDaytv; //第一个选项
    private EditText TwotimeDaytv;   //第二个选项
    private EditText timeHourtv;     //小时选择
    private EditText timeMinutetv;   //分选择
    private CheckBox FisrttimeDayselect;
    private CheckBox TwotimeDayselect;

    //设置Modem参数
    private Button SetModemInfoBt;
    private EditText SetModemInfoTv1;
    private EditText SetModemInfoTv2;
    private EditText SetModemInfoTv3;
    private EditText SetModemInfoTv4;
    private EditText SetModemInfoTv5;

    //定时选择标志
    private String TimeFirstFlag;  //第一个定时选择标志
    private String TimeTwoFlag;    //第二个定时选择标志
    private String TimeFirstpotocol;
    private String TimeTwopotocol;

    //定时设置时间输入的内容
    private String TimeFisrtday;
    private String TimeTwoday;
    private String TimeHour;
    private String TimeMinute;

    //设置保存定时抄表时间的数据
    private SharedPreferences saveTimecopy;//用来存储协议和数据

    //文件导入
    private Button ExcelFileToDbBt;
    //文件导出
    private Button DbToExcelFileBt;


    //数据处理
    String Receivedtxt;
    String showtext;
    StringBuffer txtsb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_meter188_management);

        initViews();
        initData();
        initLinstener();

        verifyStoragePermissions(Meter188ManagementActivity.this);
    }

    @Override
    protected void onDataReceived(byte[] buffer, int size) {
        String txt = Conversion.BytetohexString(buffer, size);
        LogHelper.d(TAG+"TonDataReceived+++txt", txt.toString());
        if (txt != null) {
            txtsb.append(txt);
        }
        // LogHelper.d("onDataReceived+++txtsb",txtsb.toString());
        LogHelper.d(TAG+"onDataReceived+++txtsblength", Integer.toString(txtsb.length()));
        if (txtsb.length() == 832) {
            //int temp;
            Receivedtxt = txtsb.substring(594, 616);
            //txtsb.delete(0, txtsb.length());//删除所有的数据
            LogHelper.d(TAG+"onDataReceived+++Receivedtxt", Receivedtxt);
            saveTimecopy.edit().putString("SaveMeterAddr", Receivedtxt).commit();  //保存读出来的地址
            showtext = StringToStringArrayToASCII(Receivedtxt);
            LogHelper.d(TAG+"onDataReceived+++showtext", showtext);
            //saveTimecopy.edit().putString("SaveMeterAddr", showtext).commit();  //保存读出来的地址
            runOnUiThread(new Runnable() {
                public void run() {
                    readAddrinfotv.setText(showtext);

                }
            });
        }

    }

    private void initViews() {
        //顶部条控件
        menu_titleup = (FrameLayout) findViewById(R.id.menu_titleup);
        Back_tv = (TextView) findViewById(R.id.Back_tv);
        tv_right = (TextView) findViewById(R.id.tv_right);
        tv_connect = (TextView) findViewById(R.id.tv_connect);

        //尾部条控件
        menu_titleend = (FrameLayout) findViewById(R.id.menu_titleend);
        DiskRemain_tv = (TextView) findViewById(R.id.DiskRemain_tv);
        CommunicationMode_tv = (TextView) findViewById(R.id.CommunicationMode_tv);
        uDisk_tv = (TextView) findViewById(R.id.uDisk_tv);

        readMeterAddr = (Button) findViewById(R.id.readMeterAddr);
        setMeterAddr = (Button) findViewById(R.id.setMeterAddr);
        readAddrinfotv = (TextView) findViewById(R.id.readAddrinfotv);
        setAddrInfo = (EditText) findViewById(R.id.setAddrInfo);

        //定时抄表时间设置
        TimeCopyMeter = (Button) findViewById(R.id.TimeCopyMeter);
        FirsttimeDaytv = (EditText) findViewById(FirsttimeDay);
        TwotimeDaytv = (EditText) findViewById(R.id.TwotimeDay);
        timeHourtv = (EditText) findViewById(R.id.timeHour);
        timeMinutetv = (EditText) findViewById(R.id.timeMinute);
        FisrttimeDayselect = (CheckBox) findViewById(R.id.FisrttimeDayselect);
        FisrttimeDayselect.setChecked(false);
        TwotimeDayselect = (CheckBox) findViewById(R.id.TwotimeDayselect);
        TwotimeDayselect.setChecked(false);

        //设置MODEM参数
        SetModemInfoBt = (Button) findViewById(R.id.SetModemInfoBt);
        SetModemInfoTv1 = (EditText) findViewById(R.id.SetModemInfoTv1);
        SetModemInfoTv2 = (EditText) findViewById(R.id.SetModemInfoTv2);
        SetModemInfoTv3 = (EditText) findViewById(R.id.SetModemInfoTv3);
        SetModemInfoTv4 = (EditText) findViewById(R.id.SetModemInfoTv4);
        SetModemInfoTv5 = (EditText) findViewById(R.id.SetModemInfoTv5);

        //文件导入
        ExcelFileToDbBt = (Button)findViewById(R.id.ExcelFileToDbBt);
        //文件导出
        DbToExcelFileBt = (Button)findViewById(R.id.DbToExcelFileBt);
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
        DiskRemain_tv.setText("剩余容量"+Diskvalue+"G");

        //显示中间的组件
        CommunicationMode_tv.setVisibility(View.VISIBLE);
        CommunicationMode_tv.setText("GPRG通讯");



        txtsb = new StringBuffer();

        //初始化界面的数据
        saveTimecopy = getSharedPreferences("savePotocol", MODE_PRIVATE);
        Boolean saveTimecopyFisrt = saveTimecopy.getBoolean("saveTimecopyFisrt", true);//判断是不是第一次进入
        if (saveTimecopyFisrt) {
            saveTimecopy.edit().putBoolean("saveTimecopyFisrt", false).commit();
            Toast.makeText(getApplicationContext(), "第一次进入", Toast.LENGTH_LONG).show();
            /*设置最初的时间*/
            saveTimecopy.edit().putString("TimeFisrtday", "").commit();
            saveTimecopy.edit().putString("TimeTwoday", "").commit();
            saveTimecopy.edit().putString("TimeHour", "").commit();
            saveTimecopy.edit().putString("TimeMinute", "").commit();

        } else {
            //不是第一次进入
            TimeFisrtday = saveTimecopy.getString("TimeFisrtday", null);
            TimeTwoday = saveTimecopy.getString("TimeTwoday", null);
            TimeHour = saveTimecopy.getString("TimeHour", null);
            TimeMinute = saveTimecopy.getString("TimeMinute", null);

            FirsttimeDaytv.setText(TimeFisrtday);
            TwotimeDaytv.setText(TimeTwoday);
            timeHourtv.setText(TimeHour);
            timeMinutetv.setText(TimeMinute);

        }
        TimeFirstpotocol = "5054314E";//PT1N
        TimeTwopotocol = "324E";//2N

    }

    private void initLinstener() {
        Back_tv.setOnClickListener(this);

        readMeterAddr.setOnClickListener(this);
        setMeterAddr.setOnClickListener(this);
        TimeCopyMeter.setOnClickListener(this);
        SetModemInfoBt.setOnClickListener(this);

        FisrttimeDayselect.setOnCheckedChangeListener(this);
        TwotimeDayselect.setOnCheckedChangeListener(this);
        ExcelFileToDbBt.setOnClickListener(this);
        DbToExcelFileBt.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

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
            case R.id.TimeCopyMeter: //按键
                if (checkTimeinput()) {
                    //恢复上次输入的数据
                    TimeFisrtday = FirsttimeDaytv.getText().toString();
                    TimeTwoday = TwotimeDaytv.getText().toString();
                    TimeHour = timeHourtv.getText().toString();
                    TimeMinute = timeMinutetv.getText().toString();
                    //将新的数据重新输入
                    saveTimecopy.edit().putString("TimeFisrtday", TimeFisrtday).commit();
                    saveTimecopy.edit().putString("TimeTwoday", TimeTwoday).commit();
                    saveTimecopy.edit().putString("TimeHour", TimeHour).commit();
                    saveTimecopy.edit().putString("TimeMinute", TimeMinute).commit();

                    //转换成要输入的格式
                    String TimeFisrtdaytemp = StringToHEXAsciiString(TimeFisrtday);
                    String TimeTwodaytemp = StringToHEXAsciiString(TimeTwoday);
                    String TimeHourtemp = StringToHEXAsciiString(TimeHour);
                    String TimeMinutetemp = StringToHEXAsciiString(TimeMinute);
                    LogHelper.d("TimeFisrtdaytemp", TimeFisrtdaytemp);
                    LogHelper.d("TimeTwodaytemp", TimeTwodaytemp);
                    LogHelper.d("TimeHourtemp", TimeHourtemp);
                    LogHelper.d("TimeMinutetemp", TimeMinutetemp);

                    //设置输入的命令
                    String TimeString1 =
                            TimingCollection + TimeFirstpotocol + TimeFisrtdaytemp + TimeTwopotocol +
                                    TimeTwodaytemp + TimeHourtemp + TimeMinutetemp + "3030";
                    LogHelper.d("TimeString1", TimeString1);

                    String TimeString = TcpIpCommand + SendDeviceInfoProtocolHead +
                            TimingCollection + TimeFirstpotocol + TimeFisrtdaytemp + TimeTwopotocol +
                            TimeTwodaytemp + TimeHourtemp + TimeMinutetemp + "3030" + getCrc(TimeString1) + DeviceInfoProtocolEnd;
                    LogHelper.d("TimeString", TimeString);
                    //发送
                    try {
                        mOutputStream.write(hexStr2Bytes(TimeString));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.SetModemInfoBt:
                //判断用户的输入值是否正确
                String Meteraddr = saveTimecopy.getString("SaveMeterAddr", null);
                if ("".equals(Meteraddr)) {
                    Toast.makeText(getApplicationContext(), "请先读入集中器的地址", Toast.LENGTH_SHORT).show();
                    break;
                }
                Boolean ModemFlag = checkModerminput();
                if (ModemFlag == true) {
                    //String Meteraddr = saveTimecopy.getString("SaveMeterAddr",null);
                    String SetModemInfotemp = StringToHEXAsciiString(SetModemInfoTv1.getText().toString()) + "2E" + StringToHEXAsciiString(SetModemInfoTv2.getText().toString()) +
                            "2E" + StringToHEXAsciiString(SetModemInfoTv3.getText().toString()) + "2E" + StringToHEXAsciiString(SetModemInfoTv4.getText().toString()) + "3A" +
                            StringToHEXAsciiString(SetModemInfoTv5.getText().toString());
                    LogHelper.d("SetModemInfotemp", SetModemInfotemp);
                    //构建Moderm命令
                    String SetModemInfoCmd1 = SetModemCommand + "12" + SetModemInfotemp + SetRemarksInfodefault2;
                    LogHelper.d("SetModemInfoCmd1", SetModemInfoCmd1);
                    String SetModemInfoCmd2 = StringAddOne(SetModemInfoCmd1, 70 - SetModemInfoCmd1.length()); //命令不足补0
                    String SetModemInfoCmd3 = TcpIpCommand + Meteraddr + SendDeviceInfoProtocolHead + SetModemInfoCmd2 + Conversion.getCrc(SetModemInfoCmd2) + DeviceInfoProtocolEnd;
                    LogHelper.d("+++++SetModemInfoCmd3", SetModemInfoCmd3);
                    //发送
                    try {
                        mOutputStream.write(hexStr2Bytes(SetModemInfoCmd3));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //得到用户

                break;
            case R.id.ExcelFileToDbBt:
                LitePal.deleteDatabase("MeterStore.db");
                //DataSupport.deleteAll(MeterData.class);
                LitePal.getDatabase();
                ExtraAsyncTask excelTask = new ExtraAsyncTask();
                excelTask.execute();
                break;
            case R.id.DbToExcelFileBt:
                LogHelper.d(TAG+"DbToExcelFileBt++","DbToExcelFileBt");
                String filePath = "/storage/usbhost/8_4/Meter1.xls";
                String filename = "ExtraMeter.xls";

                database dbtoexcel = new database();
                dbtoexcel.DbToExcel(filePath);
               // File file = null;
//                try {
//                    file = new File(filePath);
//                    if (!file.exists()) {
//                        LogHelper.d("filePath++++","filePath");
//                        file.mkdir();
//                    }
//                } catch (Exception e) {
//
//                }
//
//                try {
//                   // File file = new File("/storage/usbhost/8_4/ExtraMeter.xls");
//                    File file1 = new File(filePath+filename);
//                    if(!file1.exists())
//                    {
//                        file1.createNewFile();
//                    }
//                    WritableWorkbook Meterwwb = null;
//
//                    Meterwwb = Workbook.createWorkbook(file1);
//
//                    WritableSheet Metersheet = Meterwwb.createSheet("sheet1", 0);
//                    database dbtoexcel = new database();
//                    dbtoexcel.DbToExcel(file,Meterwwb,Metersheet);
//
//                    }catch (IOException e) {
//                        e.printStackTrace();
//                    }


                //创建工作表

                break;
            case R.id.Back_tv:
                finish();
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Toast.makeText(Meter188ManagementActivity.this, "onRequestPermissionsResult被调用！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()) {
            case R.id.FisrttimeDayselect:
                if (isChecked) {
                    TimeFirstFlag = "yes";
                    TimeFirstpotocol = "50543159";//PT1Y
                    LogHelper.d("选中TimeFirstFlag", TimeFirstFlag);
                } else {
                    TimeFirstFlag = "no";
                    TimeFirstpotocol = "5054314E";//PT1N
                    LogHelper.d("未选中TimeFirstFlag", TimeFirstFlag);
                }
                break;
            case R.id.TwotimeDayselect:
                if (isChecked) {
                    TimeTwoFlag = "yes";
                    TimeTwopotocol = "3259"; //2Y
                    LogHelper.d("选中TimeTwoFlag", TimeTwoFlag);

                } else {
                    TimeTwoFlag = "no";
                    TimeTwopotocol = "324E";//2N
                    LogHelper.d("未选中TimeTwoFlag", TimeTwoFlag);
                }
                break;
        }
    }

    //检测输入是否符合标准，
    private boolean checkTimeinput() {
        if (TimeFirstFlag == "yes") {
            if ("".equals(FirsttimeDaytv.getText().toString().trim()) || "".equals(timeHourtv.getText().toString().trim())
                    || "".equals(timeMinutetv.getText().toString().trim())) {
                Toast.makeText(getApplicationContext(), "请输入有效的时间", Toast.LENGTH_SHORT).show();
                return false;
            } else
                return true;
        }
        if (TimeTwoFlag == "yes") {
            if ("".equals(TwotimeDaytv.getText().toString().trim()) || "".equals(timeHourtv.getText().toString().trim())
                    || "".equals(timeMinutetv.getText().toString().trim())) {
                Toast.makeText(getApplicationContext(), "请输入有效的时间", Toast.LENGTH_SHORT).show();
                return false;
            } else
                return true;
        }
        Toast.makeText(getApplicationContext(), "请选择需要设置的时间", Toast.LENGTH_SHORT).show();
        return false;
    }

    //检测输入的Moderm参数是不是标准的
    private boolean checkModerminput() {
        if ("".equals(SetModemInfoTv1.getText().toString().trim()) || "".equals(SetModemInfoTv2.getText().toString().trim()) ||
                "".equals(SetModemInfoTv3.getText().toString().trim()) || "".equals(SetModemInfoTv4.getText().toString().trim())
                || "".equals(SetModemInfoTv5.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), "请输入有效的IP和端口", Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
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

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to
     * grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

}
