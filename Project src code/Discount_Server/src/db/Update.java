package db;

import model.Customer;
import model.Product;
import model.Seller;

/**
 * interface providing methods to update information in the database
 * @author xiaoyuw
 *
 */
public interface Update {
    
    public void updateCustomer(Customer c);
    
    public void updateSeller(Seller s);
    
    public void updateProduct(Product p, String seller);

}
