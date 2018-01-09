package com.system.mconcentrator.mconcentrator;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kelin.scrollablepanel.library.ScrollablePanel;
import com.system.mconcentrator.mconcentrator.excel.MeterData;
import com.system.mconcentrator.mconcentrator.scrollablepanel.DateInfo;
import com.system.mconcentrator.mconcentrator.scrollablepanel.OrderInfo;
import com.system.mconcentrator.mconcentrator.scrollablepanel.RoomInfo;
import com.system.mconcentrator.mconcentrator.scrollablepanel.ScrollablePanelAdapter;
import com.system.mconcentrator.mconcentrator.utils.Const;
import com.system.mconcentrator.mconcentrator.utils.Conversion;
import com.system.mconcentrator.mconcentrator.utils.LogHelper;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.system.mconcentrator.mconcentrator.utils.Conversion.getCrc;
import static com.system.mconcentrator.mconcentrator.utils.Conversion.hexStr2Bytes;
import static com.system.mconcentrator.mconcentrator.utils.Conversion.readmeterTime;
import static com.system.mconcentrator.mconcentrator.utils.Conversion.readmeterdataanalynsis;
import static com.system.mconcentrator.mconcentrator.utils.Conversion.twoDataReverseOrder;
import static com.system.mconcentrator.mconcentrator.utils.MathExtends.subtract;
import static com.system.mconcentrator.mconcentrator.utils.protocol.CopyMeterCommand;
import static com.system.mconcentrator.mconcentrator.utils.protocol.DeviceInfoProtocolEnd;
import static com.system.mconcentrator.mconcentrator.utils.protocol.DeviceInfoProtocolEnd1;
import static com.system.mconcentrator.mconcentrator.utils.protocol.ReadAllMeterCommand;
import static com.system.mconcentrator.mconcentrator.utils.protocol.ReadOneMeterCommand;
import static com.system.mconcentrator.mconcentrator.utils.protocol.SendDeviceInfoProtocolHead;
import static com.system.mconcentrator.mconcentrator.utils.protocol.TcpIpCommand;

public class CopyMeter188Activity extends SerialPortActivity implements View.OnClickListener, ScrollablePanelAdapter.TableNumClickListener {

    private static final String TAG = "CopyMeter188Activity";

    private FrameLayout menu_titleup;

    //表头控件
    private TextView Back_tv;
//  private TextView tv_connect;
//  private TextView tv_right;

    //控件
    private Button readfileFromDb;
    private Button SavafileToDb;
    private Button CopyMeterData;
    private Button CopyAllMeter;

    ScrollablePanel scrollablePanel;
    ScrollablePanelAdapter scrollablePanelAdapter;

    List<List<OrderInfo>> ordersList;
    List<OrderInfo> orderInfoList;

    //接收数据处理
    StringBuffer txtsb;
    Boolean StartFlag = false;
    Boolean receiveflag = false;
    //全局变量 行和列
    private int Copyrow;

    //采集表对话框
    AlertDialog CopyMeterDialog;

