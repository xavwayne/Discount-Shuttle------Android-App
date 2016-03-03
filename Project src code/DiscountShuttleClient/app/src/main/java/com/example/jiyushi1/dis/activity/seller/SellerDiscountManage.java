package com.example.jiyushi1.dis.activity.seller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jiyushi1.dis.R;
import com.example.jiyushi1.dis.ws.local.DownloadImage;
import com.example.jiyushi1.dis.ws.local.HttpDBServer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class SellerDiscountManage extends Activity {

    private Bundle bd;
    private ListView lvProduct;
    private String username,productname;

    private Handler handler;
    private ProgressDialog progressDialog;
    private ArrayList<String> Url_list;
    private int threads_count;
    private int ImageCount;
    private Integer length;
    private ArrayList<Bitmap> bitmaps;
    private ArrayList<DownloadFromAM> newImage;

    ArrayList<String> Titems,Iitems;

    Button btAdd,btDel;

    ArrayList<LinkedHashMap<String,Object>> Sitems;
    LinkedHashMap<String, Object> Sitem;

    CheckedTextView prechecked=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount_message_seller);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){

                }
                if(msg.what == 2){          // reading basic information from DB Server

                    String result=(String)msg.obj;

                    Log.e("Res",result);
                    String res[]=result.split("\t");
                    if(res[0].equals("0")){
                        ArrayAdapter arrayAdapter = new ArrayAdapter(SellerDiscountManage.this,android.R.layout.simple_list_item_1);
                        lvProduct.setAdapter(arrayAdapter);
                        return;
                    }

                    Sitems=new ArrayList<LinkedHashMap<String,Object>>();
                    Titems=new ArrayList<String>();
                    Iitems=new ArrayList<String>();
//                    ImageCount =0;
                    threads_count = 0;
                    for (int i=1;i<(res.length+1)/2;i++){
                        Titems.add(res[i]);
                        length++;
                    }
//                    Log.e("LLLLLLLLLLLLLLL",length.toString());
//                    Url_list = new String[length];
//                    bitmaps = new Bitmap[length];
//                    newImage = new DownloadFromAM[length];
                    bitmaps = new ArrayList<Bitmap>();
                    for(int i=0;i<length;i++){
                        bitmaps.add(null);
                    }
                    for (int i=(res.length+1)/2;i<res.length;i++){
                        Url_list.add( res[i]);
                        Iitems.add(res[i]);
                    }

                    for(int i=0;i<Titems.size();i++){
                        Sitem=new LinkedHashMap<String,Object>();
                        Sitem.put("text",Titems.get(i));
                        Sitem.put("image",Iitems.get(i));
                        Sitems.add(Sitem);
                    }
                    DownloadSet(Url_list);  // start downloading images
                }

                if(msg.what == 4){                  // delete an item
                    String result=(String)msg.obj;
                    String res[]=result.split("\t");
                    if(res[0].equals("0")){
                        return;
                    }
                    int count =0;
                    for(LinkedHashMap<String,Object> x:Sitems){
                        if(x.containsValue(productname)) {
                            Sitems.remove(x);
                            bitmaps.remove(count);
                            break;
                        }
                        count++;
                    }
                    showList();
                }

                if(msg.what == 8){          //  Images downloading finished
                    int a=android.R.layout.simple_list_item_single_choice;

                    X_Adapter xa=new X_Adapter(SellerDiscountManage.this,R.layout.product_layout,Sitems);

                    lvProduct.setAdapter(xa);
                    lvProduct.setOnItemClickListener(
                            new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    productname=Titems.get(position);
//                                    Toast.makeText(SellerDiscountManage.this, "selected" + position + "-" + id, Toast.LENGTH_SHORT).show();

                                    if (prechecked!=null){
                                            prechecked.setChecked(false);
                                    }

                                    CheckedTextView ctv= (CheckedTextView) view.findViewById(R.id.tvInList);
                                    ctv.setChecked(true);

                                    prechecked=ctv;
                                }
                            }
                    );

                }
            }

        };

        bd = getIntent().getExtras();
        username = bd.getString("username");


        lvProduct=(ListView)findViewById(R.id.lvProducts);

        Button back = (Button) findViewById(R.id.btSellerProductBack);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click(v);
            }
        });

        Button edit = (Button) findViewById(R.id.btSellerProductEdit);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click(v);
            }
        });

        btAdd=(Button) findViewById(R.id.btAdd);
        btDel=(Button) findViewById(R.id.btDelete);

        btAdd.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        click(v);
                    }
                }
        );

        btDel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        click(v);
                    }
                }
        );

        showList();
    }


    public void deleteItem(){

        LinkedHashMap<String,String> para=new LinkedHashMap<String,String>();
        para.put("username", username);
        para.put("productname", productname);
        X_Adapter xa=new X_Adapter(SellerDiscountManage.this,R.layout.product_layout,Sitems);
        lvProduct.setAdapter(xa);
        lvProduct.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        HttpConnectDB util=new HttpConnectDB(para,"utf-8",11,4);
        util.start();
    }


    public void showList(){
        length = 0;
        LinkedHashMap<String,String> para=new LinkedHashMap<String,String>();
        para.put("username",username);
        progressDialog = ProgressDialog.show(this,"Connecting","Connecting to Server");
        Url_list = new ArrayList<String>();
        newImage = new ArrayList<DownloadFromAM>();
        HttpConnectDB util=new HttpConnectDB(para,"utf-8",13,2);
        util.start();
    }




    public void click(View v){
        switch (v.getId()){
            case R.id.btSellerProductEdit:

                if(productname==null || productname.length()==0){
                    Toast.makeText(this,"No item selected!",Toast.LENGTH_LONG).show();
                    return;
                }

                Intent intent=new Intent(SellerDiscountManage.this,SellerEditProduct.class);
                bd.putString("product",productname);
                intent.putExtras(bd);
                startActivity(intent);
                break;

            case  R.id.btSellerProductBack:
                finish();
                break;

            case  R.id.btDelete:
                if(productname==null || productname.length()==0){
                    Toast.makeText(this,"No item selected!",Toast.LENGTH_LONG).show();
                    return;
                }
                deleteItem();
                break;

            case R.id.btAdd:
                Intent intent4=new Intent(SellerDiscountManage.this,SellerPostNew.class);
                intent4.putExtras(bd);
                startActivity(intent4);
                break;

        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        showList();
    }

    /**
     *  Customer defined adapter
     */
    private class X_Adapter extends BaseAdapter {

        Activity context;
        int layout;
        ArrayList<LinkedHashMap<String,Object>> data;
        LayoutInflater inflater=null;

        public X_Adapter(Activity con,int layout,ArrayList<LinkedHashMap<String,Object>> data){

            this.context=con;
            this.layout=layout;
            this.data=data;
            inflater= (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position).get("text");
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v;

            v=inflater.inflate(layout,null);

            CheckedTextView text= (CheckedTextView) v.findViewById(R.id.tvInList);
            ImageView im=(ImageView)v.findViewById(R.id.iviInList);

            String tx=(String)data.get(position).get("text");
            text.setText(tx);

            im.setImageBitmap(bitmaps.get(position));
            return v;
        }
    }

    private void DownloadSet(ArrayList<String> Url_list){
        progressDialog = ProgressDialog.show(this,"Downloading","Downloading images");
        for(int i=0;i<Url_list.size();i++){
            newImage.add( new DownloadFromAM(Url_list.get(i), i));
            newImage.get(i).start();
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
            bitmaps.set(number,super.getBitmap());
            Threads_counting(message);
        }
    }

    private class HttpConnectDB extends HttpDBServer {
        private int type;
        HttpConnectDB(Map<String, String> params, String encode,int requestType, int type){
            super(params,encode,requestType);
            this.type = type;
        }
        @Override
        public void run() {
            if(type == 2) {
                super.run();
                Message message = Message.obtain();
                message.what = 2;
                message.obj = super.getResult();
                progressDialog.dismiss();
                handler.sendMessage(message);
            }
            if(type == 4){
                super.run();
                Message message = Message.obtain();
                message.what = 4;
                message.obj = super.getResult();
                progressDialog.dismiss();
                handler.sendMessage(message);
            }
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
