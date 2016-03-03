package com.example.jiyushi1.dis.activity.seller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.jiyushi1.dis.R;
import com.example.jiyushi1.dis.ws.local.HttpAmazonImage;
import com.example.jiyushi1.dis.ws.local.HttpDBServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;


public class SellerPostNew extends Activity {


    private Bundle bd;
    private Button sub,cancel,upload;

    private EditText etname,etreg_price,etnow_price,etintro;

    private String name,reg_price,now_price,intro,image_url,image_path;
    private String directory,filename;
    private Handler handler;
    private ProgressDialog progressDialog;
    private HashMap<String,String> para=new HashMap<String,String>();

    ImageView ivPro;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_product);
         bd = getIntent().getExtras();
         String username = bd.getString("username");

        sub= (Button) findViewById(R.id.btPostSub);
        cancel= (Button) findViewById(R.id.btSellePostCancel);
        upload= (Button) findViewById(R.id.btUpload);

        etname=(EditText) findViewById(R.id.etProductName);
        etreg_price=(EditText) findViewById(R.id.etRegularPrice);
        etnow_price=(EditText) findViewById(R.id.etPriceNow);
        etintro=(EditText) findViewById(R.id.etProIntro);

        ivPro=(ImageView)findViewById(R.id.ivProImage);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    image_url=(String)msg.obj;
                    para.put("image_url",image_url);
                    HttpConnectDB util=new HttpConnectDB(para,"utf-8",12);
                    util.start();
                }
                if(msg.what == 2){
                    String result=(String)msg.obj;
                    String res[]=result.split("\t");
                    String show=result.substring(result.indexOf("\t"),result.length());
                    Toast.makeText(SellerPostNew.this,show,Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    if(!res[0].equals("1"))
                        return;
                    finish();
                }
            }
        };

        upload.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 click(v);
             }
         });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click(v);
            }
        });
        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click(v);
            }
        });
    }



    public void click(View v){
        switch (v.getId()){
            case R.id.btPostSub:

                name=etname.getText().toString();
                reg_price=etreg_price.getText().toString();
                now_price=etnow_price.getText().toString();
                intro=etintro.getText().toString();

                if(name.equals("")|| reg_price.equals("") || now_price.equals("") || intro .equals("")){
                    Toast.makeText(SellerPostNew.this,"Please fill out the blank fields",Toast.LENGTH_SHORT).show();
                    return;
                }
                para.put("username",bd.getString("username"));
                para.put("product_name", name);
                para.put("regular_price", reg_price);
                para.put("now_price",now_price);
                para.put("introduction",intro);

                progressDialog = ProgressDialog.show(this,"Uploading","Connecting to server");
                //upload image to amazon
                if(image_path!=null) {
                    File up = new File(image_path);
                    HttpConnectAM am = new HttpConnectAM(up);
                    am.start();
                }
                else{
                    image_url = "null";
                    para.put("image_url",image_url);
                    HttpConnectDB util=new HttpConnectDB(para,"utf-8",12);
                    util.start();
                }
                break;

            case R.id.btSellePostCancel:
                finish();
                break;

            case R.id.btUpload:
                Intent pick=new Intent(Intent.ACTION_PICK);
                pick.setType("image/*");
                startActivityForResult(pick,1);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode!=Activity.RESULT_OK){
            Toast.makeText(this,"You did not choose a file.",Toast.LENGTH_LONG).show();
            return;
        }
        switch (requestCode){
            case 1://get the path of the image, then upload it to amazon and then get the cloud url.
//                String result = data.getExtras().getString("result");
//                Toast.makeText(this,result,Toast.LENGTH_LONG).show();
                Uri uri = data.getData();
                image_path=getPathFromUri(SellerPostNew.this,uri);
                FileInputStream fi=null;
                try {
                    fi = new FileInputStream(image_path);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Bitmap bitmap= BitmapFactory.decodeStream(fi);
                ivPro.setImageBitmap(bitmap);

                break;
        }
    }

    private class HttpConnectDB extends HttpDBServer {
        HttpConnectDB(Map<String, String> params, String encode,int requestType){
            super(params,encode,requestType);
        }
        @Override
        public void run() {
            super.run();
            Message message = Message.obtain();
            message.what = 2;
            message.obj = super.getResult();
            handler.sendMessage(message);
        }
    }

    private class HttpConnectAM extends HttpAmazonImage{
        HttpConnectAM(File file){
            super(file);
        }
        @Override
        public void run() {
            super.run();
            Message message = Message.obtain();
            message.what = 1;
            message.obj = super.getUrl();
            handler.sendMessage(message);
        }
    }

    public String getPathFromUri(Context context, Uri uri) {
        Cursor cursor = null;
        try {
            String[] projection = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(uri,  projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
