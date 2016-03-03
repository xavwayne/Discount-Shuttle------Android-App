package com.example.jiyushi1.dis.activity.customer;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jiyushi1.dis.R;
import com.example.jiyushi1.dis.activity.main.MainActivity;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CustomerSignUp extends Activity {

    private boolean takephoto = true;

    private Spinner gender ;
    private ArrayAdapter<CharSequence> typeSeection2;
    private EditText userName;
    private EditText passWord;
    private EditText retype;
    private EditText name;
    private EditText email;
    private EditText phoneNum;
    private TextView warnning;
    private String path;

    private Map<String, String> params = new HashMap<String, String>();
    private Handler handler;
    private ProgressDialog progressDialog;
    private HttpConnectDB util;

    //image to amazon and url to db
    private ImageView imageView;
    private File photoPath;
    private  File exploreFile =null;
    //take a photo
    Uri imageUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final AlertDialog alert = new AlertDialog.Builder(this).create();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_sign_up);


        imageView = (ImageView) findViewById(R.id.imageView);
/*
        Directorypath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        FileName = "image.png";
        RemoteName = "remoteImage.png";
*/
        /*two   spinner*/

        gender = (Spinner) findViewById(R.id.genderSpinner);
        typeSeection2 = ArrayAdapter.createFromResource(this, R.array.Gender, android.R.layout.simple_spinner_dropdown_item);
        typeSeection2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(typeSeection2);

        userName = (EditText) findViewById(R.id.signUserName);
        passWord = (EditText) findViewById(R.id.signPassword);
        retype = (EditText) findViewById(R.id.retype);
        name = (EditText) findViewById(R.id.signName);
        email = (EditText) findViewById(R.id.email);
        phoneNum = (EditText) findViewById(R.id.telephone);
        warnning = (TextView) findViewById(R.id.passwrodWarnning);

        Button customerSign = (Button) findViewById(R.id.btCustomerSingUp);
        Button customerCancel = (Button) findViewById(R.id.btCustomerCancel);

        retype.addTextChangedListener(retypeWatcher);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    params.put("image", (String)msg.obj);
                    util = new HttpConnectDB(params, "utf-8",1);
                    util.start();
                }
                if(msg.what == 2){
                    progressDialog.dismiss();
                    Toast.makeText(CustomerSignUp.this, util.getResult(), Toast.LENGTH_LONG).show();
                    if (util.getResult().compareTo("Your account has been created successfully") == 0){
                        Intent signSubmit = new Intent(CustomerSignUp.this, MainActivity.class);
                        startActivity(signSubmit);
                    }
                    else {
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }

                    /*
                    alert.setTitle("Result");
                    alert.setMessage(util.getResult());
                    alert.show();
                    */
                }
            }
        };

        Button photo = (Button) findViewById(R.id.btCustomerPhoto);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String photoName = time.format(new java.util.Date()) ;


                Intent camera = new Intent("android.media.action.IMAGE_CAPTURE");
                photoPath = new File
                        (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                                ,photoName);
                imageUri = Uri.fromFile(photoPath);
                camera.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(camera,2);

            }
        });

        Button explore=(Button) findViewById(R.id.btCustomerSignExplore);

        explore.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent =new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent, 1);

                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }

                    }
                }
        );


        customerSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = userName.getText().toString();
                String password = passWord.getText().toString();
                String nameStr = name.getText().toString();
                String emailStr = email.getText().toString();
                String phonenum = phoneNum.getText().toString();
                String genderStr = gender.getSelectedItem().toString();

                String regex1 = "[\\w]+@[\\w]+.[\\w]+";
                String regex2 = "[\\w]+@[\\w]+.[\\w]+.[\\w]+";
                Pattern emailP1 = Pattern.compile(regex1);
                Pattern emailP2 = Pattern.compile(regex2);
                Matcher emailM1 = emailP1.matcher(emailStr);
                Matcher emailM2 = emailP2.matcher(emailStr);
                boolean emailB1 = emailM1.matches();
                boolean emailB2 = emailM2.matches();


                if (passWord.getText().toString().compareTo(retype.getText().toString()) != 0) {
                    String message = "Your password does't match!";
                    Toast.makeText(CustomerSignUp.this, message, Toast.LENGTH_LONG).show();
                }
                else if (emailStr.length() == 0 || password .length() ==0 ||username.length() == 0||
                        nameStr.length() == 0 || phonenum.length() == 0
                         ){
                    Toast.makeText(CustomerSignUp.this, "You should fill All the blanks!", Toast.LENGTH_LONG).show();
                }
                else if (!emailB1 && !emailB2){
                    Toast.makeText(CustomerSignUp.this, "You should input a correct email address!", Toast.LENGTH_LONG).show();
                }
                else if ( password .length() >20 ||username.length() >20||
                        nameStr.length() >20 || phonenum.length() >20){
                    Toast.makeText(CustomerSignUp.this,
                            "All parts should be no more than 20 characters.", Toast.LENGTH_LONG).show();
                }
                else {
                    //need to connect to server and uphold the form


                    params.put("username", username);
                    params.put("password", password);
                    params.put("name", nameStr);
                    params.put("email", emailStr);
                    params.put("phonenum", phonenum);
                    params.put("gender", genderStr);
                    params.put("usertype", "Customer");

                    //upload image to amazon
                    if (takephoto == true) {
                        if (photoPath != null) {
                            progressDialog = ProgressDialog.show(CustomerSignUp.this,"Connecting","Connecting to server");
                            HttpConnectAM Send = new HttpConnectAM(photoPath);
                            Send.start();
                            //DownloadImage image = new DownloadImage(Send.getUrl());

                        } else {
                            params.put("image", "Null");
                            progressDialog = ProgressDialog.show(CustomerSignUp.this,"Connecting","Connecting to server");
                            util = new HttpConnectDB(params,"utf-8",1);
                            util.start();
                        }
                    }
                    else {
                        progressDialog = ProgressDialog.show(CustomerSignUp.this,"Connecting","Connecting to server");
                        HttpConnectAM Send = new HttpConnectAM(exploreFile);
                        Send.start();
                    }
                }
            }
        });



        customerCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signCancel = new Intent(CustomerSignUp.this, MainActivity.class);
                startActivity(signCancel);
            }
        });
    }


    TextWatcher retypeWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (passWord.getText().toString().compareTo(retype.getText().toString()) != 0){
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case 1:
                    Uri uri = data.getData();
                    String path = getPathFromUri(CustomerSignUp.this,uri);
                    exploreFile = new File(path);

                    try {
                        InputStream selectedImage = new FileInputStream(exploreFile);
                        Bitmap bm1 = BitmapFactory.decodeStream(selectedImage);
                        imageView.setImageBitmap(bm1);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    takephoto = false;
                    break;

                case 2:
                    Uri selectedImage = imageUri;
                    getContentResolver().notifyChange(selectedImage, null);
                    ContentResolver cr = getContentResolver();
                    Bitmap bm;
                    try {
                        bm = MediaStore.Images.Media.getBitmap(cr,selectedImage);
                        imageView.setImageBitmap(bm);
                        Toast.makeText(this,"Succeed", Toast.LENGTH_LONG).show();
                        return;
                    } catch (IOException e) {
                        Toast.makeText(this,"Failure", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    takephoto = true;
                    break;

            }
        }
        else {
            Toast.makeText(this, "You didn't take or choose a photo.", Toast.LENGTH_LONG).show();
            photoPath = null;
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

    private class HttpConnectDB extends HttpDBServer{
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

