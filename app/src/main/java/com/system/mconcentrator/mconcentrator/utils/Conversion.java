package com.system.mconcentrator.mconcentrator.utils;

import java.util.Arrays;
import java.util.Formatter;
import java.util.Locale;

import static com.system.mconcentrator.mconcentrator.utils.protocol.ha00;
import static com.system.mconcentrator.mconcentrator.utils.protocol.la00;

/**
 * Created by huchang on 2017/11/16.
 */

public class Conversion {

    public static byte loUint16(short v) {
        return (byte) (v & 0xFF);
    }

    public static byte hiUint16(short v) {
        return (byte) (v >> 8);
    }

    public static short buildUint16(byte hi, byte lo) {
        return (short) ((hi << 8) + (lo & 0xff));
    }

    public static String BytetohexString(byte[] b, int len) {
        StringBuilder sb = new StringBuilder(b.length * (2 + 1));
        Formatter formatter = new Formatter(sb);

        for (int i = 0; i < len; i++) {
//            if (i < len - 1)
//                formatter.format("%02X", b[i]);
           // else
                formatter.format("%02X", b[i]);
        }
        formatter.close();
        return sb.toString();
    }
    public static String bytesToString(byte[] bytes) {
        final char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = hexArray[v >>> 4];
            hexChars[i * 2 + 1] = hexArray[v & 0x0F];

            sb.append(hexChars[i * 2]);
            sb.append(hexChars[i * 2 + 1]);
            sb.append(' ');
        }
        return sb.toString();
    }



    // Convert hex String to Byte
    public static int hexStringtoByte(String sb, byte[] results) {

        int i = 0;
        boolean j = false;

        if (sb != null) {
            for (int k = 0; k < sb.length(); k++) {
                if (((sb.charAt(k)) >= '0' && (sb.charAt(k) <= '9'))
                        || ((sb.charAt(k)) >= 'a' && (sb.charAt(k) <= 'f'))
                        || ((sb.charAt(k)) >= 'A' && (sb.charAt(k) <= 'F'))) {
                    if (j) {
                        results[i] += (byte) (Character.digit(sb.charAt(k), 16));
                        i++;
                    } else {
                        results[i] = (byte) (Character.digit(sb.charAt(k), 16) << 4);
                    }
                    j = !j;
                }
            }
        }
        return i;
    }
    public static byte[] stringToBytes(String s) {
        byte[] buf = new byte[s.length() / 2];
        for (int i = 0; i < buf.length; i++) {
            try {
                buf[i] = (byte) Integer.parseInt(s.substring(i * 2, i * 2 + 2),
                        16);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return buf;
    }

    /**
     * bytes字符串转换为Byte值
     * @param src String Byte字符串，每个Byte之间没有分隔符(字符范围:0-9 A-F)
     * @return byte[]
     */
    public static byte[] hexStr2Bytes(String src){
        /*对输入值进行规范化整理*/
        src = src.trim().replace(" ", "").toUpperCase(Locale.US);
        //处理值初始化
        int m=0,n=0;
        int iLen=src.length()/2; //计算长度
        byte[] ret = new byte[iLen]; //分配存储空间

        for (int i = 0; i < iLen; i++){
            m=i*2+1;
            n=m+1;
            ret[i] = (byte)(Integer.decode("0x"+ src.substring(i*2, m) + src.substring(m,n)) & 0xFF);
        }
        return ret;
    }

    public static boolean isAsciiPrintable(String str) {

        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (isAsciiPrintable(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    private static boolean isAsciiPrintable(char ch) {
        return ch >= 32 && ch < 127;
    }

    // 16进制转10进制
    public static int HexToInt(String strHex) {
        int nResult = 0;
        if (!IsHex(strHex))
            return nResult;
        String str = strHex.toUpperCase();
        if (str.length() > 2) {
            if (str.charAt(0) == '0' && str.charAt(1) == 'X') {
                str = str.substring(2);
            }
        }
        int nLen = str.length();
        for (int i = 0; i < nLen; ++i) {
            char ch = str.charAt(nLen - i - 1);
            try {
                nResult += (GetHex(ch) * GetPower(16, i));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return nResult;
    }

    // 计算16进制对应的数值
    public static int GetHex(char ch) throws Exception {
        if (ch >= '0' && ch <= '9')
            return (int) (ch - '0');
        if (ch >= 'a' && ch <= 'f')
            return (int) (ch - 'a' + 10);
        if (ch >= 'A' && ch <= 'F')
            return (int) (ch - 'A' + 10);
        throw new Exception("error param");
    }

    // 计算幂
    public static int GetPower(int nValue, int nCount) throws Exception {
        if (nCount < 0)
            throw new Exception("nCount can't small than 1!");
        if (nCount == 0)
            return 1;
        int nSum = 1;
        for (int i = 0; i < nCount; ++i) {
            nSum = nSum * nValue;
        }
        return nSum;
    }

    // 判断是否是16进制数
    public static boolean IsHex(String strHex) {
        int i = 0;
        if (strHex.length() > 2) {
            if (strHex.charAt(0) == '0'
                    && (strHex.charAt(1) == 'X' || strHex.charAt(1) == 'x')) {
                i = 2;
            }
        }
        for (; i < strHex.length(); ++i) {
            char ch = strHex.charAt(i);
            if ((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'F')
                    || (ch >= 'a' && ch <= 'f'))
                continue;
            return false;
        }
        return true;
    }

//    public static String getCrc(String txt) {
//        int high;
//        int flag;
//
//        // 16位寄存器，所有数位均为1
//        int wcrc = 0xffff;
//
//        byte[] data = hexStr2Bytes(txt);
//        LogHelper.d("getCrc++++data", Arrays.toString(data));
//
//        for (int i = 0; i < data.length; i++) {
//            // 16 位寄存器的高位字节
//            high = wcrc >> 8;
//            // 取被校验串的一个字节与 16 位寄存器的高位字节进行“异或”运算
//            wcrc = high ^ data[i];
//
//            for (int j = 0; j < 8; j++) {
//                flag = wcrc & 0x0001;
//                // 把这个 16 寄存器向右移一位
//                wcrc = wcrc >> 1;
//                // 若向右(标记位)移出的数位是 1,则生成多项式 1010 0000 0000 0001 和这个寄存器进行“异或”运算
//                if (flag == 1)
//                    wcrc ^= 0xa001;
//            }
//        }
//
//        return Integer.toHexString(wcrc);
//    }

    /**
     * 188协议获取Crc的值
     * @param data
     * @return
     */
    public static String getCrc(String data)
    {
        byte[] bytedata1 = hexStr2Bytes(data);
        String CrcAs="",CrcBs="";
        int CrcA = 0,CrcB = 0,CrcC = 0;

        CrcA = bytedata1[0]&0xFF;
        if(bytedata1.length>=2)
            CrcB = bytedata1[1]&0xFF;
        if(bytedata1.length>=3) {
            CrcC = bytedata1[2] & 0xFF;
            for (int i = 2; i < bytedata1.length; i++) {
                CrcC = (la00[CrcA] ^ CrcC) & 0xFF;
                CrcA = (ha00[CrcA] ^ CrcB) & 0xFF;
                CrcB = CrcC;
                if (i != bytedata1.length - 1)
                    CrcC = bytedata1[i + 1];
            }
        }

        CrcAs = Integer.toHexString(CrcA); //得到最终的CRC值
        CrcBs = Integer.toHexString(CrcB);
        if(CrcAs.length()<2)
            CrcAs = "0"+CrcAs;
        if(CrcBs.length()<2)
            CrcBs = "0"+CrcBs;
        data=CrcAs+CrcBs;
        LogHelper.d("++++CRCdata", data);
        return data.toUpperCase();
    }

    // *********************************************************
    // 对数据的功能性解析方法
    // 获取输入框十六进制格式
    private static String getHexString(String s) {
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

    /**
     * 将数据string 转换成十进制对应的ASCII码
     * 3938373635343332313534  --> 98765432154
     * @param str
     * @return
     */
    public static String StringToStringArrayToASCII(String str)
    {
        StringBuilder sb = new StringBuilder();

        int m=str.length()/2;
        if(m*2<str.length()){
            m++;
        }
        String[] strs=new String[m];
        int j=0;
        for(int i=0;i<str.length();i++){
            if(i%2==0){//每隔两个
                strs[j]=""+str.charAt(i);
            }else{
                strs[j]=strs[j]+str.charAt(i);//将字符加上两个空格
                j++;
            }
        }
        //System.out.println(Arrays.toString(strs));
        int code ;
        //code = Integer.parseInt(strs[0], 16);
        // System.out.println((char)code);
        for(int n=0;n<strs.length;n++)
        {
            code = Integer.parseInt(strs[n], 16);
            sb.append((char)code);
        }
        return sb.toString();
        //System.out.println(sb.toString());
    }

   //把对应的十进制的字符串转换成ASCII码的字符串 如 1 -> 31
    public static String StringToHEXAsciiString(String str)
    {
        StringBuilder sb = new StringBuilder();
        char[] strs = new char[str.length()];
        int j = 0;
        for(int i = 0;i<str.length();i++)
        {
            strs[j] =str.charAt(i);
            sb.append(Integer.toHexString(strs[j]));
            j++;
        }
        return sb.toString();

    }
    //在字符串后面补0
    public static String StringAddOne(String str,int len)
    {
        for(int i = 0;i<len;i++)
        {
            str = str+"0";
        }
        return str;
    }

    /**
     * 将一个字符串从后面两位开始倒序
     */

    public static String twoDataReverseOrder(String str)
    {
        StringBuffer sb = new StringBuffer();
        for (int i=str.length(); i>0; i-=2)
        {
            sb.append(str.substring(i-2,i));
        }

        return sb.toString();
    }

    /**
     * 十六进制字符串转换为十进制的字符串，最后两位是小数，其他位是整数，读取表读数使用
     * 00110000 ————> 17.0(倒序)
     * @param data
     * @return
     */

    public static  String readmeterdataanalynsis(String data)
    {
        int sum1 = 0,sum2 = 0;

        data = twoDataReverseOrder(data);
        String str1  =  data.substring(0,data.length()-2);
        System.out.println("str1   "+str1);
        String str2  =  data.substring(data.length()-2);
        System.out.println("str2   "+str2);

        sum1 = Integer.parseInt(str1,16);
        sum2 = Integer.parseInt(str2,16);
        System.out.println("sum1   "+Integer.toString(sum1));
        System.out.println("sum2   "+Integer.toString(sum2));

        data = Integer.toString(sum1)+"."+Integer.toString(sum2);

        return data;
    }
    public static  String readmeterTime(String time)
    {
        String time1= StringToStringArrayToASCII(time);
        time1 = time1.substring(0,4)+"-"+time1.substring(4,6)+"-"+time1.substring(6,8)+"-"+time1.substring(8,10)+"-"
                +time1.substring(10,12)+"-"+time1.substring(12,14);
        return time1;
    }

}
