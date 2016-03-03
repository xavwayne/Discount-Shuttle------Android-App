package db;

import model.Product;
import model.Seller;

/**
 * interface providing methods to insert data to database
 * @author xiaoyuw
 *
 */
public interface Insert {

    public void insertSeller(Seller s);
    
    public void insertProduct(Product p, String seller);
    
    public void insertType(String type);
}
