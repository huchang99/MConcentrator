package com.system.mconcentrator.mconcentrator.utils;

/**
 * Created by huchang on 2017/9/29.
 */

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Time;

/**
 * 自定义Log输出
 *
 * @author Administrator
 *
 * @version 1.1.0 1、对代码按照规范进行编写 2、增加写入到文件功能,包括执行所在行数、时间、TAG(类名)、信息
 * @author dzyssssss 2013.07.05
 *
 * @PS: <!-- 往SDCard写入数据权限 --> <uses-permission
 *      android:name="android.permission.WRITE_EXTERNAL_STORAGE" /> <!--
 *      在SDCard中创建与删除文件权限 --> <uses-permission
 *      android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />
 *
 */

public class LogHelper {


    /** 是否输出日志 */
    public static final boolean IS_PRINT = true;

    /** 返回值 */
    private static final int RETURE = 6;

    private LogHelper() {
    }

    /**
     * 黑色字样，任何信息都输出
     *
     */
    public static int v(String paramTag, String paramMsg) {

        try {
            if (IS_PRINT) {
                return android.util.Log.v(paramTag, paramMsg);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return RETURE;
    }

    /**
     * 输出信息和输出的异常
     *
     * @return
     */
    public static int v(String paramTag, String paramMsg, Throwable paramTr) {

        try {
            if (IS_PRINT) {
                return android.util.Log.v(paramTag, paramMsg, paramTr);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return RETURE;
    }

    /**
     * 字体为蓝色，仅输出debug调试的意思，过滤起来可以通过DDMS的Logcat标签来选择.
     *
     * @return
     */
    public static int d(String paramTag, String paramMsg) {

        try {
            if (IS_PRINT) {
                return android.util.Log.d(paramTag, paramMsg);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return RETURE;
    }

    /**
     * 调试，日志信息和异常信息
     *
     * @return
     */
    public static int d(String paramTag, String paramMsg, Throwable paramTr) {

        try {
            if (IS_PRINT) {
                return android.util.Log.d(paramTag, paramMsg, paramTr);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return RETURE;
    }

    /**
     * 绿色字体，一般提示性的消息information，它不会输出Log.v和Log.d的信息，但会显示i、w和e的信息
     *
     * @return
     */
    public static int i(String paramTag, String paramMsg) {

        try {
            if (IS_PRINT) {
                return android.util.Log.i(paramTag, paramMsg);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return RETURE;
    }

    /**
     * 日志信息和异常信息
     *
     * @return
     */
    public static int i(String paramTag, String paramMsg, Throwable paramTr) {

        try {
            if (IS_PRINT) {
                return android.util.Log.i(paramTag, paramMsg, paramTr);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return RETURE;
    }

    /**
     * 橙色，警告 优化Android代码，会输出log.e的信息
     *
     */
    public static int w(String paramTag, String paramMsg) {

        try {
            if (IS_PRINT) {
                return android.util.Log.w(paramTag, paramMsg);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return RETURE;
    }

    /**
     * 警告，日志信息和日志异常
     *

     * @return
     */
    public static int w(String paramTag, String paramMsg, Throwable paramTr) {
        try {
            if (IS_PRINT)
                return android.util.Log.w(paramTag, paramMsg, paramTr);
        } catch (Exception e) {
        }
        return RETURE;
    }

    /**
     * 红色，错误信息
     *
     */
    public static int e(String paramTag, String paramMsg) {
        try {
            if (IS_PRINT)
                return android.util.Log.e(paramTag, paramMsg);
        } catch (Exception e) {
        }
        return RETURE;
    }




}
