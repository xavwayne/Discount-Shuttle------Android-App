package adapter;

import exception.DiscountServerException;

/**
 * Created by Joe on 7/17/2015.
 */
public interface Update_sellers {
    public boolean Update_a_seller(String username, String password, String email, String telephone,
                                String store_name, String store_icon_url, String introduction,
                                int store_type,String latitude,String longitude, String Audio_url)throws DiscountServerException;
    public boolean Add_a_product(String username, String image_url, double regular_price,
                                 double price_now, String name, String introduction)throws DiscountServerException;
    public boolean Delete_a_product(String username, String name)throws DiscountServerException;
    public boolean Update_a_product(String username, String original_Name, String image_url, double regular_price,
                                    double price_now, String new_name, String introduction)throws DiscountServerException;
}
