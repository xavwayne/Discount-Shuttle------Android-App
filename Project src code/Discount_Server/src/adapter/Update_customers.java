package adapter;

import exception.DiscountServerException;

/**
 * Created by Joe on 7/17/2015.
 */
public interface Update_customers {
    public boolean Update_a_customer(String username, String password,String name, String gender,
                                     String email, String image_url, String telephone, int user_type)throws DiscountServerException;

}
