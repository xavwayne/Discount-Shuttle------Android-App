package model;

/**
 * Created by Joe on 7/17/2015.
 */
public class Customer {
    private String username;
    private String name;
    private String password;
    private String gender;
    private String email;
    private String image_url;
    private String telephone;
    
    public Customer(){
    	
    	
    }
    
    public Customer(String username, String password,String name, String gender, String email,
                    String image_url, String telephone){
        this.username = username;
        this.name = name;
        this.password = password;
        this.gender = gender;
        this.email = email;
        this.image_url = image_url;
        this.telephone = telephone;
    }

    public void set_attributes(String password, String name, String gender, String email,
                               String image_url, String telephone, int user_type){
        this.name = name;
        this.password = password;
        this.gender = gender;
        this.email = email;
        this.image_url = image_url;
        this.telephone = telephone;
    }

    public String getUsername(){
        return username;
    }

    public String getName(){
        return name;
    }

    public String getPassword(){ return password; }

    public String getGender(){
        return gender;
    }

    public String getEmail(){
        return email;
    }

    public String getImage_url(){
        return image_url;
    }

    public String getTelephone(){
        return telephone;
    }

    
    public String toString(){
    	String customer="";
    	customer += "username:" + username + "\n";
    	customer += "name:" + name + "\n";
    	customer += "password:" + password + "\n";
    	customer += "gender:" + gender + "\n";
    	customer += "email:" + email + "\n";
    	customer += "image_url:" + image_url + "\n";
    	customer += "telephone:" + telephone + "\n";
    	return customer;
    }
}
