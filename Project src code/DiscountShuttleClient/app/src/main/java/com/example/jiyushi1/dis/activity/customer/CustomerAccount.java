package com.example.jiyushi1.dis.activity.customer;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jiyushi1.dis.R;
import com.example.jiyushi1.dis.ws.local.DownloadImage;
import com.example.jiyushi1.dis.ws.local.HttpAmazonImage;
import com.example.jiyushi1.dis.ws.local.HttpDBServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;


public class CustomerAccount extends Fragment {
    View view;

    Fragment frag;
    FragmentTransaction fragTran;
    private Handler handler;
    private ProgressDialog progressDialog;

    private Spinner gender ;
    private ArrayAdapter<CharSequence> typeSeection2;

    private TextView userName;
    private EditText retype;
    private EditText name;
    private EditText email;
    private EditText phoneNum;
    private Bundle bd;
    private ImageView photo;
    private File photoPath;
    private boolean imageNotChange = true;
    Uri imageUri;

    private boolean takephoto = true;
    private  File exploreFile =null;
    private  Map<String, String> params = new HashMap<String, String>();

    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_edit_customer_account, container, false);
        final AlertDialog alert = new AlertDialog.Builder(getActivity()).create();

        bd = getActivity().getIntent().getExtras();
        bd.putString("From","Customer");
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    photo.setImageBitmap((Bitmap)msg.obj);
                }
                if(msg.what == 2){
                    String Result = (String)msg.obj;
                    Toast.makeText(getActivity(), Result, Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    if(Result.compareTo("Your setting has been saved") == 0) {
                        frag = new CustomerMainPage();
                        fragTran = getFragmentManager().beginTransaction().replace(R.id.container, frag);
                        fragTran.commit();
                    }
                }
                if(msg.what == 3){
                    params.put("image", (String)msg.obj);
                    HttpConnectDB util = new HttpConnectDB(params, "utf-8", 7);
                    util.start();
                }
            }
        };


        String dealWithInfo = bd.getString("accountInfo");

        getActivity().getIntent().putExtras(bd);

        findPart();
        final String url = parseInfo(dealWithInfo);


        if (url.compareTo("null") != 0) {
            progressDialog = ProgressDialog.show(getActivity(),"Downloading","Downloading image from Sever");
            DownloadFromAM head = new DownloadFromAM(url);
            head.start();
        }
        else if (url.compareTo("Internet Disconnected") == 0){
            alert.setTitle("Whoops!");
            alert.setMessage("Can not connect to server");
            alert.show();
        }

        Button edit = (Button) view.findViewById(R.id.btCustomerAccountSubmit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = bd.getString("username");
                String nameStr = name.getText().toString();
                String emailStr = email.getText().toString();
                String phonenum = phoneNum.getText().toString();
                String genderStr = gender.getSelectedItem().toString();

                params.put("username", username);
                params.put("name", nameStr);
                params.put("email", emailStr);
                params.put("phonenum", phonenum);
                params.put("gender", genderStr);

                //upload image to amazon
                if ( !imageNotChange ) {
                    if (takephoto == true) {

                        if (photoPath != null) {
                            progressDialog = ProgressDialog.show(getActivity(),"Uploading","Uploading to Sever");
                            HttpConnectAM Send = new HttpConnectAM(photoPath);
                            Send.start();
                        } else {
                            params.put("image", url);
                            progressDialog = ProgressDialog.show(getActivity(),"Uploading","Uploading to Sever");
                            HttpConnectDB util = new HttpConnectDB(params, "utf-8", 7);
                            util.start();
                        }
                    } else {
                        progressDialog = ProgressDialog.show(getActivity(),"Uploading","Uploading to Sever");
                        HttpConnectAM Send = new HttpConnectAM(exploreFile);
                        Send.start();
                    }
                }
                else {

                    params.put("image", url);
                    progressDialog = ProgressDialog.show(getActivity(),"Uploading","Uploading to Sever");
                    HttpConnectDB util = new HttpConnectDB(params, "utf-8", 7);
                    util.start();
                }
            }
        });

        Button camera = (Button) view.findViewById(R.id.btCustomerAccountCamera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String photoName = time.format(new java.util.Date());


                Intent camera = new Intent("android.media.action.IMAGE_CAPTURE");
                photoPath = new File
                        (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                                ,photoName);
                imageUri = Uri.fromFile(photoPath);
                camera.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(camera, 2);

            }
        });

        Button changePassword = (Button) view.findViewById(R.id.btCustomerChangePassword);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                frag = new CustomerPassword();
                fragTran = getFragmentManager().beginTransaction().replace(R.id.container, frag);
                fragTran.commit();
            }
        });

        Button explore=(Button) view.findViewById(R.id.beCustomerAccountExplore);
        explore.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //System.out.println(directory);
                        try {
                            Intent intent =new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent, 1);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(),"error",Toast.LENGTH_LONG).show();
                            return;
                        }

                    }
                }
        );


        Button back = (Button) view.findViewById(R.id.btCustomerAccountCancel);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                frag = new CustomerMainPage();
                fragTran = getFragmentManager().beginTransaction().replace(R.id.container, frag);
                fragTran.commit();

            }
        });
        return view;

    }

    private void findPart() {
        String username = bd.getString("username");
        userName = (TextView) view.findViewById(R.id.customerAccountUsername);
        userName.setText(username);

        gender = (Spinner) view.findViewById(R.id.customerAccountGender);
        typeSeection2 = ArrayAdapter.createFromResource(getActivity(), R.array.Gender, android.R.layout.simple_spinner_dropdown_item);
        typeSeection2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(typeSeection2);

        name = (EditText) view.findViewById(R.id.customerAccountName);
        email = (EditText) view.findViewById(R.id.customerAccountEmail);
        phoneNum = (EditText) view.findViewById(R.id.customerAccountPhone);
        photo = (ImageView) view.findViewById(R.id.customerAccountImage);

    }

    private String parseInfo(String dealWithInfo) {
        String[] info = dealWithInfo.split("\n");
        name.setText(info[0]);
        String genderStr = info[1];
        if(genderStr.compareTo("Female") == 0){
            gender.setSelection(1);
        }
        email.setText(info[2]);
        phoneNum.setText(info[4]);
        Log.e("IMAGE",info[3]);
        return info[3];
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode) {
                case 1:
                    Uri uri = data.getData();
                    String path = getPathFromUri(getActivity(),uri);
                    exploreFile = new File(path);

                    try {
                        InputStream selectedImage = new FileInputStream(exploreFile);
                        Bitmap bm1 = BitmapFactory.decodeStream(selectedImage);
                        photo.setImageBitmap(bm1);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    takephoto = false;
                    imageNotChange = false;
                    break;

                case 2:
                    Uri selectedImage = imageUri;
                    getActivity().getContentResolver().notifyChange(selectedImage, null);
                    ContentResolver cr = getActivity().getContentResolver();
                    Bitmap bm;
                    try {
                        bm = MediaStore.Images.Media.getBitmap(cr, selectedImage);
                        photo.setImageBitmap(bm);
                        Toast.makeText(getActivity(), "Succeed", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Toast.makeText(getActivity(), "Failure", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    takephoto = true;
                    imageNotChange = false;
                    break;
            }
        }
        else {
            Toast.makeText(getActivity(), "You didn't take a photo.", Toast.LENGTH_LONG).show();
            photoPath = null;
        }
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
