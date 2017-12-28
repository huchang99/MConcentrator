package com.system.mconcentrator.mconcentrator.application;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.StatFs;

import com.system.mconcentrator.mconcentrator.Meter188ManagementActivity;
import com.system.mconcentrator.mconcentrator.serialport.SerialPort;
import com.system.mconcentrator.mconcentrator.utils.ActivityManager;
import com.system.mconcentrator.mconcentrator.utils.LogHelper;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;

/**
 * Created by huchang on 2017/9/29.
 */

public class MyApplication extends LitePalApplication {

    private static final String TAG = "MyApplication";

    public static MyApplication myApplication;

    //串口
    private SerialPort mSerialPort = null;
    private static int baudrate = 19200; //波特率
    // private static String path = "/dev/ttyS4"; //路径
    private static String path = "/dev/ttyS2"; //路径

    //存储路径
    SharedPreferences initsavePotocol;



    @Override
    public void onCreate() {

        myApplication = this;
        super.onCreate();
        //创建数据库
        LitePal.getDatabase();

       // verifyStoragePermissions(Meter188ManagementActivity.this);

        //readSystem();
        //readSDCard();

    }

    public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
        if (mSerialPort == null) {
//			/* Read serial port parameters */
//            SharedPreferences sp = getSharedPreferences("android.serialport.sample_preferences", MODE_PRIVATE);
//            String path = sp.getString("DEVICE", "");
//            int baudrate = Integer.decode(sp.getString("BAUDRATE", "-1"));
//
//			/* Check parameters */
//            if ( (path.length() == 0) || (baudrate == -1)) {
//                throw new InvalidParameterException();
//            }

			/* Open the serial port */
            mSerialPort = new SerialPort(new File(path), baudrate);
        }
        return mSerialPort;
    }

    public void closeSerialPort() {
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }

    /**
     * @param activity
     * @des 添加一个activity到集合
     */
    public void addActivity(Activity activity) {
        ActivityManager.getActivityManager().pushActivity(activity);
    }

    /**
     * @des 删除集合中的所有的activity
     */
    public void removeAllActivity() {
        ActivityManager.getActivityManager().popAllActivity();
    }

    public String getSystemTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }


    void readSystem() {
        File root = Environment.getRootDirectory();
        StatFs sf = new StatFs(root.getPath());
        long blockSize = sf.getBlockSize();
        long blockCount = sf.getBlockCount();
        long availCount = sf.getAvailableBlocks();
        LogHelper.d("", "block大小:" + blockSize + ",block数目:" + blockCount + ",总大小:" + blockSize * blockCount / 1024 + "KB");
        LogHelper.d("", "可用的block数目：:" + availCount + ",可用大小:" + availCount * blockSize / 1024 + "KB");
    }

    public String readSDCard() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            long blockSize = sf.getBlockSize();
            long blockCount = sf.getBlockCount();
            long availCount = sf.getAvailableBlocks();
            //LogHelper.d("", "block大小:"+ blockSize+",block数目:"+ blockCount+",总大小:"+blockSize*blockCount/1024+"KB");
            //LogHelper.d("", "可用的block数目：:"+ availCount+",剩余空间:"+ availCount*blockSize/1024+"KB");
            String value = String.valueOf(availCount * blockSize / 1024 / 1024 / 1024);
            return value;
        }
        return "";
    }
}
