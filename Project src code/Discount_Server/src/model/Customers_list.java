package model;

import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Created by Joe on 7/17/2015.
 */
public class Customers_list {
    private LinkedHashMap<String, Customer> customers_list;

    public Customers_list(){
    	customers_list = new LinkedHashMap<String, Customer>();
    }
    
    public boolean Add_a_customer(Customer customer){
    	String username = customer.getUsername();
    	if(customers_list.containsKey(username)){
    		return false;
    	}
    	else{
    	   customers_list.put(customer.getUsername(), customer);
    	   return true;
    	}
    }
    
    public Customer Get_a_customer(String username){
    	Customer customer;
    	customer = customers_list.get(username);
    	return customer;
    }
    
    public boolean Delete_a_customer(String username){
        if(customers_list.containsKey(username)){
            customers_list.remove(username);
            return true;
        }
        else return false;
    }
    
    public boolean Update_a_customer(Customer customer){
    	String username = customer.getUsername();
    	if(customers_list.containsKey(username)){
    		customers_list.put(customer.getUsername(), customer);
            return true;
        }
    	else return false;
    }
    
    public String toString(){
    	String customers = "";
    	Set<String>set1=customers_list.keySet();
    	for(String key : set1){
    		Customer customer = customers_list.get(key);
    		customers += customer.toString() + "\n";
    	}
    	customers +="\n";
    	return customers;
    }
    
    public void LoadHashMap(LinkedHashMap<String, Customer> loaded){
    	customers_list.putAll(loaded);
    }

}
