package com.example.jiyushi1.dis.ws.local;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;

/**
 * Created by jiyushi1 on 7/29/15.
 */
public class HttpAmzonAudio extends Thread {
    private File FileName;
    private String Key;
    private URL url;

    // Amazon parameters
    final BasicAWSCredentials basicAWSCredentials =
            new BasicAWSCredentials("*****",
                    "*****");
    final String BucketName = "discountserveraudios";
    final AmazonS3Client S3Client = new AmazonS3Client(basicAWSCredentials);

    public HttpAmzonAudio(File file){
        this.FileName = file;
        SimpleDateFormat time=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String TimeString = time.format(new java.util.Date());
        this.Key = TimeString+".3gpp";
    }

    public void run(){
//     read file from local device
        long LengthOfFile = FileName.length();

//     Send an image to Amazon S3 Server
        PutObjectRequest putObjectRequest = new PutObjectRequest(BucketName,Key,FileName);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(LengthOfFile);
        putObjectRequest.withMetadata(objectMetadata);
        S3Client.putObject(putObjectRequest);

        java.util.Date expiration = new java.util.Date();
        long msec = expiration.getTime();
        msec += 1000 * 60 * 60 *24 *365 *10; // 1 hour.
        expiration.setTime(msec);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(BucketName, Key);
        generatePresignedUrlRequest.setExpiration(expiration);
        generatePresignedUrlRequest.setMethod(HttpMethod.GET);
        url = S3Client.generatePresignedUrl(generatePresignedUrlRequest);

    }

    public String getUrl(){
        return url.toString();
    }
}
