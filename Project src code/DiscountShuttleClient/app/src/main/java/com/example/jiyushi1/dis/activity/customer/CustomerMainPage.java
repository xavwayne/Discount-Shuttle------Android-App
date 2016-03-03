package com.example.jiyushi1.dis.activity.customer;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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



public class CustomerMainPage extends Fragment {
    View view;
    Fragment frag;
    FragmentTransaction fragTran;
    private GoogleMap mMap;

    private Spinner type ;
    private String myLongitude = "null";
    private String myLatitude = "null";
    private String destLongitude;
    private String destLatitude;
    private  Bundle bd;

    private Handler handler;
    private ProgressDialog progressDialog;

    private ArrayAdapter<CharSequence> typeSeection1;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_just_search,container,false);
        /* get the username passed by main activity */
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==2){
                    bd.putString("ShowShopList", (String)msg.obj);
                    bd.putString("type", "Customer");
                    progressDialog.dismiss();
                    getActivity().getIntent().putExtras(bd);
                    frag = new CustomerSearchOutcomes();
                    fragTran = getFragmentManager().beginTransaction().replace(R.id.container, frag);
                    fragTran.commit();
                }

                if(msg.what==3){
                    /* given the user name, get the url of uploaded image */
                    String accountInfo = (String)msg.obj;
                    bd.putString("accountInfo", accountInfo);
                    progressDialog.dismiss();
                    getActivity().getIntent().putExtras(bd);

                    frag = new CustomerAccount();
                    fragTran = getFragmentManager().beginTransaction().replace(R.id.container, frag);
                    fragTran.commit();
                }
            }
        };

        bd = getActivity().getIntent().getExtras();
        bd.putString("usertype", "Customer");

        String username = bd.getString("username");

        type = (Spinner) view.findViewById(R.id.itemType);
        typeSeection1 = ArrayAdapter.createFromResource(getActivity(), R.array.ItemType, android.R.layout.simple_spinner_dropdown_item);
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
                (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        getActivity().getSystemService(Context.LOCATION_SERVICE);


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


        // back to log in page
        Button back = (Button) view.findViewById(R.id.btCustomerLogout);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent cLogout = new Intent(getActivity(), MainActivity.class);
                startActivity(cLogout);

            }
        });

        //find current location
        Button find = (Button) view.findViewById(R.id.btfindLocation);
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

        // to edit account
        Button edit = (Button) view.findViewById(R.id.btEditCustomer);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = bd.getString("username");
                Map<String, String> params = new HashMap<String, String>();

                params.put("username", username);
                progressDialog = ProgressDialog.show(getActivity(),"Connecting","Connecting to Server");
                HttpConnectDB util = new HttpConnectDB(params, "utf-8", 6,2);
                util.start();

            }
        });

        //search your product according to your location
        Button Search = (Button) view.findViewById(R.id.btCustomerSearch);
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
                progressDialog = ProgressDialog.show(getActivity(),"Searching","Searching sellers");
                HttpConnectDB util = new HttpConnectDB(params, "utf-8",4,1);
                util.start();

            }
        });
        return view;
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
            mMap = ((MapFragment) getChildFragmentManager().findFragmentById(R.id.mapView))
                    .getMap();

        }
    }

    private class HttpConnectDB extends HttpDBServer {
        int number;
        HttpConnectDB(Map<String, String> params, String encode,int requestType,int number){
            super(params,encode,requestType);
            this.number = number;
        }
        @Override
        public void run() {
            if(number == 1){
                super.run();
                Message message = Message.obtain();
                message.what = 2;
                message.obj = super.getResult();
                handler.sendMessage(message);
            }
            if(number == 2){
                super.run();
                Message message = Message.obtain();
                message.what = 3;
                message.obj = super.getResult();
                handler.sendMessage(message);
            }
        }
    }


}
