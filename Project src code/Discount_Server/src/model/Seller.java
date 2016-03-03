package model;

import java.util.ArrayList;

/**
 * Created by Joe on 7/17/2015.
 */
public class Seller {
    private String username;
    private String password;
    private String email;
    private String telephone;
    private String store_name;
    private String store_icon_url;
    private String introduction;
    private String latitude;
    private String longitude;
    private String Audio_url;
    
    private int store_type;
    private ArrayList<Product> products;

    public Seller(){
    	
    }
    
    public Seller(String username, String password, String email, String telephone, String store_name,
           String store_icon_url, String introduction, String Audio_url, int store_type, String latitude, String longitutde){
        this.username = username;
        this.password = password;
        this.email = email;
        this.telephone = telephone;
        this.store_name = store_name;
        this.store_icon_url = store_icon_url;
        this.introduction = introduction;
        this.Audio_url = Audio_url;
        this.store_type = store_type;
        this.products = new ArrayList<Product>();
        this.latitude = latitude;
        this.longitude = longitutde;
    }

    public void setters(String username, String password, String email, String telephone, String store_name,
                   String store_icon_url, String introduction, String Audio_url, int store_type,
                        String latitude, String longitutde){
        this.username = username;
        this.password = password;
        this.email = email;
        this.telephone = telephone;
        this.store_name = store_name;
        this.store_icon_url = store_icon_url;
        this.introduction = introduction;
        this.Audio_url = Audio_url;
        this.store_type = store_type;
        this.latitude = latitude;
        this.longitude = longitutde;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public String getEmail(){
        return email;
    }

    public String getTelephone(){
        return telephone;
    }

    public String getStore_name(){
        return store_name;
    }

    public String getStore_icon_url(){
        return store_icon_url;
    }
    
    public String get_latitude(){
    	return latitude;
    }
    
    public String get_longitude(){
    	return longitude;
    }

    public String getIntroduction(){
        return introduction;
    }

    public int getStore_type(){
        return store_type;
    }

    public ArrayList<Product> getProducts(){
        return products;
    }
    
    public void Add_a_product(Product product){
    	products.add(product);
    };
    
    public boolean Delete_a_product(String name){
    	for(int i=0;i<products.size();i++){
    		if(name.equals(products.get(i).getName())){
    			products.remove(i);
    			return true;
    		}
    	}
    	return false;
    }
    
    public String toString(){
    	String seller = "";
    	seller += "username:" + username + "\n";
    	seller += "password:" + password + "\n";
    	seller += "email:" + email + "\n";
    	seller += "telephone:" + telephone + "\n";
    	seller += "store_name:" + store_name + "\n";
    	seller += "store_icon_url:" + store_icon_url + "\n";
    	seller += "introduction:" + introduction + "\n";
    	seller += "Audio_url:" + Audio_url + "\n";
    	seller += "store_type:" + store_type + "\n";
    	seller += "latitude:" + latitude + "\n";
    	seller += "longitude:" + longitude + "\n\n";
		if (products != null) {
			seller += "products:\n";
			for (int i = 0; i < products.size(); i++) {
				seller += products.get(i).toString() + "\n";
			}
		}
    	return seller;
    }

	public String getAudio() {
		return Audio_url;
	}
}