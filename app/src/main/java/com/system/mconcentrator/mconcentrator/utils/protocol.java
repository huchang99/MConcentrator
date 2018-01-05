package com.system.mconcentrator.mconcentrator.utils;

/**
 * Created by huchang on 2017/11/16.
 */

public class protocol {

    //数据包头
    public final static String TcpIpCommand = "7B010016";
    public final static String SendDeviceInfoProtocolHead = "42424242424242424253"; //发送数据包头
    public final static String ReceiveDeviceInfoProtocolHead = ""; //发送数据包头
    //数据包尾
    public final static String DeviceInfoProtocolEnd = "45"; //发送数据包尾
    public final static String DeviceInfoProtocolEnd1 = "450D"; //发送数据包尾

    //指令
    public final static String CopyMeterCommand= "585858";   //采集表数据组合指令
    public final static String ReadOneMeterCommand = "515151";   //读单只表指令
    public final static String ReadAllMeterCommand = "565656";   //读所有表指令

    public final static String ReadCommand = "525252";   //RRR 读取指令
    public final static String CollectionCommand = "4D4D4D";  //MMM  采集指令
    public final static String SetSystemTime = "545454";     //TTT  设置系统时钟
    public final static String TimingCollection = "505050";  //PPP   设置定时采集
    public final static String SetRemarksInfoCommand = "4E4E4E";  //NNN  写备注信息
    public final static String SetModemCommand = "414141";  //AAA  MODEM配置指令
    public final static String OpengateCommand = "4B4B4B"; //KKK 表阀门打开指令
    public final static String OffgateCommand = "474747"; //GGG 表阀门打开指令
    public final static String SetRemarksInfodefault1 = "BCAFD6D0C6F720202020A3BA";
    public final static String SetRemarksInfodefault2 = "0D0A";
    public final static String SetRemarksInfodefault3 = "B0E6B1BE202020202020A3BA0D0AB3F6B3A7C8D5C6DA2020A3BA0D0A";

    //页码
    public final static String PageOne = "00";


    static final int[] ha00 = {
            0x00,0x10,0x20,0x30,0x40,0x50,0x60,0x70,0x81,0x91,0xA1,0xB1,0xC1,0xD1,0xE1,0xF1,
            0x12,0x02,0x32,0x22,0x52,0x42,0x72,0x62,0x93,0x83,0xB3,0xA3,0xD3,0xC3,0xF3,0xE3,
            0x24,0x34,0x04,0x14,0x64,0x74,0x44,0x54,0xA5,0xB5,0x85,0x95,0xE5,0xF5,0xC5,0xD5,
            0x36,0x26,0x16,0x06,0x76,0x66,0x56,0x46,0xB7,0xA7,0x97,0x87,0xF7,0xE7,0xD7,0xC7,
            0x48,0x58,0x68,0x78,0x08,0x18,0x28,0x38,0xC9,0xD9,0xE9,0xF9,0x89,0x99,0xA9,0xB9,
            0x5A,0x4A,0x7A,0x6A,0x1A,0x0A,0x3A,0x2A,0xDB,0xCB,0xFB,0xEB,0x9B,0x8B,0xBB,0xAB,
            0x6C,0x7C,0x4C,0x5C,0x2C,0x3C,0x0C,0x1C,0xED,0xFD,0xCD,0xDD,0xAD,0xBD,0x8D,0x9D,
            0x7E,0x6E,0x5E,0x4E,0x3E,0x2E,0x1E,0x0E,0xFF,0xEF,0xDF,0xCF,0xBF,0xAF,0x9F,0x8F,
            0x91,0x81,0xB1,0xA1,0xD1,0xC1,0xF1,0xE1,0x10,0x00,0x30,0x20,0x50,0x40,0x70,0x60,
            0x83,0x93,0xA3,0xB3,0xC3,0xD3,0xE3,0xF3,0x02,0x12,0x22,0x32,0x42,0x52,0x62,0x72,
            0xB5,0xA5,0x95,0x85,0xF5,0xE5,0xD5,0xC5,0x34,0x24,0x14,0x04,0x74,0x64,0x54,0x44,
            0xA7,0xB7,0x87,0x97,0xE7,0xF7,0xC7,0xD7,0x26,0x36,0x06,0x16,0x66,0x76,0x46,0x56,
            0xD9,0xC9,0xF9,0xE9,0x99,0x89,0xB9,0xA9,0x58,0x48,0x78,0x68,0x18,0x08,0x38,0x28,
            0xCB,0xDB,0xEB,0xFB,0x8B,0x9B,0xAB,0xBB,0x4A,0x5A,0x6A,0x7A,0x0A,0x1A,0x2A,0x3A,
            0xFD,0xED,0xDD,0xCD,0xBD,0xAD,0x9D,0x8D,0x7C,0x6C,0x5C,0x4C,0x3C,0x2C,0x1C,0x0C,
            0xEF,0xFF,0xCF,0xDF,0xAF,0xBF,0x8F,0x9F,0x6E,0x7E,0x4E,0x5E,0x2E,0x3E,0x0E,0x1E};

