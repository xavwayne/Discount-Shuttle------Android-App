package com.example.jiyushi1.dis.ws.local;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;

/**
 * Created by Joe on 7/25/2015.
 */
public class HttpAmazonImage extends Thread {
    private String Directory;
    private String FileName;
    private String Key;
    private URL url;
    private File file1;
    private Bitmap original;
    private File file2;
    // Amazon parameters
    final  BasicAWSCredentials basicAWSCredentials =
            new BasicAWSCredentials("*****",
                    "*****");
    final String BucketName = "discountserverimages";
    final AmazonS3Client S3Client = new AmazonS3Client(basicAWSCredentials);

    public HttpAmazonImage(String Directory, String LocalFileName){
        this.Directory = Directory;
        this.FileName = LocalFileName;
        SimpleDateFormat time=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String TimeString = time.format(new java.util.Date());
        this.Key = TimeString;
    }

    public HttpAmazonImage(File file1)  {
        this.file1 = file1;
        FileInputStream input = null;
        try {
            input = new FileInputStream(this.file1);
            original = BitmapFactory.decodeStream(input);
            original = Bitmap.createScaledBitmap(original,240,160,false);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            original.compress(Bitmap.CompressFormat.JPEG, 50, out);
            file2 = new File(file1.getParentFile()+"temp.jpg");
            FileOutputStream outputStream = new FileOutputStream (file2);
            out.writeTo(outputStream);
            this.file1 = file2;
            SimpleDateFormat time=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String TimeString = time.format(new java.util.Date());
            this.Key = TimeString+".jpg";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
//     read file from local device
        File file;
        if (file1 == null) {
            file = new File(Directory, FileName);
        }
        else{
            file = file1;
        }
        long LengthOfFile = file.length();

//     Send an image to Amazon S3 Server
        PutObjectRequest putObjectRequest = new PutObjectRequest(BucketName,Key,file);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(LengthOfFile);
        putObjectRequest.withMetadata(objectMetadata);
        S3Client.putObject(putObjectRequest);

        java.util.Date expiration = new java.util.Date();
        long msec = expiration.getTime();
        msec += 1000*60*60*24*365*10; // 10 year
        expiration.setTime(msec);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(BucketName, Key);
        generatePresignedUrlRequest.setExpiration(expiration);

        url = S3Client.generatePresignedUrl(generatePresignedUrlRequest);
        file2.delete();
    }

    public String getUrl(){
        return url.toString();
    }
}
