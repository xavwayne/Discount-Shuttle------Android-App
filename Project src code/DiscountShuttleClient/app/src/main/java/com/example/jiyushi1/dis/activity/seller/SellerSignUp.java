package com.example.jiyushi1.dis.activity.seller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
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
import com.example.jiyushi1.dis.ws.local.HttpAmzonAudio;
import com.example.jiyushi1.dis.ws.local.HttpDBServer;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SellerSignUp extends Activity {

    private GoogleMap mMap;
    private int threads_count = 0;
    private int length = 2;
    private boolean takephoto = true;
    private String path;
    private  File exploreFile =null;
    //private Spinner gender ;
    private Spinner shopType ;
    private EditText userName;
    private EditText passWord;
    private EditText retype;
    private EditText name;
    private EditText email;
    private EditText phoneNum;
    private TextView warnning;
    private TextView shopName;

    private ProgressDialog progressDialog;
    private Handler handler;
    private   Map<String, String> params = new HashMap<String, String>();
    private HttpConnectDB util;

    private String myLongitude;
    private String myLatitude;
    private String destLongitude;
    private String destLatitude;
    private EditText introduction;
    //take a photo

    private MediaRecorder recorder;
    private MediaPlayer player;
    private String outfile = null; //this is the path to the rec file
    boolean flag=false;

    private File photoPath = null;
    Uri imageUri;
    private ImageView imageView;

    private ArrayAdapter<CharSequence> typeSeection3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_sign_up);


