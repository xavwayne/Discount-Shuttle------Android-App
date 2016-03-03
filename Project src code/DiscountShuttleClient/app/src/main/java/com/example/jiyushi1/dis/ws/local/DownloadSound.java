package com.example.jiyushi1.dis.ws.local;

import android.net.Uri;

/**
 * Created by jiyushi1 on 7/29/15.
 */
public class DownloadSound {
    private String url_s;
    private Uri uri;

    public DownloadSound(String url){
        this.url_s = url;
        uri = Uri.parse(url);
    }

    public Uri getUri(){
        return uri;
    }
}
