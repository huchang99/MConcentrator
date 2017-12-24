package com.system.mconcentrator.mconcentrator;

import android.widget.Toast;

import com.system.mconcentrator.mconcentrator.application.MyApplication;
import com.system.mconcentrator.mconcentrator.serialport.SerialPort;
import com.system.mconcentrator.mconcentrator.utils.LogHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

/**
 * Created by huchang on 2017/12/14.
 */

public class SerialPortSingle {

    static SerialPortSingle instance = null;

    public MyApplication mApplication;
    public SerialPort mSerialPort;
    public OutputStream mOutputStream;
    public InputStream mInputStream;


    private void SerialPortSingle() {
    }

    public static SerialPortSingle getInstance() {
        if (instance == null) {
            synchronized (SerialPortSingle.class) {
                instance = new SerialPortSingle();
            }

        }
        return instance;
    }
    /*
    public void initSerialPort() {

       // mApplication = (MyApplication)getApplication(); //获取MyApplication
        try{
            mSerialPort = mApplication.getSerialPort(); //打开串口 串口的初始化
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();

            //creat a receiving thread
           // mReadThread = new SerialPortActivity.ReadThread();
           // mReadThread.start();

        }catch (SecurityException e){
           //Toast.makeText(this,"no permission",Toast.LENGTH_SHORT).show();
        }catch (IOException e)
        {
            //Toast.makeText(this,"The serial port can not be opened for an unknown reason.",Toast.LENGTH_SHORT).show();
        }
        catch (InvalidParameterException e)
        {
           // Toast.makeText(this,"Please configure your serial port first",Toast.LENGTH_SHORT).show();
        }

    }

    public class ReadThread extends Thread {

        // byte[] buffer = new byte[64];
        int i = 0;

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                LogHelper.d("isInterrupted is ", Integer.toString(i++));
                int size;
                try {
                    byte[] buffer = new byte[32];
                    if (mInputStream == null) return;
                    size = mInputStream.read(buffer);
                    if (size > 0) {
                        LogHelper.d("size is ", Integer.toString(size));
                       // onDataReceived(buffer, size);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

            }
        }
    }*/


}
