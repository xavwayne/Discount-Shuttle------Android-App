package db;

/**
 * interface providing methods to delete from database
 * @author xiaoyuw
 *
 */
public interface Delete {

    public void deleteCustomer(String c);
    
    public void deleteSeller(String s);
    
    public void deleteProduct(String p, String s);
    
    public void deleteType(String t) ;
}
