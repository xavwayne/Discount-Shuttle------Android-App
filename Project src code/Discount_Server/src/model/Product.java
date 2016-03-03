package model;

/**
 * Created by Joe on 7/17/2015.
 */
public class Product {
    private String image_url;
    private double regular_price;
    private double price_now;
    private String name;
    private String introduction;

    public Product(String image_url, double regular_price, double price_now, String name,
            String introduction){
        this.image_url = image_url;
        this.regular_price = regular_price;
        this.price_now = price_now;
        this.name = name;
        this.introduction = introduction;

    }

    public void setters(String image_url, double regular_price, double price_now, String name,
                        String introduction){
        this.image_url = image_url;
        this.regular_price = regular_price;
        this.price_now = price_now;
        this.name = name;
        this.introduction = introduction;
    }

    public String getImage_url(){
        return image_url;
    }

    public String getName(){
        return name;
    }

    public String getIntroduction(){
        return introduction;
    }

    public double getRegular_price(){
        return regular_price;
    }

    public double getPrice_now(){
        return price_now;
    }
    
    public String toString(){
    	String product="";
    	product += "name:" + name + "\n";
    	product += "image_url:" + image_url + "\n";
    	product += "regular_price:" + regular_price + "\n";
    	product += "price_now:" + price_now + "\n";
    	product += "introduction:" + introduction + "\n";
    	return product;
    }
}

