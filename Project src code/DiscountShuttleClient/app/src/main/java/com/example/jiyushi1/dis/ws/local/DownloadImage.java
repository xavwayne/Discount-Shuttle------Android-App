package com.example.jiyushi1.dis.ws.local;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jiyushi1 on 7/26/15.
 */
public class DownloadImage extends Thread{
    private String url_s;
    private Bitmap bitmap;

    public DownloadImage(String url){
        this.url_s = url;
    }

    public void run(){
        try {
            URL url = new URL(url_s);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();
            InputStream input = httpURLConnection.getInputStream();
            bitmap = BitmapFactory.decodeStream(input);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getBitmap(){
        return bitmap;
    }
}


