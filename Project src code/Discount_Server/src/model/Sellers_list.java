package model;

import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Created by Joe on 7/17/2015.
 */
public class Sellers_list {
     private LinkedHashMap<String, Seller> sellers_list;

    public Sellers_list(){
    	sellers_list = new LinkedHashMap<String, Seller>();
    }
    
    public boolean Add_a_seller(Seller seller){
    	String username = seller.getUsername();
    	if(sellers_list.containsKey(username)){
    		return false;
    	}
    	else{
    		sellers_list.put(seller.getUsername(), seller);
    	   return true;
    	}
    }
    
    public Seller Get_a_seller(String username){
    	Seller seller;
    	seller = sellers_list.get(username);
    	return seller;
    }
    
    public boolean Delete_a_seller(String username){
        if(sellers_list.containsKey(username)){
        	sellers_list.remove(username);
            return true;
        }
        else return false;	
    }
    
    public boolean Update_a_seller(Seller seller){
    	String username = seller.getUsername();
    	if(sellers_list.containsKey(username)){
    		sellers_list.put(seller.getUsername(), seller);
            return true;
        }
    	else return false;
    }
    
    public void LoadHashMap(LinkedHashMap<String, Seller> loaded){
    	sellers_list.putAll(loaded);
    }
    
    public String toString(){
    	String sellers = "";
    	Set<String>set1=sellers_list.keySet();
    	for(String key : set1){
    		Seller seller = sellers_list.get(key);
    		sellers += seller.toString() + "\n";
    	}
    	sellers += "\n";
    	return sellers;
    }
}
