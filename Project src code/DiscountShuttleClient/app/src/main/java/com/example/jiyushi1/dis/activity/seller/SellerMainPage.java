package com.example.jiyushi1.dis.activity.seller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.jiyushi1.dis.R;
import com.example.jiyushi1.dis.activity.main.MainActivity;
import com.example.jiyushi1.dis.ws.local.HttpDBServer;

import java.util.HashMap;
import java.util.Map;

public class SellerMainPage extends Activity {

    private HttpConnectDB util;
    private ProgressDialog progressDialog;
    private Handler handler;
    Map<String, String> params;
    Bundle bd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page_seller);


        bd = getIntent().getExtras();
        String username = bd.getString("username");


        Button edit = (Button) findViewById(R.id.btSellerEditDiscount);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click(v);
            }
        });


        Button post = (Button) findViewById(R.id.btPostNew);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click(v);
            }
        });

        Button logout = (Button) findViewById(R.id.btSellerLogout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click(v);
            }
        });

        Button account = (Button) findViewById(R.id.btSellerAccount);
        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click(v);
            }
        });

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {

                if(msg.what == 1){
                    String accountInfo = util.getResult();
                    bd.putString("accountInfo",accountInfo);

                    Intent intent4=new Intent(SellerMainPage.this,SellerAccount.class);
                    intent4.putExtras(bd);

                    progressDialog.dismiss();
                    startActivity(intent4);

                }


            }
        };


    }

    public void click(View v){
        switch (v.getId()){
            case R.id.btSellerEditDiscount:
                Intent intent=new Intent(SellerMainPage.this,SellerDiscountManage.class);
                intent.putExtras(bd);
                startActivity(intent);
                break;
            case R.id.btPostNew:
                Intent intent2=new Intent(SellerMainPage.this,SellerPostNew.class);
                intent2.putExtras(bd);
                startActivity(intent2);
                break;

            case R.id.btSellerLogout:
                finish();
                break;

            case R.id.btSellerAccount:

                String username = bd.getString("username");
                params = new HashMap<String, String>();
                params.put("username", username);

                progressDialog = ProgressDialog.show(this, "Connecting", "Connecting to server");
                /* given the user name, get the url of uploaded image */
                util = new HttpConnectDB(params, "utf-8",21);
                util.start();
                break;

            default:
                return;

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
            message.what = 1;
            message.obj = super.getResult();
            handler.sendMessage(message);
        }
    }


    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure to LOG OFF?");
        builder.setTitle("Hey Dude:");
        builder.setPositiveButton("Sure",
                new android.content.DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        SellerMainPage.this.finish();
                    }
                });

        builder.setNegativeButton("Cancel",
                new android.content.DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }
}
