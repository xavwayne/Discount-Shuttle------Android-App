package com.example.jiyushi1.dis.model;

/**
 * Created by jiyushi1 on 7/28/15.
 */
public class SearchOutcomesShopDetail {
    private String priceOrig;
    private String priceNow;
    private String name;
    private String introduction;
    private String imageUri;


    public SearchOutcomesShopDetail(String name,String introduction,String priceOrig,String priceNow,
            String imageUri){

        setPriceOrig(priceOrig);
        setPriceNow(priceNow);
        setImageUri(imageUri);
        setName(name);
        setIntroduction(introduction);

    }
    public String getPriceOrig() {
        return priceOrig;
    }

    public void setPriceOrig(String priceOrig) {
        this.priceOrig = priceOrig;
    }

    public String getPriceNow() {
        return priceNow;
    }

    public void setPriceNow(String priceNow) {
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

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getDiscount(){

        double pN=  Double.valueOf(priceNow);
        double pO = Double.valueOf(priceOrig);
        double discount = 1 - pN/pO;
        int discounInt;
        discounInt = (int) (discount* 100);
        String discountStr = "" + discounInt +"%" ;
        return discountStr;

    }


}
