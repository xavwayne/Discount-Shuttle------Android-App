package adapter;

import model.Customer;

/**
 * Created by Joe on 7/17/2015.
 */
public interface Read_customers {
    public Customer Read_a_customer(String username, String password);
}
