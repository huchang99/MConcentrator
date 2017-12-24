package com.system.mconcentrator.mconcentrator;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.system.mconcentrator.mconcentrator.application.MyApplication;
import com.system.mconcentrator.mconcentrator.serialport.SerialPort;
import com.system.mconcentrator.mconcentrator.utils.LogHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

public abstract class SerialPortActivity extends Activity {

    private static final String TAG = "SerialPortActivity";

    protected MyApplication mApplication;
    protected SerialPort mSerialPort;
    protected OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = (MyApplication)getApplication(); //获取MyApplication
        try{
            mSerialPort = mApplication.getSerialPort(); //打开串口 串口的初始化
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();

            //creat a receiving thread
            mReadThread = new ReadThread();
            mReadThread.start();

        }catch (SecurityException e){
            Toast.makeText(this,"no permission",Toast.LENGTH_SHORT).show();
        }catch (IOException e)
        {
            Toast.makeText(this,"The serial port can not be opened for an unknown reason.",Toast.LENGTH_SHORT).show();
        }
        catch (InvalidParameterException e)
        {
            Toast.makeText(this,"Please configure your serial port first",Toast.LENGTH_SHORT).show();
        }
    }

    private class ReadThread extends Thread {

       // byte[] buffer = new byte[64];
        int i = 0;
        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                LogHelper.d("isInterrupted is ",Integer.toString(i++));
                int size;
                try {
                    byte[] buffer = new byte[32];
                    if (mInputStream == null) return;
                    size = mInputStream.read(buffer);
                    if (size > 0) {
                        LogHelper.d("size is ",Integer.toString(size));
                        onDataReceived(buffer, size);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

            }
        }
    }

    //定义一个接收函数的抽象方法，用于每个activity
    protected abstract void onDataReceived(final byte[] buffer, final int size);


    @Override
    protected void onStart() {
        super.onStart();
        LogHelper.d(TAG, "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mApplication = (MyApplication)getApplication(); //获取MyApplication
        try{
            mSerialPort = mApplication.getSerialPort(); //打开串口 串口的初始化
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();

            //creat a receiving thread
            mReadThread = new ReadThread();
            mReadThread.start();

        }catch (SecurityException e){
            Toast.makeText(this,"no permission",Toast.LENGTH_SHORT).show();
        }catch (IOException e)
        {
            Toast.makeText(this,"The serial port can not be opened for an unknown reason.",Toast.LENGTH_SHORT).show();
        }
        catch (InvalidParameterException e)
        {
            Toast.makeText(this,"Please configure your serial port first",Toast.LENGTH_SHORT).show();
        }
        LogHelper.d(TAG, "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogHelper.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        if(mReadThread!=null)
            mReadThread.interrupt();
        mApplication.closeSerialPort();
        mSerialPort = null;
        super.onPause();
        LogHelper.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogHelper.d(TAG, "onStop");
    }


    @Override
    protected void onDestroy() {
        LogHelper.d(TAG, "onDestroy");
//        if(mReadThread!=null)
//            mReadThread.interrupt();
//        mApplication.closeSerialPort();
//        mSerialPort = null;
        super.onDestroy();

    }
}
