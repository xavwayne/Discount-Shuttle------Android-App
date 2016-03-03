package adapter;


import java.util.LinkedHashMap;

import db.DBHelper;
import exception.DiscountServerException;
import model.Product;
import model.Seller;
import model.Sellers_list;

/**
 * Created by Joe on 7/17/2015.
 */
public abstract class Proxy_sellers {
    private static Sellers_list sellers_list;
    private static DBHelper dbhelper;

    public void Create_sellers_list(String path){
    	sellers_list = new Sellers_list();
    	dbhelper = new DBHelper(path);
    }
    
    public boolean Delete_a_seller(String username) throws DiscountServerException {
    	if(sellers_list.Delete_a_seller(username)){
    		dbhelper.deleteSeller(username);
    		return true;
    	}
    	else
    		throw new DiscountServerException(2);
    }

    public boolean Add_a_seller(String username, String password, String email, String telephone,
                                String store_name, String store_icon_url, String introduction,
                                String Audio_url, int store_type, String latitude,
                                String longitude) throws DiscountServerException {
    	
    	Seller seller = new Seller(username,password,email,telephone,store_name,store_icon_url,introduction,Audio_url,
    			                    store_type,latitude,longitude);
    	
    	if(sellers_list.Add_a_seller(seller)){
    		dbhelper.insertSeller(seller);
    		return true;
    	}
    	else 
            throw new DiscountServerException(1);
    }

    public Seller Read_a_Seller(String username, String password) {
        Seller seller = sellers_list.Get_a_seller(username);
        if(seller != null){
           if(password.equals(seller.getPassword())){
        	   return seller;
           }
        }
        return null;
    }
    
    public Seller Read_a_Seller(String username) {
        Seller seller = sellers_list.Get_a_seller(username);
        if(seller != null){
        	   return seller;        
        }
        return null;
    }

    public boolean Update_a_seller(String username, String password, String email, String telephone, String store_name, 
    		                       String store_icon_url, String introduction, int store_type,String latitude,String longitude
    		                       ,String Audio_url) throws DiscountServerException {
        Seller seller = sellers_list.Get_a_seller(username);
    	if(seller!=null){
        	seller = new Seller(username,password,email,telephone,store_name,store_icon_url,introduction,Audio_url,
        			            store_type,latitude,longitude);
        	sellers_list.Update_a_seller(seller);
        	dbhelper.updateSeller(seller);
        	return true;
        }
        else
        	throw new DiscountServerException(2);
    }


    public boolean Add_a_product(String username, String image_url, double regular_price, double price_now, String name, String introduction) throws DiscountServerException {
    	Product product = new Product(image_url,regular_price,price_now,name,introduction);
    	Seller seller = sellers_list.Get_a_seller(username);
    	if(seller!=null){
           boolean if_product_existed=false;
           if(seller.getProducts()!=null){
              for(int i=0;i<seller.getProducts().size();i++){
        	      if(seller.getProducts().get(i).getName().equals(product.getName()))
        	          if_product_existed = true;
              }
           }
           if(!if_product_existed){
    	      seller.Add_a_product(product);
    	      sellers_list.Update_a_seller(seller);
    	      dbhelper.insertProduct(product, username);
    	      return true;
           }
           else 
        	   throw new DiscountServerException(3);
    	}
    	else
    		throw new DiscountServerException(2);
    }

    public boolean Delete_a_product(String username, String name) throws DiscountServerException {
    	Seller seller = sellers_list.Get_a_seller(username);
    	if(seller!=null){
    		if(seller.Delete_a_product(name)){
    			sellers_list.Delete_a_seller(seller.getUsername());
    			sellers_list.Add_a_seller(seller);
    			dbhelper.deleteProduct(name, username);
    			return true;
    		}
    		else throw new DiscountServerException(4);
    	}
    	else
    		throw new DiscountServerException(2);
    }


    public boolean Update_a_product(String username, String original_Name, String image_url, double regular_price, double price_now, String new_name, String introduction) throws DiscountServerException {
    	Seller seller = sellers_list.Get_a_seller(username);
    	if(seller!=null){
    		if(seller.Delete_a_product(original_Name)){
    			Product product = new Product(image_url,regular_price,price_now,new_name,introduction);
    			seller.Add_a_product(product);
    			sellers_list.Update_a_seller(seller);
    			dbhelper.deleteProduct(original_Name, username);
    			dbhelper.insertProduct(product, username);
    			return true;
    		}
    		else 
    			throw new DiscountServerException(4);
    	}
    	else
    		throw new DiscountServerException(2);
    }
    
    public void LoadHashMap(LinkedHashMap<String, Seller> loaded){
    	sellers_list.LoadHashMap(loaded);
    }
    
    public String toString(){
    	return sellers_list.toString();
    }
}
