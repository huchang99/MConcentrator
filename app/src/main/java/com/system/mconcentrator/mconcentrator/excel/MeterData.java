package com.system.mconcentrator.mconcentrator.excel;

import org.litepal.crud.DataSupport;

/**
 * Created by huchang on 2017/12/6.
 */

public class MeterData extends DataSupport {


    private String HotelNum;
    private String HouseNum;
    private String MeterStyle;
    private String CollectorNum;
    private String MeterNum;
    private String OldReadData;
    private String MeterReadData;
    private String Amount;
    private String OldReadTime;
    private String NowReadTime;

    public String getHotelNum() {
        return HotelNum;
    }

    public void setHotelNum(String hotelNum) {
        this.HotelNum = hotelNum;
    }

    public String getHouseNum() {
        return HouseNum;
    }

    public void setHouseNum(String houseNum) {
        this.HouseNum = houseNum;
    }

    public String getMeterStyle() {
        return MeterStyle;
    }

    public void setMeterStyle(String meterStyle) {
        this.MeterStyle = meterStyle;
    }

    public String getCollectorNum() {
        return CollectorNum;
    }

    public void setCollectorNum(String collectorNum) {
        this.CollectorNum = collectorNum;
    }

    public String getMeterNum() {
        return MeterNum;
    }

    public void setMeterNum(String meterNum) {
        this.MeterNum = meterNum;
    }

    public String getOldReadData() {
        return OldReadData;
    }

    public void setOldReadData(String oldReadData) {
        this.OldReadData = oldReadData;
    }

    public String getMeterReadData() {
        return MeterReadData;
    }

    public void setMeterReadData(String meterReadData) {
        this.MeterReadData = meterReadData;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        this.Amount = amount;
    }

    public String getOldReadTime() {
        return OldReadTime;
    }

    public void setOldReadTime(String oldReadTime) {
        this.OldReadTime = oldReadTime;
    }

    public String getNowReadTime() {
        return NowReadTime;
    }

    public void setNowReadTime(String nowReadTime) {
        this.NowReadTime = nowReadTime;
    }

}
