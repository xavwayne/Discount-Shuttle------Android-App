package db;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import model.Customer;
import model.Product;
import model.Seller;

/**
 * interface providing methods to search data from database
 * @author xiaoyuw
 *
 */
public interface Search {
    
    public Customer searchCustomer(String username);
    
    public Seller searchSellerByUsername(String username);
    
    public ArrayList<Seller> searchSellerByType(Integer type) ;
    
    public Integer searchTypeID(String type);
    
    public LinkedHashMap<Integer, Product> searchProductByType(int type);
    
    public LinkedHashMap<Integer, Product> searchProductBySeller(String seller) ;

}
