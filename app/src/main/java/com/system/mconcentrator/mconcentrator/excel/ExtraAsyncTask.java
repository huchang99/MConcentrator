package com.system.mconcentrator.mconcentrator.excel;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.system.mconcentrator.mconcentrator.utils.LogHelper;

/**
 * Created by huchang on 2017/12/6.
 */

public class ExtraAsyncTask extends AsyncTask<Void,Integer,Boolean> {
    private final String Tag = "ExtraAsyncTask";

    private database dbExcel = null;
    //声明进度条对话框
    private ProgressDialog pdDialog=null;

    private Context context;

    private int icount = 0;

    //任务标志
    private String Taskflag;

    public ExtraAsyncTask(Context context,String Taskflag) {
       // super();
        this.context = context;
        this.Taskflag = Taskflag;

    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        Taskflag = null;
        pdDialog.dismiss();
    }

    @Override
    protected void onPreExecute() {

        icount = 0;
       // super.onPreExecute();
        LogHelper.d(Tag+"ExtraAsyncTask","start download");
        //ProgressDialog.show();
        // 创建ProgressDialog对象
        pdDialog = new ProgressDialog(context);

        // 设置进度条风格，风格为长形
        pdDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);


        if("exceltodb".equals(Taskflag)){
            // 设置ProgressDialog 标题
            pdDialog.setTitle("文件导入");
            // 设置ProgressDialog 提示信息
            pdDialog.setMessage("正在读取中……");
        }

        if("dbtoexcel".equals(Taskflag))
        {
            // 设置ProgressDialog 标题
            pdDialog.setTitle("文件导出");
            // 设置ProgressDialog 提示信息
            pdDialog.setMessage("正在导出……");
        }


        // 设置ProgressDialog 标题图标
       // pdDialog.setIcon(R.drawable.ic_launcher);

        // 设置ProgressDialog 进度条进度
        pdDialog.setProgress(100);

        // 设置ProgressDialog 的进度条是否不明确
        pdDialog.setIndeterminate(false);

        // 设置ProgressDialog 是否可以按退回按键取消
        pdDialog.setCancelable(true);

        //设置点击外框部分不取消
        pdDialog.setCanceledOnTouchOutside(false);

        // 让ProgressDialog显示
        pdDialog.show();

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        //super.onProgressUpdate(values);
        pdDialog.setProgress(values[0]);

    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        Log.d("doInBackground start","++++start");

        while(icount++<99)
        {
             publishProgress(icount);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if("exceltodb".equals(Taskflag))
        {
            dbExcel = new database();
            dbExcel.readExcelToDB();
        }
        if("dbtoexcel".equals(Taskflag)){
            database dbtoexcel = new database();
            dbtoexcel.DbToExcel();
        }
        publishProgress(icount);
        return null;
    }
}