/*
        gender = (Spinner) findViewById(R.id.sellerGenderSpinner);
        typeSeection2 = ArrayAdapter.createFromResource(this, R.array.Gender, android.R.layout.simple_spinner_dropdown_item);
        typeSeection2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(typeSeection2);
*/
        shopType = (Spinner) findViewById(R.id.shopTypeSpinner);
        typeSeection3 = ArrayAdapter.createFromResource(this, R.array.ItemType, android.R.layout.simple_spinner_dropdown_item);
        typeSeection3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shopType.setAdapter(typeSeection3);

        userName = (EditText) findViewById(R.id.sellerSignUserName);
        passWord = (EditText) findViewById(R.id.sellerSignPassword);
        retype = (EditText) findViewById(R.id.sellerRetype);
        name = (EditText) findViewById(R.id.sellerSignName);
        email = (EditText) findViewById(R.id.sellerSignEmail);
        phoneNum = (EditText) findViewById(R.id.sellerSignPhone);
        warnning = (TextView) findViewById(R.id.sellerPasswrodWarnning);
        shopName = (EditText) findViewById(R.id.sellerSignShopName);
        imageView = (ImageView) findViewById(R.id.sellerImageView);
        introduction = (EditText) findViewById(R.id.sellerSignIntroduction);

        Button customerSign = (Button) findViewById(R.id.btSellerSingUp);
        Button customerCancel = (Button) findViewById(R.id.btSellerCancel);
        Button btRec = (Button) findViewById(R.id.btSellerSignRecord);
        Button btPlay = (Button) findViewById(R.id.btSellerSignPlay);

        btRec.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        /*
                        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
                        String videoName = time.format(new java.util.Date()) +".3gpp";
                        */
                        outfile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath()
                                + "/Temp.3gpp";
                        record();
                        //Toast.makeText(SellerSignUp.this, "Recording...", Toast.LENGTH_LONG).show();
                        return true;
                    }
                }
        );

        btRec.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        switch (event.getAction()) {


                            case MotionEvent.ACTION_UP:
                                stopRec();
                                //Toast.makeText(SellerSignUp.this, "Recorded!", Toast.LENGTH_LONG).show();
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                }
        );

        btPlay.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                playBack();
                                break;

                            case MotionEvent.ACTION_UP:
                                stopPlay();
                                break;
                            default:
                                break;
                        }
                        return false;

                    }
                }
        );
        retype.addTextChangedListener(retypeWatcher);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    params.put("image", (String) msg.obj);

                }

                if(msg.what == 2){
                    Toast.makeText(SellerSignUp.this, util.getResult(), Toast.LENGTH_LONG).show();
                    if (util.getResult().compareTo("Your account has been created successfully") == 0) {
                        Intent signSubmit = new Intent(SellerSignUp.this, MainActivity.class);
                        startActivity(signSubmit);
                    }
                    else{
                        Intent intent=getIntent();
                        finish();
                        startActivity(intent);
                    }
                }

                if(msg.what == 3){
                    params.put("audio", (String)msg.obj);
                }

                if (msg.what == 8){

                    util = new HttpConnectDB(params, "utf-8",2);
                    util.start();
                    progressDialog.dismiss();

                }
            }
        };





        Button photo = (Button) findViewById(R.id.btSellerCamera);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat time = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                String photoName = time.format(new java.util.Date()) +".jpg";


                Intent camera = new Intent("android.media.action.IMAGE_CAPTURE");
                photoPath = new File
                        (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                                ,photoName);
                imageUri = Uri.fromFile(photoPath);
                camera.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(camera,2);

            }
        });

        Button explore=(Button) findViewById(R.id.btSellerSignExplore);
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
                            Toast.makeText(SellerSignUp.this,"error",Toast.LENGTH_LONG).show();
                            return;
                        }

                    }
                }
        );

        customerSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (outfile != null){

                String username = userName.getText().toString();
                String password = passWord.getText().toString();
                String nameStr = name.getText().toString();
                String emailStr = email.getText().toString();
                String phonenum = phoneNum.getText().toString();
                String intro = introduction.getText().toString();
                String shopNameStr = shopName.getText().toString();
                String shopTypeStr = shopType.getSelectedItem().toString();
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
                    Toast.makeText(SellerSignUp.this, message, Toast.LENGTH_LONG).show();
                }
                else if (emailStr.length() == 0 || password .length() ==0 ||username.length() == 0||
                nameStr.length() == 0 || phonenum.length() == 0|| intro.length() == 0
                ||shopNameStr.length() == 0 ){
                    Toast.makeText(SellerSignUp.this, "You should fill All the blanks!", Toast.LENGTH_LONG).show();
                }
                else if (!emailB1 && !emailB2){
                    Toast.makeText(SellerSignUp.this, "You should input a correct email address!", Toast.LENGTH_LONG).show();
                }
                else if (emailStr.length() > 20 || password .length() > 20 ||username.length() > 20||
                        nameStr.length() > 20|| phonenum.length() > 20|| intro.length() > 30
                        ||shopNameStr.length() > 20){
                    Toast.makeText(SellerSignUp.this, "Introduction's length should be no more than 30" +
                            " characters, and other parts should be no more than 20 characters.", Toast.LENGTH_LONG).show();
                }
                else if(emailStr.indexOf("\'") > 0 || password.indexOf("\'") > 0 ||username.indexOf("\'") > 0||
                        nameStr.indexOf("\'") > 0 || phonenum.indexOf("\'") > 0|| intro.indexOf("\'") > 0
                        ||shopNameStr.indexOf("\'") > 0 ){
                    Toast.makeText(SellerSignUp.this, "\' is not allowed in all blanks", Toast.LENGTH_LONG).show();
                }
                else {
                    //need to connect to server and uphold the form

                    params.put("username", username);
                    params.put("password", password);
                    params.put("name", nameStr);
                    params.put("email", emailStr);
                    params.put("phonenum", phonenum);
                    params.put("introduction", intro);
                    params.put("usertype", "Seller");
                    params.put("shopname", shopNameStr);
                    params.put("shoptype", shopTypeStr);

                    //if you add a marker, find items around the marker
                    if (destLongitude != null && destLatitude!= null) {
                       // Log.e("cao", "NULL DEST");
                        params.put("longitude", destLongitude);
                        params.put("latitude", destLatitude);
                    }
                    //else find items around your position
                    else  if (myLongitude != null && myLatitude !=null){
                       // Log.e("cao", "NULL MY");
                        params.put("longitude", myLongitude);
                        params.put("latitude", myLatitude);
                    }
                    else{
                        params.put("longitude", "null");
                        params.put("latitude", "null");
                    }



                    //String test = params.get("shoptype");
                    //Log.e("TYPE",test);

                    File videoFile = new File(outfile);


                    if (takephoto == true) {
                        if (photoPath != null) {
                            progressDialog = ProgressDialog.show(SellerSignUp.this,"Connecting","Connecting to server");

                            HttpConnectAMImage SendImage = new HttpConnectAMImage(photoPath);
                            HttpConnectAMAudio SendAudio = new HttpConnectAMAudio(videoFile);
                            SendImage.start();
                            SendAudio.start();
                            //DownloadImage image = new DownloadImage(Send.getUrl());

                        } else {
                            progressDialog = ProgressDialog.show(SellerSignUp.this,"Connecting","Connecting to server");
                            params.put("image", "null");
                            length = 1;
                            HttpConnectAMAudio SendAudio = new HttpConnectAMAudio(videoFile);
                            SendAudio.start();
                        }
                    }
                    else {
                        progressDialog = ProgressDialog.show(SellerSignUp.this,"Connecting","Connecting to server");

                        HttpConnectAMImage SendImage = new HttpConnectAMImage(exploreFile);
                        HttpConnectAMAudio SendAudio = new HttpConnectAMAudio(videoFile);
                        SendImage.start();
                        SendAudio.start();

                        }
                    }
                }
                else {
                    Toast.makeText(SellerSignUp.this, "Please record voice introduction!", Toast.LENGTH_LONG).show();
                }
            }
        });



        customerCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signCancel = new Intent(SellerSignUp.this, MainActivity.class);
                startActivity(signCancel);

            }
        });

        setUpMapIfNeeded();
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        //method to add mark and find route by ling click
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.clear();
                Marker m = mMap.addMarker(new MarkerOptions().position(
                        latLng).title("Search nearby stores?"));

                //the position I want to go
                destLatitude = String.valueOf(latLng.latitude);
                destLongitude = String.valueOf(latLng.longitude);

            }
        });



        // location manager
        final LocationManager locMgr =
                (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        getSystemService(Context.LOCATION_SERVICE);

        //location listener to monitor the change of location
        final LocationListener locListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double currLatitude =  (location.getLatitude());
                double currLongitude = (location.getLongitude());

                //my position
                myLongitude = String.valueOf(currLongitude);
                myLatitude = String.valueOf(currLatitude);

                LatLng currentLocation = new LatLng(currLatitude,currLongitude);
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currentLocation, 12);
                mMap.animateCamera(update);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        Button find = (Button) findViewById(R.id.btSellerFindLocation);
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<String> provider = locMgr.getAllProviders();
                String prov = null;

                for (String p:provider){
                    if (p != null)
                    {
                        prov = p;
                        break;
                    }
                }
                locMgr.requestLocationUpdates(prov,100000,0,locListener);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.sellerMapView))
                    .getMap();

        }
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
            switch (requestCode) {
                case 1:
                    Uri uri = data.getData();
                    String path = getPathFromUri(SellerSignUp.this,uri);
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
                        bm = MediaStore.Images.Media.getBitmap(cr, selectedImage);
                        imageView.setImageBitmap(bm);
                        Toast.makeText(this, "Succeed", Toast.LENGTH_LONG).show();
                        return;
                    } catch (IOException e) {
                        Toast.makeText(this, "Failure", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    takephoto = true;
                    break;
            }
        }
        else {
            Toast.makeText(this, "You didn't take a photo.", Toast.LENGTH_LONG).show();
            photoPath = null;
        }
    }

    private class HttpConnectAMImage extends HttpAmazonImage{
        HttpConnectAMImage(File file){
            super(file);
        }
        @Override
        public void run() {
            super.run();
            Message message = Message.obtain();
            message.what = 1;
            message.obj = super.getUrl();
            Threads_counting(message);
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

    private class HttpConnectAMAudio extends HttpAmzonAudio {

        public HttpConnectAMAudio(File file) {
            super(file);
        }
        public  void run(){
            super.run();
            Message message = Message.obtain();
            message.what = 3;
            message.obj = super.getUrl();
            Threads_counting(message);
        }
    }

    public void record() {
        if (recorder != null)
            recorder.release();
        File out = new File(outfile);
        if (out.exists()) {
//            System.out.println("Exist!!!!!!!!!");
            out.delete();
        }
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(outfile);
        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();
        flag=true;
    }

    public void stopRec() {

        if (recorder != null && flag==true) {
            recorder.stop();
            flag=false;

        }
    }

    public void playBack() {
        if (player != null) {
            player.release();
        }
        player = new MediaPlayer();
        if (outfile != null){
        try {
            player.setDataSource(outfile);
            player.prepare();

        } catch (IOException e) {
            e.printStackTrace();
//            return;
        }
        player.start();}
    }

    public void stopPlay() {
        if (player != null) {
            player.stop();
        }
    }

    public synchronized void Threads_counting(Message message){
        handler.sendMessage(message);
        threads_count++;
        if(threads_count == length){

            Message msg = Message.obtain();
            msg.what = 8;
            handler.sendMessage(msg);
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