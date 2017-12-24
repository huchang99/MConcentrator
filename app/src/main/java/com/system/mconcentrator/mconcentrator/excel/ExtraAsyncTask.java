package com.system.mconcentrator.mconcentrator.excel;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by huchang on 2017/12/6.
 */

public class ExtraAsyncTask extends AsyncTask<Void,Integer,Boolean> {

    private database dbExcel = null;

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        Log.d("doInBackground start","++++start");

        dbExcel = new database();
        dbExcel.readExcelToDB();
        return null;
    }
}