    static int[] la00 = {
            0x00,0x21,0x42,0x63,0x84,0xA5,0xC6,0xE7,0x08,0x29,0x4A,0x6B,0x8C,0xAD,0xCE,0xEF,
            0x31,0x10,0x73,0x52,0xB5,0x94,0xF7,0xD6,0x39,0x18,0x7B,0x5A,0xBD,0x9C,0xFF,0xDE,
            0x62,0x43,0x20,0x01,0xE6,0xC7,0xA4,0x85,0x6A,0x4B,0x28,0x09,0xEE,0xCF,0xAC,0x8D,
            0x53,0x72,0x11,0x30,0xD7,0xF6,0x95,0xB4,0x5B,0x7A,0x19,0x38,0xDF,0xFE,0x9D,0xBC,
            0xC4,0xE5,0x86,0xA7,0x40,0x61,0x02,0x23,0xCC,0xED,0x8E,0xAF,0x48,0x69,0x0A,0x2B,
            0xF5,0xD4,0xB7,0x96,0x71,0x50,0x33,0x12,0xFD,0xDC,0xBF,0x9E,0x79,0x58,0x3B,0x1A,
            0xA6,0x87,0xE4,0xC5,0x22,0x03,0x60,0x41,0xAE,0x8F,0xEC,0xCD,0x2A,0x0B,0x68,0x49,
            0x97,0xB6,0xD5,0xF4,0x13,0x32,0x51,0x70,0x9F,0xBE,0xDD,0xFC,0x1B,0x3A,0x59,0x78,
            0x88,0xA9,0xCA,0xEB,0x0C,0x2D,0x4E,0x6F,0x80,0xA1,0xC2,0xE3,0x04,0x25,0x46,0x67,
            0xB9,0x98,0xFB,0xDA,0x3D,0x1C,0x7F,0x5E,0xB1,0x90,0xF3,0xD2,0x35,0x14,0x77,0x56,
            0xEA,0xCB,0xA8,0x89,0x6E,0x4F,0x2C,0x0D,0xE2,0xC3,0xA0,0x81,0x66,0x47,0x24,0x05,
            0xDB,0xFA,0x99,0xB8,0x5F,0x7E,0x1D,0x3C,0xD3,0xF2,0x91,0xB0,0x57,0x76,0x15,0x34,
            0x4C,0x6D,0x0E,0x2F,0xC8,0xE9,0x8A,0xAB,0x44,0x65,0x06,0x27,0xC0,0xE1,0x82,0xA3,
            0x7D,0x5C,0x3F,0x1E,0xF9,0xD8,0xBB,0x9A,0x75,0x54,0x37,0x16,0xF1,0xD0,0xB3,0x92,
            0x2E,0x0F,0x6C,0x4D,0xAA,0x8B,0xE8,0xC9,0x26,0x07,0x64,0x45,0xA2,0x83,0xE0,0xC1,
            0x1F,0x3E,0x5D,0x7C,0x9B,0xBA,0xD9,0xF8,0x17,0x36,0x55,0x74,0x93,0xB2,0xD1,0xF0
    };

}
