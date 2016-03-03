package com.example.jiyushi1.dis.activity.seller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jiyushi1.dis.R;
import com.example.jiyushi1.dis.ws.local.HttpDBServer;

import java.util.HashMap;
import java.util.Map;

public class SellerPassword extends Activity {
    private TextView warnning;
    private EditText oldPassword;
    private EditText newPassword;
    private EditText retype;
    private Bundle bd;
    private Handler handler;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_password);
        oldPassword = (EditText) findViewById(R.id.etOldPassword);
        newPassword = (EditText) findViewById(R.id.etNewPassword);
        retype = (EditText) findViewById(R.id.etRetype);
        warnning =(TextView) findViewById(R.id.tvWarn);


        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 2){
                    String result = (String)msg.obj;
                    Toast.makeText(SellerPassword.this,result, Toast.LENGTH_LONG).show();
                    if (result.compareTo("Change Password Successful") == 0) {
                        finish();
                    }
                    progressDialog.dismiss();
                }
            }
        };

        retype.addTextChangedListener(retypeWatcher);

        Button submit = (Button) findViewById(R.id.btSellerPasswordSubmit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String oldP = oldPassword.getText().toString();
                String newP = newPassword.getText().toString();

                bd = getIntent().getExtras();
                String username = bd.getString("username");


                Map<String, String> params = new HashMap<String, String>();
                params.put("oldpassword", oldP);
                params.put("newpassword", newP);
                params.put("username", username);
                params.put("usertype", "Seller");

                progressDialog = ProgressDialog.show(SellerPassword.this, "Connecting", "Connecting to Server");
                HttpConnectDB util = new HttpConnectDB(params, "utf-8", 8);
                util.start();
            }
        });

        Button cancel = (Button) findViewById(R.id.btSellerPasswordCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });

    }

    TextWatcher retypeWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (newPassword.getText().toString().compareTo(retype.getText().toString()) != 0){
                warnning.setText("Not Same!");
            }
            else{
                warnning.setText("Good job!");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

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
}



