package com.example.jiyushi1.dis.model;

/**
 * Created by jiyushi1 on 7/27/15.
 */
public class SearchOutcomesShopList {

    private String shopName;
    private String introduction;
    private String imageUri;
    private String pk;

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }



    public SearchOutcomesShopList(String shopName,String introduction,String imageUri,String pk){
        setShopName(shopName);
        setImageUri(imageUri);
        setIntroduction(introduction);
        setPk(pk);
    }


    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
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


}
