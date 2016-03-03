package com.example.jiyushi1.dis.activity.customer;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jiyushi1.dis.R;
import com.example.jiyushi1.dis.model.SearchOutcomesShopList;
import com.example.jiyushi1.dis.ws.local.DownloadImage;
import com.example.jiyushi1.dis.ws.local.HttpDBServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CustomerSearchOutcomes extends Fragment {
    private View view;
    private Fragment frag;
    private FragmentTransaction fragTran;
    private List<SearchOutcomesShopList> myShopList= new ArrayList<SearchOutcomesShopList>();

    private String Url_list[];
    private int threads_count = 0;

    private Handler handler;
    private ProgressDialog progressDialog;
    private Integer length;
    private Bitmap bitmaps[];
    private DownloadFromAM newImage[];
    private String ClickedShop;
    SearchOutcomesShopList shopClicked;


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_search_outcomes,container,false);

        Bundle bd = getActivity().getIntent().getExtras();
        //String username = bd.getString("username");
        String shoplist = bd.getString("ShowShopList");

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 8){
                    populateListView(view);
                    registerClick();
                }
                if(msg.what == 2){
                    String Result = (String)msg.obj;
                    Bundle bd = getActivity().getIntent().getExtras();
                    Log.e("Result!!!!!:",Result);
                    bd.putString("showshopdetail", Result);
                    bd.putString("shopname", shopClicked.getShopName());
                    getActivity().getIntent().putExtras(bd);
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), ClickedShop, Toast.LENGTH_LONG).show();
                    frag = new CustomerSearchDetail();
                    fragTran = getFragmentManager().beginTransaction().replace(R.id.container, frag);
                    fragTran.commit();
                }
            }
        };
        //if theere is no store in this type
        AlertDialog alert = new AlertDialog.Builder(getActivity()).create();
        if (shoplist.compareTo("0") == 0 ){
            alert.setTitle("Whoops!");
            alert.setMessage("There is no store of this type.");
            alert.show();
        }
        else if (shoplist.compareTo("Internet Disconnected") == 0){
            alert.setTitle("Whoops!");
            alert.setMessage("Can not connect to server");
            alert.show();
        }
        else {
            populateShopList(shoplist);
            DownloadSet(Url_list);
        }

        Button back = (Button) view.findViewById(R.id.btBackOutcome);
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

    private void registerClick() {
        ListView list = (ListView) view.findViewById(R.id.ShopList);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {

                shopClicked = myShopList.get(position);
                ClickedShop = "You clicked " + shopClicked.getShopName();


                Map<String, String> params = new HashMap<String, String>();
                params.put("pk", shopClicked.getPk());
                progressDialog = ProgressDialog.show(getActivity(),"Connecting","Reading items from the shop");
                HttpConnectDB util = new HttpConnectDB(params, "utf-8", 5);
                util.start();

            }
        });

    }

    private void populateListView(View view) {
        ArrayAdapter<SearchOutcomesShopList> adapter= new MyListAdapter();
        ListView list = (ListView) view.findViewById(R.id.ShopList);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<SearchOutcomesShopList> {
        public MyListAdapter(){
            super(getActivity(),R.layout.shoplist_view,myShopList);
        }
        public View getView(int position ,View converView, ViewGroup parent){
            View itemView = converView;
            if (itemView == null){
                itemView = getActivity().getLayoutInflater().inflate(R.layout.shoplist_view,parent,false);
            }
            SearchOutcomesShopList currentShop = myShopList.get(position);

            ImageView image = (ImageView) itemView.findViewById(R.id.specialImage);
            image.setImageBitmap(bitmaps[position]);

            TextView shopName =(TextView) itemView.findViewById(R.id.shopName);
            shopName.setText(currentShop.getShopName());

            TextView shopSpecial =(TextView) itemView.findViewById(R.id.introduction);
            shopSpecial.setText(currentShop.getIntroduction());

            return itemView;
        }
    }

    private void populateShopList(String shopList) {

        String[] phase1 = shopList.split("\t");
        length = Integer.parseInt(phase1[0]);
        newImage = new DownloadFromAM[length];
        Url_list = new String[length];
        bitmaps = new Bitmap[length];
        for (int i = 0 ; i < length; i++){
            String[] phase2 = phase1[i+1].split(";");
            myShopList.add(new SearchOutcomesShopList(phase2[0],phase2[1],phase2[2],phase2[3]));
            Url_list[i] = phase2[2];
        }

    }

    private void DownloadSet(String Url_list[]){
        progressDialog = ProgressDialog.show(getActivity(),"Downloading","Downloading images");
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
            progressDialog.dismiss();
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

}