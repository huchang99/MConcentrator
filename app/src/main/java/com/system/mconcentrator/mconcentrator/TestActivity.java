package com.system.mconcentrator.mconcentrator;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.system.mconcentrator.mconcentrator.utils.Conversion;
import com.system.mconcentrator.mconcentrator.utils.LogHelper;

import java.io.IOException;
import java.util.Arrays;

public class TestActivity extends SerialPortActivity implements View.OnClickListener {

    //private static final String TAG = "MyApplication";

    private EditText editText;//输入发送的内容
    private Button sendbt;
    private TextView Distext;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        editText = (EditText) findViewById(R.id.sendedit_test);
        sendbt = (Button) findViewById(R.id.sendtextbt);
        Distext = (TextView) findViewById(R.id.receivedisplay);

        sendbt.setOnClickListener(this);

    }

    @Override
    protected void onDataReceived(final byte[] buffer, final int size) {
        runOnUiThread(new Runnable() {
            public void run() {
                String txt = new String(buffer,0,size-1);
                txt = Conversion.BytetohexString(buffer, size);
                LogHelper.d("+++txt",txt);
                //显示
                if (Distext != null) {
                   // Distext.append(new String(buffer, 0, size));
                    Distext.append(txt);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendtextbt:
                //String testchar = editText.getText().toString();
                String testchar = "4242424242424242425352525200406a45";
                //getSendBuf(testchar);
                //CharSequence t = editText.getText().toString();
//                char[] text = new char[t.length()];
//                for(int i = 0;i<t.length();i++)
//                {
//                    text[i] = t.charAt(i);
//                }
                try{
                    //mOutputStream.write(new String(text).getBytes());
                    mOutputStream.write(getSendBuf(testchar));
                    //mOutputStream.write('\n');
                }catch (IOException e){
                    e.printStackTrace();
                }
                break;

        }

    }

    /**
     * 直接发送字符对应的十六进制
     * @param data
     * @return
     */
    private byte[] getSendBuf(String data) {
        LogHelper.d("++++getSendBufStart", data);
       // sendIndex = 0;
       // byte[] sendBuf = Conversion.stringToBytes(getHexString(data));
       //  sendDataLen = sendBuf.length;
        // LogHelper.d("++++sendBuf", Arrays.toString(sendBuf));
        byte[] sendBuf =Conversion.hexStr2Bytes(data);
        LogHelper.d("++++sendBuf", Arrays.toString(sendBuf));
        return sendBuf;
    }

    // *********************************************************
    // 对数据的功能性解析方法
    // 获取输入框十六进制格 式
    private String getHexString(String s) {
        // String s = sendtext.getText().toString();
        LogHelper.d("++++getHexString", "getHexString");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (('0' <= c && c <= '9') || ('a' <= c && c <= 'f')
                    || ('A' <= c && c <= 'F')) {
                sb.append(c);
            }
        }
        if ((sb.length() % 2) != 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
