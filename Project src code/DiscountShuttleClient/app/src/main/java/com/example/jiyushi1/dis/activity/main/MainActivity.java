package com.example.jiyushi1.dis.activity.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.jiyushi1.dis.R;
import com.example.jiyushi1.dis.activity.customer.CustomerNavigation;
import com.example.jiyushi1.dis.activity.guest.GuestLogin;


import com.example.jiyushi1.dis.activity.seller.SellerMainPage;
import com.example.jiyushi1.dis.ws.local.HttpDBServer;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends Activity {

    private TextView sign;
    private TextView guestLogin;

    private ArrayAdapter<CharSequence> typeSeection;
    private Spinner type;
    private EditText userName;
    private EditText passWord;
    private ProgressDialog progressDialog;
    private Handler handler;

    String userType;
    String username;
    String password;
    String usertype;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userName = (EditText) findViewById(R.id.logUserName);
        passWord = (EditText) findViewById(R.id.logPassword);

        type = (Spinner) findViewById(R.id.logUsertype);
        typeSeection = ArrayAdapter.createFromResource(this, R.array.UserType, android.R.layout.simple_spinner_dropdown_item);
        typeSeection.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(typeSeection);

        sign = (TextView) findViewById(R.id.signUp);
        guestLogin = (TextView) findViewById(R.id.guestLogin);
        Button login = (Button) findViewById(R.id.btMainLogin);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    String Result = (String) msg.obj;
                    Toast.makeText(MainActivity.this, Result, Toast.LENGTH_LONG).show();

                    if (Result.compareTo("You have logged in successfully") == 0) {

                        if (userType.compareTo("Customer") == 0) {

                            Intent logToCustomer = new Intent(MainActivity.this, CustomerNavigation.class);

                        /*    bundle username as pk    */
                            Bundle bd = new Bundle();
                            bd.putString("username",username);
                            logToCustomer.putExtras(bd);

                            startActivity(logToCustomer);
                        } else {

                            Intent toGuest = new Intent(MainActivity.this, SellerMainPage.class);

                        /*    bundle username as pk    */

                            Bundle bd = new Bundle();
                            bd.putString("username",username);
                            toGuest.putExtras(bd);

                            startActivity(toGuest);
                        }
                    }

                }
            }
        };

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logToSignUp = new Intent(MainActivity.this, SignUpMain.class);
                startActivity(logToSignUp);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userType = type.getSelectedItem().toString();

                username = userName.getText().toString();
                password = passWord.getText().toString();
                usertype = type.getSelectedItem().toString();

                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);
                params.put("usertype", usertype);

                progressDialog = ProgressDialog.show(MainActivity.this,"Connecting","Trying to connect to the server");
                HttpConnectDB util = new HttpConnectDB(params, "utf-8",3);
                util.start();
            }
        });
        guestLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSeller = new Intent(MainActivity.this, GuestLogin.class);
                startActivity(toSeller);
            }
        });
    }

    private class HttpConnectDB extends HttpDBServer{
        HttpConnectDB(Map<String, String> params, String encode,int requestType){
            super(params,encode,requestType);
        }
        @Override
        public void run() {
            super.run();
            progressDialog.dismiss();
            Message message = Message.obtain();
            message.what = 1;
            message.obj = super.getResult();
            handler.sendMessage(message);
        }
    }

    @Override
    public void onBackPressed() {

        Intent i= new Intent(Intent.ACTION_MAIN);

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        i.addCategory(Intent.CATEGORY_HOME);

        startActivity(i);
    }


}