    //抄读所有表标志位
    public boolean CopymeterDialogflag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_copy_meter188);
        initViews();
        initData();
        initLinstener();
    }

    private void initLinstener() {

        Back_tv.setOnClickListener(this);
        readfileFromDb.setOnClickListener(this);
        SavafileToDb.setOnClickListener(this);
        CopyMeterData.setOnClickListener(this);
        CopyAllMeter.setOnClickListener(this);
        scrollablePanelAdapter.setOnTableNumClickListener(this);
    }

    private void initData() {

        //设置表头透明
        menu_titleup.getBackground().setAlpha(0);

        //显示左边的组件
        Back_tv.setVisibility(View.VISIBLE);
        Back_tv.setText("返回");

        //初始化数据
        txtsb = new StringBuffer();

    }

    private void initViews() {
        //顶部条控件
        menu_titleup = (FrameLayout) findViewById(R.id.menu_titleup);
        //tv_right = (TextView) findViewById(R.id.tv_right);
        Back_tv = (TextView) findViewById(R.id.Back_tv);
        // tv_connect = (TextView) findViewById(R.id.tv_connect);

        // List<MeterData> mdatas = DataSupport.findAll(MeterData.class);
        List<MeterData> mdatas = new ArrayList<>();

//        final ScrollablePanel scrollablePanel = (ScrollablePanel) findViewById(R.id.scrollable_panel);
//        final ScrollablePanelAdapter scrollablePanelAdapter = new ScrollablePanelAdapter();

        scrollablePanel = (ScrollablePanel) findViewById(R.id.scrollable_panel);
        scrollablePanelAdapter = new ScrollablePanelAdapter();
        generateTestData(scrollablePanelAdapter, mdatas);
        scrollablePanel.setPanelAdapter(scrollablePanelAdapter);

        readfileFromDb = (Button) findViewById(R.id.readfileFromDb);
        SavafileToDb = (Button) findViewById(R.id.SavafileToDb);
        CopyMeterData = (Button) findViewById(R.id.CopyMeterData);
        CopyAllMeter = (Button) findViewById(R.id.CopyAllMeter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.readfileFromDb:
                CopymeterDialogflag = true;
                List<MeterData> mdatas = DataSupport.findAll(MeterData.class);
                generateTestData(scrollablePanelAdapter, mdatas);
                scrollablePanel.setPanelAdapter(scrollablePanelAdapter);
                break;
            case R.id.SavafileToDb:

                LogHelper.d("SavafileToDb", "Db");
                List<MeterData> mdatas1 = DataSupport.findAll(MeterData.class);
                //将当前数据保存到数据库
                for (int i = 0; i < mdatas1.size(); i++) {
                    OrderInfo orderInfoOldReadData = ordersList.get(i).get(6);
                    OrderInfo orderInfoAmount = ordersList.get(i).get(7);
                    OrderInfo orderInfoOldTimeData = ordersList.get(i).get(9);
                    MeterData data = new MeterData();
                    if (!(orderInfoOldReadData == null || "".equals(orderInfoOldReadData.getGuestName()))) {
                        LogHelper.d(TAG + "orderInfoOldReadData", "orderInfoOldReadData");
                        data.setOldReadData(orderInfoOldReadData.getGuestName());
                    }
                    if (!(orderInfoAmount == null || "".equals(orderInfoAmount.getGuestName()))) {
                        LogHelper.d(TAG + "orderInfoAmount", "orderInfoAmount");
                        data.setAmount(orderInfoAmount.getGuestName());
                    }

                    if (!(orderInfoOldTimeData == null || "".equals(orderInfoOldTimeData.getGuestName()))) {
                        LogHelper.d(TAG + "orderInfoOldTimeData", "orderInfoOldTimeData");
                        data.setOldReadTime(orderInfoOldTimeData.getGuestName());
                    }
                    data.update(i + 1);
                }
                break;
            case R.id.CopyMeterData:
                txtsb.delete(0, txtsb.length());//删除所有的数据
                String copyallmeterstr1 = "FE" + CopyMeterCommand;
                String copyallmeterstr2 = SendDeviceInfoProtocolHead + copyallmeterstr1 + getCrc(copyallmeterstr1) + DeviceInfoProtocolEnd;
                LogHelper.d(TAG + "copyallmeterstr2", copyallmeterstr2);
                try {
                    mOutputStream.write(hexStr2Bytes(copyallmeterstr2));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //弹出对话框等待
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                CopyMeterDialog = builder.setTitle("采集表数据").setMessage(
                        "正在采集请等待").create();
                CopyMeterDialog.show();

                break;
            case R.id.CopyAllMeter:
                if (CopymeterDialogflag == true) {
                    txtsb.delete(0, txtsb.length());//删除所有的数据
                    String ReadAllMeterStr1 = "FC" + ReadAllMeterCommand + "FFFFFFFFFFFF";
                    String ReadAllMeterStr2 = SendDeviceInfoProtocolHead + ReadAllMeterStr1 + getCrc(ReadAllMeterStr1) + DeviceInfoProtocolEnd;
                    LogHelper.d(TAG + "ReadAllMeterStr2", ReadAllMeterStr2);
                    try {
                        mOutputStream.write(hexStr2Bytes(ReadAllMeterStr2));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "请先读取文件", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.Back_tv:
                CopymeterDialogflag = false;
                finish();
                break;
        }

    }

    private void generateTestData(ScrollablePanelAdapter scrollablePanelAdapter, List<MeterData> mdatas) {

        List<RoomInfo> roomInfoList = new ArrayList<>();
        for (int i = 0; i < mdatas.size(); i++) {
            RoomInfo roomInfo = new RoomInfo();
            roomInfo.setRoomType("");
            roomInfo.setRoomId(i);
            roomInfo.setRoomName("" + i);
            roomInfoList.add(roomInfo);
        }

        scrollablePanelAdapter.setRoomInfoList(roomInfoList);

        List<DateInfo> dateInfoList = new ArrayList<>();

        DateInfo dateInfo1 = new DateInfo();

        dateInfo1.setDate("小区");
        dateInfoList.add(dateInfo1);

        DateInfo dateInfo2 = new DateInfo();
        dateInfo2.setDate("栋号");
        dateInfoList.add(dateInfo2);

        DateInfo dateInfo3 = new DateInfo();
        dateInfo3.setDate("房号");
        dateInfoList.add(dateInfo3);

        DateInfo dateInfo4 = new DateInfo();
        dateInfo4.setDate("表状态");
        dateInfoList.add(dateInfo4);

        DateInfo dateInfo5 = new DateInfo();
        dateInfo5.setDate("采集器编号");
        dateInfoList.add(dateInfo5);

        DateInfo dateInfo6 = new DateInfo();
        dateInfo6.setDate("表编号");
        dateInfoList.add(dateInfo6);

        DateInfo dateInfo7 = new DateInfo();
        dateInfo7.setDate("上次读数");
        dateInfoList.add(dateInfo7);

        DateInfo dateInfo8 = new DateInfo();
        dateInfo8.setDate("表读数");
        dateInfoList.add(dateInfo8);

        DateInfo dateInfo9 = new DateInfo();
        dateInfo9.setDate("用量");
        dateInfoList.add(dateInfo9);

        DateInfo dateInfo10 = new DateInfo();
        dateInfo10.setDate("上次抄表时间");
        dateInfoList.add(dateInfo10);

        DateInfo dateInfo11 = new DateInfo();
        dateInfo11.setDate("抄表时间");
        dateInfoList.add(dateInfo11);

        scrollablePanelAdapter.setDateInfoList(dateInfoList);

        ordersList = new ArrayList<>();
        for (int i = 0; i < mdatas.size(); i++) {
            orderInfoList = new ArrayList<>();
            for (int j = 0; j < 10; j++) {
                OrderInfo orderInfo = new OrderInfo();
                switch (j) {
                    case 0:
                        orderInfo.setGuestName(mdatas.get(i).getHotelNum());
                        break;
                    case 1:
                        orderInfo.setGuestName(mdatas.get(i).getHouseNum());
                        break;
                    case 2:
                        orderInfo.setGuestName(mdatas.get(i).getMeterStyle());
                        break;
                    case 3:
                        orderInfo.setGuestName(mdatas.get(i).getCollectorNum());
                        break;
                    case 4:
                        orderInfo.setGuestName(mdatas.get(i).getMeterNum());
                        break;
                    case 5:
                        orderInfo.setGuestName(mdatas.get(i).getOldReadData());
                        break;
                    case 6:
                        orderInfo.setGuestName(mdatas.get(i).getMeterReadData());
                        break;
                    case 7:
                        orderInfo.setGuestName(mdatas.get(i).getAmount());
                        break;
                    case 8:
                        orderInfo.setGuestName(mdatas.get(i).getOldReadTime());
                        break;
                    case 9:
                        orderInfo.setGuestName(mdatas.get(i).getNowReadTime());
                        break;
                }
                orderInfo.setBegin(true);
                orderInfo.setStatus(OrderInfo.Status.REVERSE);
                orderInfoList.add(orderInfo);
            }
            ordersList.add(orderInfoList);
            scrollablePanelAdapter.setOrdersList(ordersList);
        }
    }


    @Override
    protected void onDataReceived(byte[] buffer, int size) {

        String txt = Conversion.BytetohexString(buffer, size);
//        if (txt != null) {
//            txtsb.append(txt);
//            LogHelper.d("onDataReceived+++txt", txt.toString());
//            LogHelper.d("onDataReceived+++txtsb", txtsb.toString());
//        }
        if ("7B01001630303030303030303030310D".equals(txt))
            return;

        //设置开始接收的头部
        if (txt != null)
            txtsb.append(txt);
        LogHelper.d(TAG + "onDataReceived+++txt1", txt);
        LogHelper.d(TAG + "onDataReceived+++txtsb1", txtsb.toString());
        LogHelper.d(TAG + "txtsb.length()", Integer.toString(txtsb.length()));
        //如果txtsb接收到的字符串>8，或者StartFlag为true，证明字符已经接收到包头，在接收状态
        if (txtsb.length() >= 8 || StartFlag == true) {
            LogHelper.d(TAG + "txtsb.substring(0, 8) ", txtsb.substring(0, 8));
            //设置开始接收的头部,包头没问题开始接收
            if (TcpIpCommand.equals(txtsb.substring(0, 8))) {
                //继续接收，设置这串字符在接收状态
                StartFlag = true;
                LogHelper.d(TAG + "continue ", "continue");
                LogHelper.d(TAG + "txtsb", txtsb.toString());
                LogHelper.d(TAG + "txtsb.substring(txtsb.length() - 2)", txtsb.substring(txtsb.length() - 4));
                //判断接收的字符串是否结束
                if (DeviceInfoProtocolEnd1.equals(txtsb.substring(txtsb.length() - 4))) {
                    //判断CRC校验码,如果校验码正确，则接收到全部数据
                    String onDataReceivedCRC1 = txtsb.substring(txtsb.length() - 8, txtsb.length() - 4);
                    String onDataReceivedCRC2 = getCrc(txtsb.substring(8, txtsb.length() - 8));
                    LogHelper.d(TAG + "onDataReceivedCRC1 ", onDataReceivedCRC1);
                    LogHelper.d(TAG + "onDataReceivedCRC2 ", onDataReceivedCRC2);
                    if (onDataReceivedCRC1.equals(onDataReceivedCRC2)) {
                        LogHelper.d(TAG + "onDataReceived+++txtsb2end", txtsb.toString());
                        LogHelper.d(TAG + "onDataReceived+++txtsblength2end", Integer.toString(txtsb.length()));
                        //加个变量缓存接收到的数据用handler发送出去
                        StartFlag = false;
                        receiveflag = true;
                        // txtsb.delete(0, txtsb.length());//删除所有的数据
                    }

                } else {

                }
            } else {
                StartFlag = false;
                txtsb.delete(0, txtsb.length());//删除所有的数据
                LogHelper.d("end1 ", "end1");
            }
        } else {
            StartFlag = false;
            txtsb.delete(0, txtsb.length());//删除所有的数据
            LogHelper.d("end ", "end");
        }

        //处理接收字符串,判断字符串中的标志位
        if (receiveflag == true) {
            receiveflag = false;
            //接收数据完成
            String flagStr = txtsb.substring(8, 10);
            final String mRdata = txtsb.toString();
            LogHelper.d(TAG + "flagStr ", flagStr);
            switch (flagStr) {
                case "FE":
                    CopyMeterDialog.dismiss();
                    break;

                case "FD":
                    LogHelper.d(TAG + "FD", "FD+++++");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String[] mdataone = new String[3];
                            mdataone[0] = readmeterdataanalynsis(mRdata.substring(34, 42));
                            mdataone[1] = mRdata.substring(42, 44);
                            mdataone[2] = readmeterTime(mRdata.substring(48, 76));
                            LogHelper.d(TAG + "mdataone[0]", mdataone[0]);
                            LogHelper.d(TAG + "mdataone[1]", mdataone[1]);
                            LogHelper.d(TAG + "mdataone[2]", mdataone[2]);
                            //发送数据
                            Message msg = new Message();
                            msg.what = Const.readonemeter;
                            msg.obj = mdataone;
                            handler.sendMessage(msg);
                            txtsb.delete(0, txtsb.length());//删除所有的数据


                        }
                    }).start();

                    break;

                case "FC":
                    LogHelper.d(TAG + "FC", "FC+++++");

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //做耗时操作
                            //得到表的数量
                            int metercount = (mRdata.length() - 18) / 66;
                            LogHelper.d(TAG + "metercount", Integer.toString(metercount));
                            String[][] mdataArray = new String[metercount][3];
                            for (int i = 0; i < metercount; i++) {
                                mdataArray[i][0] = mRdata.substring(34 + i * 66, 42 + i * 66);
                                mdataArray[i][1] = mRdata.substring(42 + i * 66, 44 + i * 66);
                                mdataArray[i][2] = mRdata.substring(48 + i * 66, 76 + i * 66);
                            }
//                                for(int i = 0;i<mdataArray.length;i++)
//                                for(int j = 0;j<mdataArray[i].length;j++)
//                                {
//                                   LogHelper.d(TAG+"mdataArray"+i+j,mdataArray[i][j]);
//                                }
                            String[][] mdataArray1 = new String[metercount][3];
                            for (int i = 0; i < mdataArray.length; i++) {
                                //读数调整为十进制
                                mdataArray1[i][0] = readmeterdataanalynsis(mdataArray[i][0]);
                                //状态不变
                                mdataArray1[i][1] = mdataArray[i][1];
                                //解析时间
                                mdataArray1[i][2] = readmeterTime(mdataArray[i][2]);

                            }
                            for (int i = 0; i < mdataArray1.length; i++)
                                for (int j = 0; j < mdataArray1[i].length; j++) {
                                    LogHelper.d(TAG + "mdataArray1" + i + j, mdataArray1[i][j]);
                                }
                            //发送数据
                            Message msg1 = new Message();
                            msg1.what = Const.readallmeter;
                            msg1.obj = mdataArray1;
                            handler.sendMessage(msg1);
                            txtsb.delete(0, txtsb.length());//删除所有的数据
                        }
                    }).start();
                    break;
            }
        }

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Const.readallmeter:
                    String[][] readallmeterString = (String[][]) msg.obj;
