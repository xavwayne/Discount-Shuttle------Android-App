package adapter;

import java.util.LinkedHashMap;

import db.DBHelper;
import exception.DiscountServerException;
import model.Customer;
import model.Customers_list;

/**
 * Created by Joe on 7/17/2015.
 */
public abstract class Proxy_customers {
    private static Customers_list customers_list;
    private static DBHelper dbhelper;
    
    public void Create_customers_list(String path){
    	customers_list = new Customers_list();
    	dbhelper = new DBHelper(path);
    };

    public boolean Add_a_customer(String username, String name, String password, String gender, String email,
    		                      String image_url, String telephone) throws DiscountServerException {
         Customer customer = new Customer(username,password,name,gender,email,image_url,
        		                          telephone);
         if(customers_list.Add_a_customer(customer)){
        	 dbhelper.insertCustomer(customer);
        	 return true;
         }
         throw new DiscountServerException(1);
    }

    public boolean Delete_a_customer(String username) throws DiscountServerException {
    	if(customers_list.Delete_a_customer(username)){
    		dbhelper.deleteCustomer(username);
    		return true;
    	}
    	else
    		throw new DiscountServerException(5);
    }

    public Customer Read_a_customer(String username, String password) {
        Customer customer = customers_list.Get_a_customer(username);
		if (customer != null) {
			if (password.equals(customer.getPassword())) {
				return customer;
			}
		}
    	return null;
    	
    }
    
    public Customer Read_a_customer(String username) {
        Customer customer = customers_list.Get_a_customer(username);
		if (customer != null) {
				return customer;
		}
		else
    	return null;
    }

    public boolean Update_a_customer(String username, String password, String name, String gender, 
    		                         String email, String image_url, String telephone, int user_type) throws DiscountServerException {
    	Customer customer = customers_list.Get_a_customer(username);
    	if(customer!=null){
    		customer.set_attributes(password, name, gender, email, image_url, telephone, user_type);
    		customers_list.Update_a_customer(customer);
    		dbhelper.updateCustomer(customer);
    		return true;
    	}
    	throw new DiscountServerException(5);
    }
    
    public void LoadHashMap(LinkedHashMap<String, Customer> loaded){
    	customers_list.LoadHashMap(loaded);
    }
    
    public String toString(){
    	return customers_list.toString();
    }
}
