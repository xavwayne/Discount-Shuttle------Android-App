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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.jiyushi1.dis.R;
import com.example.jiyushi1.dis.ws.local.DownloadImage;
import com.example.jiyushi1.dis.ws.local.HttpAmazonImage;
import com.example.jiyushi1.dis.ws.local.HttpDBServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class SellerEditProduct extends Activity {

    Bundle bd;

    String username,oldProductname,newProductname;

    private EditText etname,etreg_price,etnow_price,etintro;

    private String reg_price,now_price,intro,image_url,image_path;

    private String directory,filename;

    private ImageView imageView;

    Button btUpload;
    private Handler handler;
    private ProgressDialog progressDialog;
    private String old_image_url;
    private boolean if_select = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    Bitmap bitmap=(Bitmap)msg.obj;
                    imageView.setImageBitmap(bitmap);
                    progressDialog.dismiss();
                }

                if(msg.what == 2){
                    String result= (String)msg.obj;
                    String res[]=result.split("\t");
                    if(res[0].equals("0")){
                        Toast.makeText(SellerEditProduct.this,"Erro Loading page!",Toast.LENGTH_LONG).show();
                        return;
                    }
                    oldProductname=res[1];
                    reg_price=res[2];
                    now_price=res[3];
                    intro=res[4];
                    old_image_url=res[5];
                    etname.setText(oldProductname);
                    etreg_price.setText(reg_price);
                    etnow_price.setText(now_price);
                    etintro.setText(intro);
                    image_url = old_image_url;
                    DownloadFromAM di=new DownloadFromAM(old_image_url);
                    di.start();
                }

                if(msg.what == 3){                       // After updating to Amazon
                    progressDialog.dismiss();
                    image_url=(String)msg.obj;
                    upload2DB();
                }

                if(msg.what == 4){
                    String result=(String)msg.obj;
                    String res[]=result.split("\t");
                    if(res[0].equals("0")){
                        String show=result.substring(result.indexOf("\t"),result.length());
                        Toast.makeText(SellerEditProduct.this,show,Toast.LENGTH_LONG).show();

                        return;
                    }
                    String show=result.substring(result.indexOf("\t"), result.length());
                    Toast.makeText(SellerEditProduct.this,show,Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    finish();
                }
            }
        };
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        bd = getIntent().getExtras();
        username = bd.getString("username");
        oldProductname=bd.getString("product");

        etname=(EditText)findViewById(R.id.etProductName_M);
        etreg_price=(EditText)findViewById(R.id.etRegularPrice_M);
        etnow_price=(EditText)findViewById(R.id.etPriceNow_M);
        etintro=(EditText)findViewById(R.id.etProIntro_M);

        imageView=(ImageView)findViewById(R.id.ivProImage_M);

        btUpload=(Button)findViewById(R.id.btUpload_M);

        btUpload.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        click(v);
                    }
                }
        );



        showInfo();

        Button edit = (Button) findViewById(R.id.btEditProSub);

        edit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                click(v);
            }
        });

        Button back = (Button) findViewById(R.id.btSelleEditProCancel);

        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                click(v);
            }
        });
    }

    public void showInfo()  {
        HashMap<String,String> para=new HashMap<String,String>();
        progressDialog = ProgressDialog.show(this,"Downloading","Reading data from Server");
        para.put("username",username);
        para.put("product_name", oldProductname);

        HttpConnectDB util=new HttpConnectDB(para,"utf-8",9,1);
        util.start();
    }



    public void  upload2Am(){

        if(image_path==null)
            return;
        File file =new File(image_path);

        HttpConnectAM am=new HttpConnectAM(file);
        am.start();
    }

    public void upload2DB(){

        newProductname=etname.getText().toString();
        reg_price=etreg_price.getText().toString();
        now_price=etnow_price.getText().toString();
        intro=etintro.getText().toString();

        if(newProductname.equals("")|| reg_price.equals("") || now_price.equals("") || intro .equals("")){
            Toast.makeText(this,"Please fill out the blank fields",Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog = ProgressDialog.show(SellerEditProduct.this, "Connecting", "Uploading setting to Server");
        HashMap<String,String> para=new HashMap<String,String>();

        para.put("username",bd.getString("username"));
        para.put("product_newname",newProductname);
        para.put("product_oldname",oldProductname);
        para.put("regular_price",reg_price);
        para.put("now_price",now_price);
        para.put("introduction", intro);
        para.put("image_url", image_url);

        HttpConnectDB util=new HttpConnectDB(para,"utf-8",10,2);
        util.start();
    }

    public void click(View v){
        switch (v.getId()){
            case R.id.btEditProSub:
                if(if_select) {
                    progressDialog = ProgressDialog.show(SellerEditProduct.this, "Uploading", "Uploading setting to Server");
                    upload2Am();
                }
                else{
                    upload2DB();
                }
                break;

            case R.id.btSelleEditProCancel:
                finish();
                break;

            case R.id.btUpload_M:
                if_select = true;
                Intent pick=new Intent(Intent.ACTION_PICK);
                pick.setType("image/*");
                startActivityForResult(pick, 1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "You did not choose a file.", Toast.LENGTH_LONG).show();
            return;
        }
        switch (requestCode) {
            case 1://get the path of the image

                Uri uri = data.getData();
                image_path = getPathFromUri(SellerEditProduct.this,uri);
                FileInputStream fi=null;
                try {
                    fi=new FileInputStream(image_path);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Bitmap bitmap= BitmapFactory.decodeStream(fi);
                imageView.setImageBitmap(bitmap);
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class DownloadFromAM extends DownloadImage {
        DownloadFromAM(String url){
            super(url);
        }
        @Override
        public void run() {
            super.run();
            Message message = Message.obtain();
            message.what = 1;
            message.obj = super.getBitmap();
            progressDialog.dismiss();
            handler.sendMessage(message);
        }
    }

    private class HttpConnectDB extends HttpDBServer {
        private int type;
        HttpConnectDB(Map<String, String> params, String encode,int requestType, int type){
            super(params,encode,requestType);
            this.type = type;
        }
        @Override
        public void run() {
            if(type == 1){
               super.run();
               Message message = Message.obtain();
               message.what = 2;
                Log.e("aaaaaaaa",super.getResult());
               message.obj = super.getResult();
               handler.sendMessage(message);
            }
            if(type == 2){
                super.run();
                Message message = Message.obtain();
                message.what = 4;
                message.obj = super.getResult();
                handler.sendMessage(message);
            }
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
            message.what = 3;
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
