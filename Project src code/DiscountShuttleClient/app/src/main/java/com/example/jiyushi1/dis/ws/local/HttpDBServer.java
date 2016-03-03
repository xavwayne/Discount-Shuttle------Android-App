package com.example.jiyushi1.dis.ws.local;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by Joe on 7/28/2015.
 */
public class HttpDBServer extends Thread{
    private URL url = null;
    private Map<String, String> params;
    private String encode;
    private int requestType;
    public String result = null;
    public static Object locker = new Object();

    public HttpDBServer(Map<String, String> params, String encode,int requestType){
        this.params = params;
        this.encode = encode;
        this.requestType = requestType;
    }

    public String getResult(){
        return result;
    }

    public void run(){
            String IP = "***.***.***.**:8080/";


            switch (requestType) {
                case 1:  //case sign up
                    try {
                        url = new URL("http://"+IP+ "Discount_Server/Sign_up_customer");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    result = submitPostData(params, encode);

                    break;

                case 2: //case log in
                    try {
                        url = new URL("http://"+IP+"Discount_Server/Sign_up_seller");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    result = submitPostData(params, encode);

                    break;
                case 3:
                    try {
                        url = new URL("http://"+IP+"Discount_Server/Login");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    result = submitPostData(params, encode);

                    break;

                case 4:
                    try {
                        url = new URL("http://"+IP+"Discount_Server/Search_sellers");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    result = submitPostData(params, encode);

                    break;

                case 5:
                    try {
                        url = new URL("http://"+IP+"Discount_Server/Search_products");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    result = submitPostData(params, encode);

                    break;

                case 6:
                    try {
                        url = new URL("http://"+IP+"Discount_Server/Read_a_customer");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    result = submitPostData(params, encode);

                    break;
                case 7:
                    try {
                        url = new URL("http://"+IP+"Discount_Server/Edit_a_customer");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    result = submitPostData(params, encode);

                    break;
                case 8:
                    try {
                        url = new URL("http://"+IP+"Discount_Server/Change_password");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    result = submitPostData(params, encode);

                    break;

                case 9:
                    try {
                        url = new URL("http://"+IP+"Discount_Server/Seller_get_product");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    result = submitPostData(params, encode);
                    break;
                case 10:
                    try {
                        url = new URL("http://"+IP+"Discount_Server/Edit_a_product");

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    result = submitPostData(params, encode);
                    break;

                case 11:

                    try {
                        url = new URL("http://"+IP+"Discount_Server/Delete_a_product");

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    result = submitPostData(params, encode);
                    break;

                case 12:
                    try {
                        url = new URL("http://"+IP+"Discount_Server/Add_a_product");

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    result = submitPostData(params, encode);
                    break;

                case 13:
                    try {
                        url = new URL("http://"+IP+"Discount_Server/Seller_discount_barn");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    result = submitPostData(params, encode);
                    break;

                case 21:
                    try {
                        url = new URL("http://"+IP+"Discount_Server/Read_a_seller");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    result = submitPostData(params, encode);

                    break;
                case 22:
                    try {
                        url = new URL("http://"+IP+"Discount_Server/Edit_a_seller");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    result = submitPostData(params, encode);
                    break;
            }
    }

    public String submitPostData(Map<String, String> params, String encode) {

        byte[] data = getRequestData(params, encode).toString().getBytes();

        try {

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setUseCaches(false);

            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));

            OutputStream outputstream = httpURLConnection.getOutputStream();
            outputstream.write(data);

            //String test = params.get("shoptype");
            //Log.e("TYPE IN USER TYPE", test);

            int response = httpURLConnection.getResponseCode();            //Get Response Code
            if (response == HttpURLConnection.HTTP_OK) {
                InputStream inptStream = httpURLConnection.getInputStream();
                return dealResponseResult(inptStream);                 // Deal Response
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Internet Disconnected";

    }

    protected StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            for(Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), encode))
                        .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }

    protected String dealResponseResult(InputStream inputStream) {

        String resultData = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        try {
            while ((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultData = new String(byteArrayOutputStream.toByteArray());
        return resultData;
    }
}


