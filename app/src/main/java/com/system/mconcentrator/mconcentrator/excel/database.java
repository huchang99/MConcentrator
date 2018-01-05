package com.system.mconcentrator.mconcentrator.excel;

import android.util.Log;

import com.system.mconcentrator.mconcentrator.utils.LogHelper;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * Created by huchang on 2017/12/6.
 */

public class database {


    private static final String TAG = "database";
    private String sourcefile = "/storage/usbhost/8_4/ExtraMeter.xls";
    private String OutfilePath = "/storage/usbhost/8_4/Meter1.xls";
  //  private String sourcefile = "/storage/usbhost2/8_4/ExtraMeter.xls";
  //  private String OutfilePath = "/storage/usbhost2/8_4/Meter1.xls";
    private Workbook wbook;
    private WritableWorkbook wwbCopy;
    private WritableSheet shSheet;


    private MeterData Meterdata;
    private String[] title = {"编号", "栋号", "房号", "表类型", "采集器编号", "表编号", "上次读数", "表读数", "用量", "上次抄表时间", "抄表时间"};

    public database() {
        super();

    }

    public void readExcelToDB() {
        try {
//            wbook = Workbook.getWorkbook(new File("path/testSampleData.xls"));
//            wwbCopy = Workbook.createWorkbook(new File("path/testSampleDataCopy.xls"), wbook);
//            shSheet = wwbCopy.getSheet(0);
            //InputStream is = context.getAssets().open("ExtraMeter.xls");
            Log.d("readExcelToDB", "readExcelToDB start");
            Workbook book = Workbook.getWorkbook(new File(sourcefile));
            book.getNumberOfSheets();
            //获取第一个工作表对象
            Sheet sheet = book.getSheet(0);
            int Rows = sheet.getRows();
            ExtraExcel info = null;

            for (int i = 1; i < Rows; i++) {
                String HotelNum = (sheet.getCell(2, i)).getContents();
                String HouseNum = (sheet.getCell(3, i)).getContents();
                String MeterStyle = (sheet.getCell(4, i)).getContents();
                String CollectorNum = (sheet.getCell(6, i)).getContents();
                String MeterNum = (sheet.getCell(7, i)).getContents();
                String OldReadData = (sheet.getCell(8, i)).getContents();
                String MeterReadData = (sheet.getCell(9, i)).getContents();
                String Amount = (sheet.getCell(10, i)).getContents();
                String OldReadTime = (sheet.getCell(14, i)).getContents();
                String NowReadTime = (sheet.getCell(15, i)).getContents();

                info = new ExtraExcel(HotelNum, HouseNum, MeterStyle, CollectorNum, MeterNum, OldReadData,
                        MeterReadData, Amount, OldReadTime, NowReadTime);
                saveInfoToDataBase(info);


            }
            book.close();
            Log.d("jzq_excel", "excel读取完成");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveInfoToDataBase(ExtraExcel info) {

        MeterData meterdata = new MeterData();
        meterdata.setHotelNum(info.getHotelNum());
        meterdata.setHouseNum(info.getHouseNum());
        meterdata.setMeterStyle(info.getMeterStyle());
        meterdata.setCollectorNum(info.getCollectorNum());
        meterdata.setMeterNum(info.getMeterNum());
        meterdata.setOldReadData(info.getOldReadData());
        meterdata.setMeterReadData(info.getMeterReadData());
        meterdata.setAmount(info.getAmount());
        meterdata.setOldReadTime(info.getOldReadTime());
        meterdata.setNowReadTime(info.getNowReadTime());
        meterdata.save();

    }

    //初始化导出的Excel表
    public void DbToExcel() {
        WritableWorkbook Meterwwb = null;
        String content = null;
        try {
            // File file = new File("/storage/usbhost/8_4/ExtraMeter.xls");
            File file = new File(OutfilePath);
            if (!file.exists()) {
                file.createNewFile();
            }

            Meterwwb = Workbook.createWorkbook(file);

            //创建工作表
            WritableSheet Metersheet = Meterwwb.createSheet("sheet1", 0);

            //创建Lable
            /*Label(x,y,z)其中x代表单元格的第x+1列，第y+1行, 单元格的内容是z
             * 在Label对象的子对象中指明单元格的位置和内容
             */
            LogHelper.d("title.length",Integer.toString(title.length));
            for (int i = 0; i < title.length; i++) {

                Label label = new Label(i, 0, title[i]);

                /**将定义好的单元格添加到工作表中*/
                Metersheet.addCell(label);
            }

            //读取数据库的内容，存到Excel表格中
            List<MeterData> meterDbDatas = DataSupport.findAll(MeterData.class);
            //for(MeterData mdata :meterDbDatas)
            for (int i = 0; i < meterDbDatas.size(); i++) {
               // LogHelper.d("meterDbDatas.size()",Integer.toString(meterDbDatas.size()));
                MeterData mdata = meterDbDatas.get(i);
                for (int j = 0; j < title.length; j++) {
                    switch (j) {
                        case 0: {
                            content = Integer.toString(i+1);
                            break;
                        }
                        case 1: {
                            content = mdata.getHotelNum();
                            break;
                        }
                        case 2: {
                            content = mdata.getHouseNum();
                            break;
                        }
                        case 3: {
                            content = mdata.getMeterStyle();
                            break;
                        }
                        case 4: {
                            content = mdata.getCollectorNum();
                            break;
                        }
                        case 5: {
                            content = mdata.getMeterNum();
                            break;
                        }
                        case 6: {
                            content = mdata.getOldReadData();
                            break;
                        }
                        case 7: {
                            content = mdata.getMeterReadData();
                            break;
                        }
                        case 8: {
                            content = mdata.getAmount();
                            break;
                        }
                        case 9: {
                            content = mdata.getOldReadTime();
                            break;
                        }
                        case 10: {
                            content = mdata.getNowReadTime();
                            break;
                        }

                    }
                    Label label1 = new Label(j, i+1, content);
                    Metersheet.addCell(label1);

                }
            }
            // 写入数据
            Meterwwb.write();
            // 关闭文件
            Meterwwb.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