//                    for (int i = 0; i < readallmeterString.length; i++)
//                        for (int j = 0; j < readallmeterString[i].length; j++) {
//                            LogHelper.d(TAG + "readallmeterString" + i + j, readallmeterString[i][j]);
//                        }
                    for (int i = 0; i < readallmeterString.length; i++) {
                        OrderInfo orderInfo1 = new OrderInfo();
                        orderInfo1.setGuestName(readallmeterString[i][0]);
                        ordersList.get(i).set(6, orderInfo1);

                        OrderInfo orderInfo2 = new OrderInfo();
                        if ("4F".equals(readallmeterString[i][1])) {
                            orderInfo2.setGuestName("正常");
                            ordersList.get(i).set(2, orderInfo2);
                        } else {
                            orderInfo2.setGuestName("失败");
                            ordersList.get(i).set(2, orderInfo2);
                        }

                        OrderInfo orderInfo3 = new OrderInfo();
                        orderInfo3.setGuestName(readallmeterString[i][2]);
                        ordersList.get(i).set(9, orderInfo3);
                        //用量
                        OrderInfo orderInfo4 = new OrderInfo();
                        orderInfo4.setGuestName(subtract(readallmeterString[i][0],ordersList.get(i).get(5).getGuestName()));
                        ordersList.get(i).set(7, orderInfo4);
                    }
                    scrollablePanel.notifyDataSetChanged();
                    break;
                case Const.readonemeter:
                    String[] readonemeterString = (String[]) msg.obj;
                    OrderInfo orderInfo1 = new OrderInfo();
                    orderInfo1.setGuestName(readonemeterString[0]);
                    ordersList.get(Copyrow - 1).set(6, orderInfo1);

                    OrderInfo orderInfo2 = new OrderInfo();
                    if ("4F".equals(readonemeterString[1])) {
                        orderInfo2.setGuestName("正常");
                        ordersList.get(Copyrow - 1).set(2, orderInfo2);
                    } else {
                        orderInfo2.setGuestName("失败");
                        ordersList.get(Copyrow - 1).set(2, orderInfo2);
                    }

                    OrderInfo orderInfo3 = new OrderInfo();
                    orderInfo3.setGuestName(readonemeterString[2]);
                    ordersList.get(Copyrow - 1).set(9, orderInfo3);
                    scrollablePanel.notifyDataSetChanged();
                    break;

            }
        }
    };

    @Override
    public void tableNumclickListener(int row, int column, List<List<OrderInfo>>
            ordersList, ScrollablePanelAdapter.OrderViewHolder viewHolder) {
        LogHelper.d("row+++", Integer.toString(row));  //行
        LogHelper.d("column+++", Integer.toString(column));//列

        Copyrow = row;
        String Onejizhongqiaddr = twoDataReverseOrder(ordersList.get(row - 1).get(4).getGuestName()); //采集集中器的地址
        String Onemeteraddr = twoDataReverseOrder(ordersList.get(row - 1).get(3).getGuestName());//采集表地址

        LogHelper.d(TAG + "Onejizhongqiaddr+++", Onejizhongqiaddr);
        LogHelper.d(TAG + "Onemeteraddr+++", Onemeteraddr);

        txtsb.delete(0, txtsb.length());//删除所有的数据
        String readonemeterStr1 = "FD" + ReadOneMeterCommand + Onejizhongqiaddr + Onemeteraddr;
        String readonemeterStr2 = SendDeviceInfoProtocolHead + readonemeterStr1 + getCrc(readonemeterStr1) + DeviceInfoProtocolEnd;
        LogHelper.d(TAG + "readonemeterStr2", readonemeterStr2);
        try {
            mOutputStream.write(hexStr2Bytes(readonemeterStr2));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //LogHelper.d("meternum+++",meternum);
//        OrderInfo orderInfo = new OrderInfo();
//        orderInfo.setGuestName(Integer.toString(row));
        //更新list数据
//        ordersList.get(row-1).set(column-1, orderInfo);
//        ordersList.get(row-1).set(column-1, orderInfo);
//        ordersList.get(row-1).set(column-1, orderInfo);
//        ordersList.get(row-1).set(column-1, orderInfo);
        //更新整个list
        // this.ordersList = ordersList;
        //  scrollablePanelAdapter.setOrderView(row,column+2,viewHolder,true,orderInfo);
//        scrollablePanelAdapter.setOrderView(row,column+3,viewHolder,true,orderInfo);
//        scrollablePanelAdapter.setOrderView(row,column+5,viewHolder,true,orderInfo);
//        scrollablePanel.notifyDataSetChanged();


        //取出对应行的采集器编号和表编号
/*
        String CollectionDeviceNum = ordersList.get(row - 1).get(column - 1).getGuestName();
        LogHelper.d("CollectionDeviceNum", CollectionDeviceNum);

        String MeterDeviceNum = ordersList.get(row - 1).get(column - 2).getGuestName();
        LogHelper.d("MeterDeviceNum", MeterDeviceNum);

        //组合成对应的集中器采集单表的命令，发送
        String SendOneMeterString1 = CollectionCommand + twoDataReverseOrder(CollectionDeviceNum)
                + twoDataReverseOrder(MeterDeviceNum);
        String SendOneMeterString2 = SendDeviceInfoProtocolHead + SendOneMeterString1 + getCrc(SendOneMeterString1) + DeviceInfoProtocolEnd;
        LogHelper.d("+++SendOneMeterString2", SendOneMeterString2);
        StartFlag = false;
        txtsb.delete(0, txtsb.length());//删除所有的数据
        try {
            mOutputStream.write(hexStr2Bytes(SendOneMeterString2));
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
    }

}
