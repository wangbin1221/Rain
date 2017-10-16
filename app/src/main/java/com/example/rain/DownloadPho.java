package com.example.rain;

/**
 * Created by Administrator on 2017/10/14.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/9/30.
 */

public class DownloadPho extends AsyncTask<String,Integer,Bitmap> {
    private ImageView mimageView;
    public DownloadPho(View view){
        mimageView = (ImageView)view;
    };

    @Override
    protected Bitmap doInBackground(String... params) {
        //return null;
        URL url = null;
        Bitmap bitmap = null;
        HttpURLConnection conn=null;
        //OkHttpClient okHttpClient = new OkHttpClient();
        InputStream is=null;
        try {
            url = new URL(params[0]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            /*Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            bitmap = response.body()*/
            is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(conn!=null){
                conn.disconnect();
                conn=null;
            }
            if(is!=null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                is=null;
            }
        }
        return bitmap;

    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        mimageView.setImageBitmap(bitmap);
    }
}