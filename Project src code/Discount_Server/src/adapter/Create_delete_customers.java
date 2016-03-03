package adapter;

import exception.DiscountServerException;

/**
 * Created by Joe on 7/17/2015.
 */
public interface Create_delete_customers {
    public boolean Add_a_customer(String username, String name, String password, String gender,
                                  String email, String image_url, String telephone)throws DiscountServerException;
    public boolean Delete_a_customer(String username)throws DiscountServerException;
}
