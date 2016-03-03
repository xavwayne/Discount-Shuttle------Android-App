package com.example.jiyushi1.dis.model;

/**
 * Created by jiyushi1 on 7/23/15.
 */
public class Shop {
    private double priceOrig;
    private double priceNow;
    String name;
    String introduction;
    private int imageID;

    public Shop(String name, String introduction, int imageID, double priceOrig, double priceNow ) {
        this.priceOrig = priceOrig;
        this.priceNow = priceNow;
        this.name = name;
        this.introduction = introduction;
        this.imageID = imageID;
    }

    public double getPriceOrig() {
        return priceOrig;
    }

    public void setPriceOrig(double priceOrig) {
        this.priceOrig = priceOrig;
    }

    public double getPriceNow() {
        return priceNow;
    }

    public void setPriceNow(double priceNow) {
        this.priceNow = priceNow;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }
    public int getDiscount(){
        double discount = 1 - priceNow/priceOrig;
        return ((int)(discount*100));
    }



}
