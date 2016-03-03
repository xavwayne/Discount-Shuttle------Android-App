package com.example.jiyushi1.dis.activity.seller;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.util.Log;
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
import com.example.jiyushi1.dis.ws.local.DownloadImage;
import com.example.jiyushi1.dis.ws.local.DownloadSound;
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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SellerAccount extends Activity {

    private GoogleMap mMap;

    private String usernameStr;
    private Handler handler;
    private int threads_count = 0;
    private int length = 2;
    private Map<String, String> params = new HashMap<String, String>();
    private ProgressDialog progressDialog;
    private Spinner shopType ;
    private TextView userName;

    private EditText email;
    private EditText phoneNum;
    private TextView shopName;
    private String myLongitude;
    private String myLatitude;
    private String destLongitude;
    private String destLatitude;
    private EditText introduction;
    //take a photo
    private  LocationManager locMgr;
    private LocationListener locListener;
    private ImageView imageView;
    Bundle bd;
    String username,password;
    boolean passchanged=false;
    private ImageView photo;
    private File photoPath;
    private boolean imageNotChange = true;
    private Uri imageUri;
    private HttpConnectDB util;


    private boolean takephoto = true;
    private String path;
    private MediaRecorder recorder;
    private MediaPlayer player;

    boolean flag=false;
    private String audioOutfile = null; //this is the path to the rec file
    private File exploreFile = null;
    private Button customerSign;
    private Button customerCancel;
    private Button takePhoto;
    private Button find;
    private Button explore;
    private Button change;
    private Button btRec ;
    private Button btPlay;

    private ArrayAdapter<CharSequence> typeSeection3;

    private String downloadImageUri;
    private String downloadAudioUri;
    private String currentLongitude;
    private String currentLatitude;
    private String shopTypeStr;
    private Uri uri = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_seller);

        bd = getIntent().getExtras();
        String dealWithInfo = bd.getString("accountInfo");
        Log.e("TEST", dealWithInfo);
        findPart();
        setMap();
        ParseInfo(dealWithInfo);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    params.put("image", (String) msg.obj);

                }

                if(msg.what == 2){
                    progressDialog.dismiss();
                    Toast.makeText(SellerAccount.this, util.getResult(), Toast.LENGTH_LONG).show();
                    if (util.getResult().compareTo("Your setting has been saved") == 0) {
                        finish();
                    }
                }

                if(msg.what == 3){
                    params.put("audio", (String) msg.obj);
                }

                if (msg.what == 8){
                    util = new HttpConnectDB(params, "utf-8",22);
                    util.start();

                }

                if(msg.what == 4){
                    imageView.setImageBitmap((Bitmap) msg.obj);
                    progressDialog.dismiss();
                }
            }
        };

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String photoName = time.format(new java.util.Date());


                Intent camera = new Intent("android.media.action.IMAGE_CAPTURE");
                photoPath = new File
                        (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                                , photoName);
                imageUri = Uri.fromFile(photoPath);
                camera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(camera, 2);

            }
        });

        explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, 1);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(SellerAccount.this, "error", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });

        customerSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                params.put("username", bd.getString("username"));

                //usernameStr


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

                if (emailStr.length() == 0 ||
                        phonenum.length() == 0 || intro.length() == 0
                        || shopNameStr.length() == 0) {
                    Toast.makeText(SellerAccount.this, "You should fill in All the blanks!", Toast.LENGTH_LONG).show();
                } else if (!emailB1 && !emailB2) {
                    Toast.makeText(SellerAccount.this, "You should input a correct email address!", Toast.LENGTH_LONG).show();
                }
                else if (
                        phonenum.length() >20|| intro.length()>30
                        || shopNameStr.length() >20 ){
                    Toast.makeText(SellerAccount.this, "Introduction's length should be no more than 30" +
                            " characters, and other parts should be no more than 20 characters.", Toast.LENGTH_LONG).show();
                }
                else {
                    //need to connect to server and uphold the form


                    //if you add a marker, find items around the marker
                    if (destLongitude != null && destLatitude!= null) {
                        params.put("longitude", destLongitude);
                        params.put("latitude", destLatitude);
                    }
                    //else find items around your position
                    else if (currentLongitude != null && currentLatitude !=null){
                        params.put("longitude", currentLongitude);
                        params.put("latitude", currentLatitude);
                    } else {
                        params.put("longitude", "null");
                        params.put("latitude", "null");
                    }

                    params.put("username", usernameStr);
                    params.put("email", emailStr);
                    params.put("phonenum", phonenum);
                    params.put("introduction", intro);
                    params.put("usertype", "Seller");
                    params.put("shopname", shopNameStr);
                    params.put("shoptype", shopTypeStr);

                    File audioFile = new File(audioOutfile);

                    if (takephoto == true) {
                        if (photoPath != null) {
                            if (audioOutfile.compareTo(uri.toString()) == 0) {
                                params.put("audio", downloadAudioUri);
                                //progressDialog = ProgressDialog.show(SellerAccount.this, "Connecting", "Connecting to server");
                                length = 1;
                            } else {
                                progressDialog = ProgressDialog.show(SellerAccount.this, "Connecting", "Connecting to server");
                                HttpConnectAMAudio SendAudio = new HttpConnectAMAudio(audioFile);
                                SendAudio.start();
                            }
                            progressDialog = ProgressDialog.show(SellerAccount.this, "Connecting", "Connecting to server");
                            HttpConnectAMImage SendImage = new HttpConnectAMImage(photoPath);
                            SendImage.start();
                        } else {
                            progressDialog = ProgressDialog.show(SellerAccount.this, "Connecting", "Connecting to server");
                            params.put("image", downloadImageUri);
                            length = 1;
                            if (audioOutfile.compareTo(uri.toString()) == 0) {
                                params.put("audio", downloadAudioUri);
                                //progressDialog = ProgressDialog.show(SellerAccount.this, "Connecting", "Connecting to server");
                                util = new HttpConnectDB(params, "utf-8",22);
                                util.start();
                            } else {
                                progressDialog = ProgressDialog.show(SellerAccount.this, "Connecting", "Connecting to server");
                                HttpConnectAMAudio SendAudio = new HttpConnectAMAudio(audioFile);
                                SendAudio.start();
                            }

                        }
                    } else {

                        if (audioOutfile.compareTo(uri.toString()) == 0) {
                            params.put("audio", downloadAudioUri);
                            //progressDialog = ProgressDialog.show(SellerAccount.this, "Connecting", "Connecting to server");
                            length = 1;

                        } else {
                            progressDialog = ProgressDialog.show(SellerAccount.this, "Connecting", "Connecting to server");
                            HttpConnectAMAudio SendAudio = new HttpConnectAMAudio(audioFile);
                            SendAudio.start();
                        }
                        progressDialog = ProgressDialog.show(SellerAccount.this, "Connecting", "Connecting to server");
                        HttpConnectAMImage SendImage = new HttpConnectAMImage(exploreFile);
                        SendImage.start();

                    }







                }
            }
        });



        customerCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLatitude.compareTo("null") == 0 || currentLongitude.compareTo("null") ==0 ){
                    AlertDialog alert = new AlertDialog.Builder(SellerAccount.this).create();
                    alert.setTitle("Whoops!");
                    alert.setMessage("You didn't upload shop's location.");
                    alert.show();
                }
                else {
                    LatLng currentLocation = new LatLng(Double.valueOf(currentLatitude), Double.valueOf(currentLongitude));
                    mMap.addMarker(new MarkerOptions().position(
                            currentLocation).title("Your Shop is now here"));
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currentLocation, 12);
                    mMap.animateCamera(update);
                }

            }
        });

        change.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SellerAccount.this, SellerPassword.class);
                        intent.putExtras(bd);
                        startActivity(intent);
                    }
                }
        );



        btRec.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        /*
                        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
                        String videoName = time.format(new java.util.Date()) +".3gpp";
                        */
                        audioOutfile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath()
                                + "/Temp.3gpp";
                        record();
                        Toast.makeText(SellerAccount.this, "Recording...", Toast.LENGTH_LONG).show();
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
                                Toast.makeText(SellerAccount.this, "Recorded!", Toast.LENGTH_LONG).show();
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
    }

    private void ParseInfo(String dealWithInfo) {
        String[] info = dealWithInfo.split("\n");

        userName.setText(usernameStr);
        shopName.setText(info[0]);
        introduction.setText(info[1]);
        email.setText(info[2]);
        phoneNum.setText(info[3]);
        downloadImageUri = info[4];
        downloadAudioUri = info[5];
        currentLatitude = info[6];
        currentLongitude = info[7];
        shopTypeStr = info[8];
        switch(shopTypeStr) {
            case "1":
                shopType.setSelection(0);
                break;
            case "2":
                shopType.setSelection(1);
                break;
            case "3":
                shopType.setSelection(2);
                break;
            case "4":
                shopType.setSelection(3);
                break;
            case "5":
                shopType.setSelection(4);
                break;
        }

        if (downloadImageUri != "null"){
            DownloadImageFromAM image = new DownloadImageFromAM(downloadImageUri);
            progressDialog = ProgressDialog.show(SellerAccount.this, "Connecting", "Connecting to server");
            image.start();

        }
        if (downloadAudioUri != null){
            DownloadSound audio = new DownloadSound(downloadAudioUri);
            uri = audio.getUri();
            audioOutfile = uri.toString();
        }

    }

    private void setMap() {
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
                        latLng).title("Your Shop's current location"));

                destLatitude = String.valueOf(latLng.latitude);
                destLongitude = String.valueOf(latLng.longitude);

            }
        });
        // location manager
        locMgr =
                (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        getSystemService(Context.LOCATION_SERVICE);

        //location listener to monitor the change of location
            locListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double currLatitude =  (location.getLatitude());
                double currLongitude = (location.getLongitude());

                //my position
                myLongitude = String.valueOf(currLongitude);
                myLatitude = String.valueOf(currLatitude);

                LatLng currentLocation = new LatLng(currLatitude,currLongitude);
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currentLocation, 14);
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
    }

    private void findPart() {
        bd=getIntent().getExtras();
        usernameStr =bd.getString("username");

        shopType = (Spinner) findViewById(R.id.shopTypeSpinner_M);
        typeSeection3 = ArrayAdapter.createFromResource(this, R.array.ItemType, android.R.layout.simple_spinner_dropdown_item);
        typeSeection3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shopType.setAdapter(typeSeection3);

        userName = (TextView) findViewById(R.id.sellerSignUserName_M);

        email = (EditText) findViewById(R.id.sellerSignEmail_M);
        phoneNum = (EditText) findViewById(R.id.sellerSignPhone_M);

        shopName = (EditText) findViewById(R.id.sellerSignShopName_M);
        imageView = (ImageView) findViewById(R.id.sellerImageView_M);
        introduction = (EditText) findViewById(R.id.sellerSignIntroduction_M);

        customerSign = (Button) findViewById(R.id.btSellerSingUp_M);
        customerCancel = (Button) findViewById(R.id.btSellerCancel_M);
        takePhoto = (Button) findViewById(R.id.btSellerCamera_M);
        find = (Button) findViewById(R.id.btSellerFindLocation_M);
        explore = (Button) findViewById(R.id.btSellerSignExplore_M);
        change=(Button)findViewById(R.id.btSellerChangepswd);
        btRec = (Button) findViewById(R.id.btSellerSignRecord);
        btPlay = (Button) findViewById(R.id.btSellerSignPlay);
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
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.sellerMapView_M))
                    .getMap();
        }
    }







    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != Activity.RESULT_OK){
            Toast.makeText(this, "You didn't take a photo.", Toast.LENGTH_LONG).show();
            photoPath = null;
            exploreFile = null;
            passchanged=false;
        }
        else {
            passchanged=true;
            switch (requestCode){
                case 1:
                    Uri uri = data.getData();
                    String path = getPathFromUri(SellerAccount.this,uri);
                    exploreFile = new File(path);

                    try {
                        InputStream selectedImage = new FileInputStream(exploreFile);
                        Bitmap bm1 = BitmapFactory.decodeStream(selectedImage);
                        imageView.setImageBitmap(bm1);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    takephoto = false;
                    imageNotChange = false;
                    break;
                case 2:
                    Uri selectedImage = imageUri;
                    getContentResolver().notifyChange(selectedImage, null);
                    ContentResolver cr = getContentResolver();
                    Bitmap bm;
                    try {
                        bm = MediaStore.Images.Media.getBitmap(cr, selectedImage);
                        imageView.setImageBitmap(bm);
                        Toast.makeText(SellerAccount.this, "Succeed", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Toast.makeText(SellerAccount.this, "Failure", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    takephoto = true;
                    imageNotChange = false;
                    break;

            }
        }
    }
    public void record() {
        if (recorder != null)
            recorder.release();
        File out = new File(audioOutfile);
        if (out.exists()) {
//            System.out.println("Exist!!!!!!!!!");
            out.delete();
        }
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(audioOutfile);
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
        try {
            player.setDataSource(audioOutfile);
            player.prepare();

        } catch (IOException e) {
            e.printStackTrace();
//            return;
        }
        player.start();
    }

    public void stopPlay() {
        if (player != null) {
            player.stop();
        }
    }

    private class HttpConnectAMImage extends HttpAmazonImage {
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

    public synchronized void Threads_counting(Message message){
        handler.sendMessage(message);
        threads_count++;
        if(threads_count == length){
            Message msg = Message.obtain();
            msg.what = 8;
            handler.sendMessage(msg);
        }
    }

    private class DownloadImageFromAM extends DownloadImage {
        DownloadImageFromAM(String url){
            super(url);
        }
        @Override
        public void run() {
            super.run();
            Message message = Message.obtain();
            message.what = 4;
            message.obj = super.getBitmap();
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
