package com.system.mconcentrator.mconcentrator;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.kelin.scrollablepanel.library.ScrollablePanel;
import com.system.mconcentrator.mconcentrator.excel.MeterData;
import com.system.mconcentrator.mconcentrator.scrollablepanel.DateInfo;
import com.system.mconcentrator.mconcentrator.scrollablepanel.OrderInfo;
import com.system.mconcentrator.mconcentrator.scrollablepanel.RoomInfo;
import com.system.mconcentrator.mconcentrator.scrollablepanel.ScrollablePanelAdapter;
import com.system.mconcentrator.mconcentrator.utils.Conversion;
import com.system.mconcentrator.mconcentrator.utils.LogHelper;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.system.mconcentrator.mconcentrator.utils.Conversion.getCrc;
import static com.system.mconcentrator.mconcentrator.utils.Conversion.hexStr2Bytes;
import static com.system.mconcentrator.mconcentrator.utils.Conversion.twoDataReverseOrder;
import static com.system.mconcentrator.mconcentrator.utils.protocol.CollectionCommand;
import static com.system.mconcentrator.mconcentrator.utils.protocol.DeviceInfoProtocolEnd;
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
    private Button CopyOneMeter;
    private Button CopyAllMeter;

    ScrollablePanel scrollablePanel;
    ScrollablePanelAdapter scrollablePanelAdapter;

    List<List<OrderInfo>> ordersList;
    List<OrderInfo> orderInfoList;

    //接收数据处理
    StringBuffer txtsb;
    Boolean StartFlag = false;


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
        CopyOneMeter.setOnClickListener(this);
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
        CopyOneMeter = (Button) findViewById(R.id.CopyOneMeter);
        CopyAllMeter = (Button) findViewById(R.id.CopyAllMeter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.readfileFromDb:
                List<MeterData> mdatas = DataSupport.findAll(MeterData.class);
                generateTestData(scrollablePanelAdapter, mdatas);
                scrollablePanel.setPanelAdapter(scrollablePanelAdapter);
                break;
            case R.id.SavafileToDb:

                LogHelper.d("SavafileToDb", "Db");
                List<MeterData> mdatas1 = DataSupport.findAll(MeterData.class);
                //将当前数据保存到数据库
                for(int i = 0;i<mdatas1.size();i++) {
                    OrderInfo orderInfoOldReadData = ordersList.get(i).get(6);
                    OrderInfo orderInfoAmount = ordersList.get(i).get(7);
                    OrderInfo orderInfoOldTimeData = ordersList.get(i).get(9);
                    MeterData data = new MeterData();
                    if(!(orderInfoOldReadData == null ||"".equals(orderInfoOldReadData.getGuestName())))
                    {
                        LogHelper.d(TAG+"orderInfoOldReadData","orderInfoOldReadData");
                        data.setOldReadData(orderInfoOldReadData.getGuestName());
                    }
                    if(!(orderInfoAmount == null ||"".equals(orderInfoAmount.getGuestName())))
                    {
                        LogHelper.d(TAG+"orderInfoAmount","orderInfoAmount");
                        data.setAmount(orderInfoAmount.getGuestName());
                    }

                    if(!(orderInfoOldTimeData == null ||"".equals(orderInfoOldTimeData.getGuestName())))
                    {
                        LogHelper.d(TAG+"orderInfoOldTimeData","orderInfoOldTimeData");
                        data.setOldReadTime(orderInfoOldTimeData.getGuestName());
                    }
                    data.update(i+1);
                }
                break;
            case R.id.CopyOneMeter:

                break;
            case R.id.CopyAllMeter:

                break;
            case R.id.Back_tv:
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
        dateInfo4.setDate("表类型");
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


    private void readDBtofile() {

    }

    @Override
    protected void onDataReceived(byte[] buffer, int size) {

        String txt = Conversion.BytetohexString(buffer, size);
//        if (txt != null) {
//            txtsb.append(txt);
//            LogHelper.d("onDataReceived+++txt", txt.toString());
//            LogHelper.d("onDataReceived+++txtsb", txtsb.toString());
//        }


        //设置开始接收的头部
        if (txt != null) {
            txtsb.append(txt);
            LogHelper.d(TAG+"onDataReceived+++txt1", txt);
            LogHelper.d(TAG+"onDataReceived+++txtsb1", txtsb.toString());
            LogHelper.d(TAG+"txtsb.length()", Integer.toString(txtsb.length()));
            //如果txtsb接收到的字符串>8，或者StartFlag为true，证明字符已经接收到包头，在接收状态
            if (txtsb.length() >= 8 || StartFlag == true) {
                LogHelper.d(TAG+"txtsb.substring(0, 8) ", txtsb.substring(0, 8));
                //设置开始接收的头部,包头没问题开始接收
                if (TcpIpCommand.equals(txtsb.substring(0, 8))) {
                    //继续接收，设置这串字符在接收状态
                    StartFlag = true;
                    LogHelper.d(TAG+"continue ", "continue");
                    LogHelper.d(TAG+"txtsb", txtsb.toString());
                    LogHelper.d(TAG+"txtsb.substring(txtsb.length() - 2)", txtsb.substring(txtsb.length() - 2));
                    //判断接收的字符串是否结束
                    if (DeviceInfoProtocolEnd.equals(txtsb.substring(txtsb.length() - 2))&&txtsb.length() >= 56) {
                        //判断CRC校验码,如果校验码正确，则接收到全部数据
                        String onDataReceivedCRC1 = txtsb.substring(txtsb.length() - 6, txtsb.length() - 2);
                        String onDataReceivedCRC2 = getCrc(txtsb.substring(50, txtsb.length() - 6));
                        LogHelper.d(TAG+"onDataReceivedCRC1 ", txtsb.substring(txtsb.length() - 6, txtsb.length() - 2));
                        LogHelper.d(TAG+"onDataReceivedCRC2 ", getCrc(txtsb.substring(50, txtsb.length() - 6)));
                            if (onDataReceivedCRC1.equals(onDataReceivedCRC2)){
                                LogHelper.d(TAG+"onDataReceived+++txtsb2end", txtsb.toString());
                                LogHelper.d(TAG+"onDataReceived+++txtsblength2end", Integer.toString(txtsb.length()));
                                //加个变量缓存接收到的数据用handler发送出去
                                StartFlag = false;
                                txtsb.delete(0, txtsb.length());//删除所有的数据
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

        }


//        LogHelper.d("onDataReceived+++txtsb", txtsb.toString());
//        LogHelper.d("onDataReceived+++txtsblength", Integer.toString(txtsb.length()));

    }

    @Override
    public void tableNumclickListener(int row, int column, List<List<OrderInfo>>
            ordersList, ScrollablePanelAdapter.OrderViewHolder viewHolder) {
        LogHelper.d("row+++", Integer.toString(row));  //行
        LogHelper.d("column+++", Integer.toString(column));//列
        //LogHelper.d("meternum+++",meternum);
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setGuestName(Integer.toString(row));
        //更新list数据
//        ordersList.get(row-1).set(column-1, orderInfo);
//        ordersList.get(row-1).set(column-1, orderInfo);
//        ordersList.get(row-1).set(column-1, orderInfo);
//        ordersList.get(row-1).set(column-1, orderInfo);
        //更新整个list
       // this.ordersList = ordersList;
        scrollablePanelAdapter.setOrderView(row,column+2,viewHolder,true,orderInfo);
        scrollablePanelAdapter.setOrderView(row,column+3,viewHolder,true,orderInfo);
        scrollablePanelAdapter.setOrderView(row,column+5,viewHolder,true,orderInfo);
        scrollablePanel.notifyDataSetChanged();

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
