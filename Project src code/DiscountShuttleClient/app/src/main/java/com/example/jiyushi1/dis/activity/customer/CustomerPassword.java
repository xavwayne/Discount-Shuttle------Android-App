package com.example.jiyushi1.dis.activity.customer;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jiyushi1.dis.R;

import com.example.jiyushi1.dis.ws.local.HttpDBServer;


import java.util.HashMap;
import java.util.Map;


public class CustomerPassword extends Fragment {

    View view;
    Fragment frag;
    FragmentTransaction fragTran;

    private TextView warnning;
    private EditText oldPassword;
    private EditText newPassword;
    private EditText retype;

    private Handler handler;
    private ProgressDialog progressDialog;
    private String from;
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_password_customer,container,false);

        oldPassword = (EditText) view.findViewById(R.id.changeOldPassword);
        newPassword = (EditText) view.findViewById(R.id.changeNewPassword);
        retype = (EditText) view.findViewById(R.id.changeRetype);
        warnning =(TextView) view.findViewById(R.id.changePasswordWarrning);

        retype.addTextChangedListener(retypeWatcher);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 2){
                    String result = (String)msg.obj;
                    Toast.makeText(getActivity(),result, Toast.LENGTH_LONG).show();
                    if (result.compareTo("Change Password Successful") == 0) {
                            frag = new CustomerAccount();
                            fragTran = getFragmentManager().beginTransaction().replace(R.id.container, frag);
                            fragTran.commit();

                    }
                    progressDialog.dismiss();
                }
            }
        };

        Button submit = (Button) view.findViewById(R.id.btCustomerPasswordSubmit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String oldP = oldPassword.getText().toString();
                String newP = newPassword.getText().toString();

                if (newP.length() != 0) {
                    Bundle bd = getActivity().getIntent().getExtras();
                    String username = bd.getString("username");
                    from = bd.getString("From");

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("oldpassword", oldP);
                    params.put("newpassword", newP);
                    params.put("username", username);
                    params.put("usertype", from);
                    progressDialog = ProgressDialog.show(getActivity(), "Connecting", "Connecting to Server");
                    HttpConnectDB util = new HttpConnectDB(params, "utf-8", 8);
                    util.start();
                }
                else {
                    Toast.makeText(getActivity(),"Password can not be null", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button cancel = (Button) view.findViewById(R.id.btCustomerPasswordCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    frag = new CustomerAccount();
                    fragTran = getFragmentManager().beginTransaction().replace(R.id.container, frag);
                    fragTran.commit();


            }
        });
        return view;
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

