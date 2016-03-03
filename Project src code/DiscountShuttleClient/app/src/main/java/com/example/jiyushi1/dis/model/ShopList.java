package com.example.jiyushi1.dis.model;

/**
 * Created by jiyushi1 on 7/23/15.
 */
public class ShopList {
    private String shopName;
    private String special;
    private int iconID;
    public int getIconID() {
        return iconID;
    }

    public void setIconID(int iconID) {
        this.iconID = iconID;
    }



    public ShopList(String shopName,String special,int iconID){
        setShopName(shopName);
        setSpecial(special);
        setIconID(iconID);

    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getSpecial() {
        return special;
    }

    public void setSpecial(String special) {
        this.special = special;
    }
}
