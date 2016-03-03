package com.example.jiyushi1.dis.activity.customer;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jiyushi1.dis.R;
import com.example.jiyushi1.dis.model.SearchOutcomesShopDetail;
import com.example.jiyushi1.dis.ws.local.DownloadImage;
import com.example.jiyushi1.dis.ws.local.DownloadSound;
import com.example.jiyushi1.dis.ws.local.HttpDBServer;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CustomerSearchDetail extends Fragment {
    private GoogleMap mMap;
    private MediaPlayer player = new MediaPlayer();

    private View view;
    private Fragment frag;
    private FragmentTransaction fragTran;

    private List<SearchOutcomesShopDetail> myShopList= new ArrayList<SearchOutcomesShopDetail>();
    private String longitude;
    private String latitude;

    private String Url_list[];
    private int threads_count = 0;
    private Handler handler;
    private ProgressDialog progressDialog;
    private Integer length;
    private Bitmap bitmaps[];
    private DownloadFromAM newImage[];
    private  String  videoUri;
    private Uri uri = null;
    @Override
   public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_detail_info,container,false);


        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {

                if(msg.what == 8){
                    populateListView(view);
                    registerClick();
                }
            }
        };

        Bundle bd = getActivity().getIntent().getExtras();
        String shopDetail = bd.getString("showshopdetail");

        Log.e("TAG",shopDetail);

        TextView shopName = (TextView) view.findViewById(R.id.customerDetailShopname);
        shopName.setText(bd.getString("shopname"));
        String Details[] = shopDetail.split("\t");


        final AlertDialog alert = new AlertDialog.Builder(getActivity()).create();

        if (Details[0].compareTo("0") == 0){
            populateShopDetail(shopDetail);
            alert.setTitle("Whoops!");
            alert.setMessage("The shop owner is too lazy, he uploads no products.");
            alert.show();
        }
        else if (shopDetail.compareTo("Internet Disconnected") == 0){
            alert.setTitle("Whoops!");
            alert.setMessage("Can not connect to server");
            alert.show();
        }
        else {
            populateShopDetail(shopDetail);
            DownloadSet(Url_list);

        }

        DownloadSound downloadSound = new DownloadSound(videoUri);
        uri = downloadSound.getUri();


        Button back = (Button) view.findViewById(R.id.btBackSearch);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frag = new CustomerSearchOutcomes();
                fragTran = getFragmentManager().beginTransaction().replace(R.id.container, frag);
                fragTran.commit();


            }
        });

        setUpMapIfNeeded();
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        // location manager
        final LocationManager locMgr =
                (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        getActivity().getSystemService(Context.LOCATION_SERVICE);

        //location listener to monitor the change of location
        final LocationListener locListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double currLatitude =  (location.getLatitude());
                double currLongitude = (location.getLongitude());

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


        Button find = (Button) view.findViewById(R.id.btCustomerDetailFind);
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (longitude.compareTo("null") == 0 || latitude.compareTo("null") ==0 ){
                    alert.setTitle("Whoops!");
                    alert.setMessage("The owner didn't upload its location.");
                    alert.show();
                }
                else {
                    List<String> provider = locMgr.getAllProviders();
                    String prov = null;

                    for (String p : provider) {
                        if (p != null) {
                            prov = p;
                            break;
                        }
                    }
                    locMgr.requestLocationUpdates(prov, 100000, 0, locListener);
                    mMap.addMarker(new MarkerOptions().position(
                            new LatLng(Double.valueOf(latitude), Double.valueOf(longitude))).title("The shop is here!"));
                }
            }
        });


        ImageButton play = (ImageButton) view.findViewById(R.id.btCustomerDetailVoice);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uri != null) {

                    try {
                        player.reset();
                        player.setDataSource(getActivity(),uri);
                        player.prepare();
                        player.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(getActivity(),"No audio is downloaded.", Toast.LENGTH_LONG).show();
                }
            }
        });

        ImageButton stop = (ImageButton) view.findViewById(R.id.btCustomerDetailStop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlay();
            }
        });


        return view;
    }



    private void populateListView(View view) {
        ArrayAdapter<SearchOutcomesShopDetail> adapter= new MyListAdapter();
        ListView list = (ListView) view.findViewById(R.id.Detail);
        list.setAdapter(adapter);

    }




    private class MyListAdapter extends ArrayAdapter<SearchOutcomesShopDetail> {
        public MyListAdapter(){
            super(getActivity(),R.layout.shopdetail_view, myShopList);
        }
        public View getView(int position ,View converView, ViewGroup parent){
            View itemView = converView;
            if (itemView == null){
                itemView = getActivity().getLayoutInflater().inflate(R.layout.shopdetail_view,parent,false);
            }
            SearchOutcomesShopDetail currentShop = myShopList.get(position);

            ImageView itemImage = (ImageView) itemView.findViewById(R.id.itemImage);
            itemImage.setImageBitmap(bitmaps[position]);

            TextView item =(TextView) itemView.findViewById(R.id.item);
            item.setText(currentShop.getName());

            //TextView introduction =(TextView) itemView.findViewById(R.id.introduction);
            //introduction.setText(currentShop.getIntroduction());

            TextView price =(TextView) itemView.findViewById(R.id.price);
            price.setText(""+currentShop.getPriceOrig());

            TextView discount =(TextView) itemView.findViewById(R.id.discount);
            discount.setText(""+currentShop.getDiscount());

            return itemView;
        }

    }

    private void populateShopDetail(String shopDetail) {

        String[] phase1 = shopDetail.split("\t");
        length = Integer.parseInt(phase1[0]);
        newImage = new DownloadFromAM[length];
        Url_list = new String[length];
        bitmaps = new Bitmap[length];
        String[] location = phase1[1].split(";");
        longitude = location[0];
        latitude = location[1];
        videoUri = location[2];
        for (int i = 0 ; i < length; i++){
            String[] phase2 = phase1[i+2].split(";");
            myShopList.add(new SearchOutcomesShopDetail(phase2[0],phase2[1],phase2[2],phase2[3],phase2[4]));
            Url_list[i] = phase2[4];
        }
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
            mMap = ((MapFragment) getChildFragmentManager().findFragmentById(R.id.customerDetailMapView))
                    .getMap();

        }
    }

    private void registerClick() {
        ListView list = (ListView) view.findViewById(R.id.Detail);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                AlertDialog alert = new AlertDialog.Builder(getActivity()).create();
                SearchOutcomesShopDetail shopClicked = myShopList.get(position);
                String message = shopClicked.getIntroduction();
                alert.setTitle("Introduction of this:");
                alert.setMessage(message);
                alert.show();
            }
        });
    }

    private void DownloadSet(String Url_list[]){
        progressDialog = ProgressDialog.show(getActivity(), "Downloading", "Downloading images");
        for(int i=0;i<Url_list.length;i++){
            newImage[i] = new DownloadFromAM(Url_list[i], i);
            newImage[i].start();
        }
    }

    private class DownloadFromAM extends DownloadImage {
        int number;
        DownloadFromAM(String url, int number){
            super(url);
            this.number = number;
        }

        @Override
        public void run() {
            super.run();
            Message message = Message.obtain();
            message.what = 1;
            bitmaps[number] = super.getBitmap();
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

    public synchronized void Threads_counting(Message message){
        handler.sendMessage(message);
        threads_count++;
        if(threads_count == length){
            progressDialog.dismiss();
            Message msg = Message.obtain();
            msg.what = 8;
            handler.sendMessage(msg);
        }
    }

    public void stopPlay() {

            if (player != null) {
                if (player.isPlaying()){
                    player.stop();
                }
                else {
                    Toast.makeText(getActivity(),"Nothing is playing", Toast.LENGTH_LONG).show();
                }
            }
        else{
                Toast.makeText(getActivity(),"No audio is downloaded", Toast.LENGTH_LONG).show();
            }



    }
}
