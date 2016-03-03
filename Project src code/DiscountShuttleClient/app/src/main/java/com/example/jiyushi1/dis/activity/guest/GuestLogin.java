package com.example.jiyushi1.dis.activity.guest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.jiyushi1.dis.R;

import com.example.jiyushi1.dis.activity.main.MainActivity;
import com.example.jiyushi1.dis.ws.local.HttpDBServer;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GuestLogin extends Activity {

    private GoogleMap mMap;

    private Spinner type ;
    private String myLongitude = "null";
    private String myLatitude = "null";
    private String destLongitude;
    private String destLatitude;
    private Bundle bd = new Bundle();

    private Handler handler;
    private ProgressDialog progressDialog;

    private ArrayAdapter<CharSequence> typeSeection1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_login);

        //bd = this.getIntent().getExtras();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==2){
                    bd.putString("ShowShopList", (String)msg.obj);
                    bd.putString("type", "Guest");
                    Intent gToResult = new Intent(GuestLogin.this, GuestSearchOutcomes.class);
                    gToResult.putExtras(bd);
                    startActivity(gToResult);
                }
            }
        };


        type = (Spinner) findViewById(R.id.itemType);
        typeSeection1 = ArrayAdapter.createFromResource(this, R.array.ItemType, android.R.layout.simple_spinner_dropdown_item);
        typeSeection1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(typeSeection1);



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




        //find current location
        Button find = (Button) findViewById(R.id.btGuestFindLocation);
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
                locMgr.requestLocationUpdates(prov,10000,0,locListener);

                //Location lastLoc = locMgr.getLastKnownLocation(prov);
                //double currLatitude =  (lastLoc.getLatitude());
                //double currLongitude = (lastLoc.getLongitude());
                // mMap.addMarker(new MarkerOptions().position(
                //       new LatLng(myhomeX,myhomeY )).title("Find Me Here"));

            }
        });

        Button back = (Button) findViewById(R.id.btGuestLogout);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent guestToLog = new Intent(GuestLogin.this, MainActivity.class);
                startActivity(guestToLog);

            }
        });

        //search your product according to your location
        Button Search = (Button) findViewById(R.id.btGuestSearch);
        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemtype = type.getSelectedItem().toString();

                Map<String, String> params = new HashMap<String, String>();
                params.put("itemtype", itemtype);

                //if you add a marker, find items around the marker
                if (destLongitude != null) {
                    params.put("longitude", destLongitude);
                    params.put("latitude", destLatitude);
                }
                //else find items around your position
                else{
                    params.put("longitude", myLongitude);
                    params.put("latitude", myLatitude);
                }
                progressDialog = ProgressDialog.show(GuestLogin.this,"Searching","Searching sellers");
                HttpConnectDB util = new HttpConnectDB(params, "utf-8",4);
                util.start();

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
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapView))
                    .getMap();

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
            progressDialog.dismiss();
            handler.sendMessage(message);
        }
    }

}
