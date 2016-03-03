package adapter;

import exception.DiscountServerException;

/**
 * Created by Joe on 7/17/2015.
 */
public interface Create_delete_sellers {

    public boolean Add_a_seller(String username, String password, String email, String telephone,
                                String store_name, String store_icon_url, String introduction,
                                String Audio_url, int store_type, String latitude,
                                String longitude)throws DiscountServerException;
    public boolean Delete_a_seller(String username)throws DiscountServerException;
}
