package db;

import java.sql.*;
import model.*;
import java.util.*;
import java.io.*;

/**
 * ProxyDB implements all CRUD methods to manipulate database
 * @author xiaoyuw
 */
public class ProxyDB {

    protected static final String URL = "jdbc:mysql://127.0.0.1:3306/";
    protected static final String SCHEM = "discount_shuttle";
    protected static final String USER = "CaptainTeemo";   //username
    protected static final String PSWD = "12345";          //password

    protected Properties command;
    private String query;

    

    /**
     * Connect to database. if the database exists, then do nothing. if the
     * database does not exist, then create the database and tables.
     */
    public ProxyDB(String path) {

	try {
	    this.command = new Properties();
	    this.command.load(new FileInputStream(path));
	} catch (FileNotFoundException e1) {
	    e1.printStackTrace();
	} catch (IOException e1) {
	    e1.printStackTrace();
	}

	try {
	    Class.forName("com.mysql.jdbc.Driver");
	} catch (ClassNotFoundException e) {
	    System.out.println("Could not load Driver");
	    e.printStackTrace();
	}

	String url = URL + SCHEM;

	try {
	    Connection conn = DriverManager.getConnection(url, USER, PSWD);
	    conn.close();

	} catch (SQLException e) {
	    System.out.println(" Database " + SCHEM + " not found!");

	    System.out.println("Create database " + SCHEM + "...");
	    this.create();
	}
		

    }

    /**
     * Create database and tables
     */
    public void create() {

	Connection conn = null;
	try {
	    conn = DriverManager.getConnection(URL, USER, PSWD);
	    Statement stat = conn.createStatement();
	    query = command.getProperty("CreateSCH");
	    stat.execute(query);

	    query = command.getProperty("CreateTab_CUSTOMER");
	    stat.execute(query);

	    query = command.getProperty("CreateTab_TYPE");
	    stat.execute(query);

	    query = command.getProperty("CreateTab_SELLER");
	    stat.execute(query);

	    query = command.getProperty("CreateTab_PRODUCT");
	    stat.execute(query);
	    
	    this.insertType("Food");
	    this.insertType("Pharmacy");
	    this.insertType("Cosmetics");
	    this.insertType("Drink");
	    this.insertType("Clothes");
	    this.insertType("Default");

	    stat.close();
	    conn.close();
	    System.out.println("Create Complete!");

	} catch (SQLException e) {
	    System.out.println("Could not connect to server");
	    e.printStackTrace();
	}

    }

    /**
     * Insert a customer into database
     * 
     * @param c
     *            a Customre instance
     */
    public void insertCustomer(Customer c) {
	String url = URL + SCHEM;
	String username = c.getUsername();
	String password = c.getPassword();
	String name = c.getName();
	String gender = c.getGender();
	String email = c.getEmail();
	String tele = c.getTelephone();
	String image = c.getImage_url();
	Connection conn = null;

	try {
	    conn = DriverManager.getConnection(url,USER,PSWD);
	    Statement stat = conn.createStatement();

	    query = command.getProperty("InsertCustomer");
	    query = query.replace("%username", username);
	    query = query.replace("%password", password);
	    query = query.replace("%name", name);
	    query = query.replace("%gender", gender);
	    query = query.replace("%email", email);
	    query = query.replace("%telephone", tele);
	    query = query.replace("%image", image);

	    System.out.println(query);
	    stat.execute(query);

	    stat.close();
	    conn.close();

	} catch (SQLException e) {
	    e.printStackTrace();
	}

    }

    /**
     * Insert a seller into database
     * 
     * @param s
     *            a Seller instance
     */
    public void insertSeller(Seller s) {
	String url = URL + SCHEM;
	String username = s.getUsername();
	String password = s.getPassword();
	String name = s.getStore_name();
	String email = s.getEmail();
	String icon = s.getStore_icon_url();
	String intro = s.getIntroduction();
	String tele = s.getTelephone();
	String latitude = s.get_latitude();
	String longitude = s.get_longitude();
	String audio=s.getAudio();
	Integer type = s.getStore_type();
	ArrayList<Product> products = s.getProducts();

	Connection conn = null;

	try {
	    conn = DriverManager.getConnection(url,USER,PSWD);
	    Statement stat = conn.createStatement();

	    query = command.getProperty("InsertSeller");

	    query = query.replace("%username", username);
	    query = query.replace("%password", password);
	    query = query.replace("%email", email);
	    query = query.replace("%tele", tele);
	    query = query.replace("%storename", name);
	    query = query.replace("%icon", icon);
	    query = query.replace("%intro", intro);
	    query = query.replace("%latitude", latitude);
	    query = query.replace("%longitude", longitude);
	    query = query.replace("%type", type.toString());
	    query = query.replace("%audio", audio);

	    

	    System.out.println(query);
	    stat.execute(query);

	    for (Product x : products) {
		insertProduct(x, username);
	    }

	    stat.close();
	    conn.close();

	} catch (SQLException e) {
	    e.printStackTrace();
	}

    }

    /**
     * Insert a product into database
     * 
     * @param p
     *            a Product instance
     * @param seller
     *            seller's username
     */
    public void insertProduct(Product p, String seller) {
	String url = URL + SCHEM;
	String name = p.getName();
	Double regular_price = p.getRegular_price();
	Double present_price = p.getPrice_now();
	String image = p.getImage_url();
	String intro = p.getIntroduction();

	Connection conn = null;

	try {
	    conn = DriverManager.getConnection(url,USER,PSWD);
	    Statement stat = conn.createStatement();

	    query = command.getProperty("InsertProduct");
	    query = query.replace("%name", name);
	    query = query.replace("%reg_price", regular_price.toString());
	    query = query.replace("%now_price", present_price.toString());
	    query = query.replace("%image", image);
	    query = query.replace("%intro", intro);
	    query = query.replace("%seller", seller);

	    System.out.println(query);
	    stat.execute(query);

	    stat.close();
	    conn.close();

	} catch (SQLException e) {
	    e.printStackTrace();
	}

    }

    /**
     * declare a new type in the database
     * 
     * @param type
     *            type name
     */
    public void insertType(String type) {

	String url = URL + SCHEM;
	Connection conn = null;

	try {
	    conn = DriverManager.getConnection(url,USER,PSWD);
	    Statement stat = conn.createStatement();

	    query = command.getProperty("InsertType");
	    query = query.replace("%name", type);

	    System.out.println(query);
	    stat.execute(query);

	    stat.close();
	    conn.close();

	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    /**
     * delete a custome from database
     * 
     * @param c
     *            a customer's username
     */
    public void deleteCustomer(String c) {

	String url = URL + SCHEM;
	Connection conn = null;

	try {
	    conn = DriverManager.getConnection(url,USER,PSWD);
	    Statement stat = conn.createStatement();

	    query = command.getProperty("Del_Customer");
	    query = query.replace("%username", c);

	    System.out.println(query);
	    stat.execute(query);

	    stat.close();
	    conn.close();

	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    /**
     * delete a seller from database
     * 
     * @param s
     *            seller's username
     */
    public void deleteSeller(String s) {

	String url = URL + SCHEM;
	Connection conn = null;

	try {
	    conn = DriverManager.getConnection(url,USER,PSWD);
	    Statement stat = conn.createStatement();

	    query = command.getProperty("Del_Seller");
	    query = query.replace("%username", s);

	    System.out.println(query);
	    stat.execute(query);

	    stat.close();
	    conn.close();

	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    /**
     * delete a product from database
     * 
     * @param p
     *            product name
     * @param s
     *            seller's username
     */
    public void deleteProduct(String p, String s) {
	String url = URL + SCHEM;
	Connection conn = null;

	try {
	    conn = DriverManager.getConnection(url,USER,PSWD);
	    Statement stat = conn.createStatement();

	    query = command.getProperty("Del_Product");
	    query = query.replace("%name", p);
	    query = query.replace("%seller", s);

	    System.out.println(query);
	    stat.execute(query);

	    stat.close();
	    conn.close();

	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    /**
     * delete a type from databae
     * 
     * @param t
     *            type name
     */
    public void deleteType(String t) {
	String url = URL + SCHEM;
	Connection conn = null;

	try {
	    conn = DriverManager.getConnection(url,USER,PSWD);
	    Statement stat = conn.createStatement();

	    query = command.getProperty("Del_Type");
	    query = query.replace("%name", t);

	    System.out.println(query);
	    stat.execute(query);

	    stat.close();
	    conn.close();

	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    /**
     * update a customer in the database
     * 
     * @param c
     *            a customer instance
     */
    public void updateCustomer(Customer c) {
	String url = URL + SCHEM;
	String username = c.getUsername();
	String password = c.getPassword();
	String name = c.getName();
	String gender = c.getGender();
	String email = c.getEmail();
	String tele = c.getTelephone();
	String image = c.getImage_url();
	Connection conn = null;

	try {
	    conn = DriverManager.getConnection(url,USER,PSWD);
	    Statement stat = conn.createStatement();

	    query = command.getProperty("UpdateCustomer");
	    query = query.replace("%username", username);
	    query = query.replace("%password", password);
	    query = query.replace("%name", name);
	    query = query.replace("%gender", gender);
	    query = query.replace("%email", email);
	    query = query.replace("%tele", tele);
	    query = query.replace("%image", image);

	    System.out.println(query);
	    stat.execute(query);

	    stat.close();
	    conn.close();

	} catch (SQLException e) {
	    e.printStackTrace();
	}

    }

    /**
     * update a seller in the database
     * 
     * @param s
     *            a seller instance
     */
    public void updateSeller(Seller s) {

	String url = URL + SCHEM;
	String username = s.getUsername();
	String password = s.getPassword();
	String name = s.getStore_name();
	String email = s.getEmail();
	String icon = s.getStore_icon_url();
	String intro = s.getIntroduction();
	String tele = s.getTelephone();
	String latitude = s.get_latitude();
	String longitude = s.get_longitude();
	String audio=s.getAudio();
	Integer type = s.getStore_type();

	Connection conn = null;

	try {
	    conn = DriverManager.getConnection(url,USER,PSWD);
	    Statement stat = conn.createStatement();

	    query = command.getProperty("UpdateSeller");

	    query = query.replace("%username", username);
	    query = query.replace("%password", password);
	    query = query.replace("%email", email);
	    query = query.replace("%tele", tele);
	    query = query.replace("%storename", name);
	    query = query.replace("%icon", icon);
	    query = query.replace("%intro", intro);
	    query = query.replace("%latitude", latitude);
	    query = query.replace("%longitude", longitude);
	    query = query.replace("%type", type.toString());
	    query = query.replace("%audio", audio);

	    System.out.println(query);
	    stat.execute(query);

	    stat.close();
	    conn.close();

	} catch (SQLException e) {
	    e.printStackTrace();
	}

    }

    /**
     * update a product in the database
     * 
     * @param p
     *            a product instance
     * @param seller
     *            seller's username
     */
    public void updateProduct(Product p, String seller) {

	String url = URL + SCHEM;
	String name = p.getName();
	Double regular_price = p.getRegular_price();
	Double present_price = p.getPrice_now();
	String image = p.getImage_url();
	String intro = p.getIntroduction();

	Connection conn = null;

	try {
	    conn = DriverManager.getConnection(url,USER,PSWD);
	    Statement stat = conn.createStatement();

	    query = command.getProperty("UpdateProduct");
	    query = query.replace("%name", name);
	    query = query.replace("%reg_price", regular_price.toString());
	    query = query.replace("%now_price", present_price.toString());
	    query = query.replace("%image", image);
	    query = query.replace("%intro", intro);
	    query = query.replace("%seller", seller);

	    System.out.println(query);
	    stat.execute(query);

	    stat.close();
	    conn.close();

	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    /**
     * search for a customer by its username
     * 
     * @param username
     *            customr's username
     * @return customer instance
     */
    public Customer searchCustomer(String username) {

	Customer customer = null;
	String url = URL + SCHEM;

	String password;
	String name;
	String gender;
	String email;
	String tele;
	String image;
	Connection conn = null;

	try {
	    conn = DriverManager.getConnection(url,USER,PSWD);
	    Statement stat = conn.createStatement();

	    query = command.getProperty("Select_Customer");
	    query = query.replace("%username", username);

	    System.out.println(query);
	    ResultSet rs = stat.executeQuery(query);
	    if (rs.next()) {
		password = rs.getString(2);
		name = rs.getString(3);
		gender = rs.getString(4);
		email = rs.getString(5);
		tele = rs.getString(6);
		image = rs.getString(7);

		customer = new Customer(username, password, name, gender,
			email, image, tele);
	    }

	    rs.close();
	    stat.close();
	    conn.close();
	    return customer;

	} catch (SQLException e) {
	    e.printStackTrace();
	    return null;
	}

    }

    /**
     * search for a seller by its username
     * 
     * @param username
     *            sller's username
     * @return a seller instance
     */
    public Seller searchSellerByUsername(String username) {
	String url = URL + SCHEM;
	Seller seller = null;
	String password = null;
	String name = null;
	String email = null;
	String icon = null;
	String intro = null;
	String tele = null;
	String latitude = null;
	String longitude = null;
	String audio=null;
	Integer type = null;

	Connection conn = null;

	try {
	    conn = DriverManager.getConnection(url,USER,PSWD);
	    Statement stat = conn.createStatement();

	    query = command.getProperty("Select_Seller_by_username");

	    query = query.replace("%username", username);

	    System.out.println(query);

	    ResultSet rs = stat.executeQuery(query);

	    if (rs.next()) {
		password = rs.getString(2);
		email = rs.getString(3);
		tele = rs.getString(4);
		name = rs.getString(5);
		icon = rs.getString(6);
		audio=rs.getString(7);
		intro = rs.getString(8);
		latitude = rs.getString(9);
		longitude = rs.getString(10);
		type = rs.getInt(11);
		rs.close();

		seller = new Seller(username, password, email, tele, name,
			icon, intro, audio, type, latitude, longitude);
	    }

	    stat.close();
	    conn.close();
	    return seller;

	} catch (SQLException e) {
	    e.printStackTrace();
	    return null;
	}
    }

    /**
     * search for sellers by its storetype
     * 
     * @param type
     *            the store type
     * @return all mathched sellers
     */
    public ArrayList<Seller> searchSellerByType(Integer type) {
	ArrayList<Seller> sellers = new ArrayList<Seller>();

	String url = URL + SCHEM;

	Seller seller = null;
	String username = null;
	String password = null;
	String name = null;
	String email = null;
	String icon = null;
	String intro = null;
	String tele = null;
	String latitude = null;
	String longitude = null;
	String audio=null;

	Connection conn = null;

	try {
	    conn = DriverManager.getConnection(url,USER,PSWD);
	    Statement stat = conn.createStatement();

	    query = command.getProperty("Select_Seller_by_type");

	    query = query.replace("%type", type.toString());

	    System.out.println(query);

	    ResultSet rs = stat.executeQuery(query);

	    while (rs.next()) {
		username = rs.getString(1);
		password = rs.getString(2);
		email = rs.getString(3);
		tele = rs.getString(4);
		name = rs.getString(5);
		icon = rs.getString(6);
		audio=rs.getString(7);
		intro = rs.getString(8);
		latitude = rs.getString(9);
		longitude = rs.getString(10);

		seller = new Seller(username, password, email, tele, name,
			icon, intro, audio, type, latitude, longitude);
		sellers.add(seller);
	    }

	    rs.close();
	    stat.close();
	    conn.close();
	    return sellers;

	} catch (SQLException e) {
	    e.printStackTrace();
	    return null;
	}

    }

    /**
     * get a type's primary key
     * 
     * @param type
     *            type name
     * @return the primary key
     */
    public Integer searchTypeID(String type) {
	String url = URL + SCHEM;
	Integer typeid = null;

	Connection conn = null;

	try {
	    conn = DriverManager.getConnection(url,USER,PSWD);
	    Statement stat = conn.createStatement();

	    query = command.getProperty("Select_Type");
	    query = query.replace("%name", type);

	    System.out.println(query);

	    ResultSet rs = stat.executeQuery(query);

	    if (rs.next()) {
		typeid = rs.getInt(1);

	    }

	    stat.close();
	    conn.close();
	    return typeid;

	} catch (SQLException e) {
	    e.printStackTrace();
	    return null;
	}

    }

    /**
     * search for product by its type
     * 
     * @param type
     *            product type(store type)
     * @return all matched products with its primary key
     */
    public LinkedHashMap<Integer, Product> searchProductByType(int type) {

    	LinkedHashMap<Integer, Product> productsAll = new LinkedHashMap<Integer, Product>();
    	LinkedHashMap<Integer, Product> products = new LinkedHashMap<Integer, Product>();

	ArrayList<Seller> sellers = this.searchSellerByType(type);
	for (Seller x : sellers) {
	    String username = x.getUsername();
	    products = this.searchProductBySeller(username);
	    productsAll.putAll(products);
	}

	return productsAll;
    }

    /**
     * search for product belonging to a seller
     * 
     * @param seller
     *            seller's username
     * @return all products with its primary key belonging to a seller
     */
    public LinkedHashMap<Integer, Product> searchProductBySeller(String seller) {
    	LinkedHashMap<Integer, Product> products = new LinkedHashMap<Integer, Product>();
	Product product;

	int id;
	String url = URL + SCHEM;
	String name;
	Double regular_price;
	Double present_price;
	String image;
	String intro;

	Connection conn = null;

	try {
	    conn = DriverManager.getConnection(url,USER,PSWD);
	    Statement stat = conn.createStatement();

	    query = command.getProperty("Select_Product_by_Seller");
	    query = query.replace("%seller", seller);

	    System.out.println(query);

	    ResultSet rs = stat.executeQuery(query);

	    while (rs.next()) {
		id = rs.getInt(1);
		name = rs.getString(2);
		regular_price = rs.getDouble(3);
		present_price = rs.getDouble(4);
		image = rs.getString(5);
		intro = rs.getString(6);

		product = new Product(image, regular_price, present_price,
			name, intro);

		products.put(id, product);
	    }

	    stat.close();
	    conn.close();
	    return products;

	} catch (SQLException e) {
	    e.printStackTrace();
	    return null;
	}
    }

    // synchronize Linkedhashmap

    /**
     * read all customers from database to application memory
     * 
     * @return all customers with their username
     */
    public LinkedHashMap<String, Customer> synCustomer() {

    	LinkedHashMap<String, Customer> customers = new LinkedHashMap<String, Customer>();
	Customer customer = null;
	String url = URL + SCHEM;
	String username;
	String password;
	String name;
	String gender;
	String email;
	String tele;
	String image;
	Connection conn = null;

	try {
	    conn = DriverManager.getConnection(url,USER,PSWD);
	    Statement stat = conn.createStatement();

	    query = command.getProperty("Select_Customer_All");

	    System.out.println(query);
	    ResultSet rs = stat.executeQuery(query);
	    while (rs.next()) {
		username = rs.getString(1);
		password = rs.getString(2);
		name = rs.getString(3);
		gender = rs.getString(4);
		email = rs.getString(5);
		tele = rs.getString(6);
		image = rs.getString(7);

		customer = new Customer(username, password, name, gender,
			email, image, tele);
		customers.put(username, customer);
	    }

	    rs.close();
	    stat.close();
	    conn.close();
	    return customers;

	} catch (SQLException e) {
	    e.printStackTrace();
	    return null;
	}

    }

    /**
     * read all sellers from database to application memory
     * 
     * @return all sellers with their username
     */
    public LinkedHashMap<String, Seller> synSeller() {

	String url = URL + SCHEM;

	Seller seller = null;
	String username = null;
	String password = null;
	String name = null;
	String email = null;
	String icon = null;
	String intro = null;
	String tele = null;
	String latitude = null;
	String longitude = null;
	String audio=null;
	int type;
	ArrayList<Product> products = new ArrayList<Product>();
	LinkedHashMap<String, Seller> sellers = new LinkedHashMap<String, Seller>();

	Connection conn = null;

	try {
	    conn = DriverManager.getConnection(url,USER,PSWD);
	    Statement stat = conn.createStatement();

	    query = command.getProperty("Select_Seller_All");

	    System.out.println(query);

	    ResultSet rs = stat.executeQuery(query);

	    while (rs.next()) {
		username = rs.getString(1);
		password = rs.getString(2);
		email = rs.getString(3);
		tele = rs.getString(4);
		name = rs.getString(5);
		icon = rs.getString(6);
		audio=rs.getString(7);
		intro = rs.getString(8);
		latitude = rs.getString(9);
		longitude = rs.getString(10);
		type = rs.getInt(11);

		seller = new Seller(username, password, email, tele, name,
			icon, intro, audio, type, latitude, longitude);
		LinkedHashMap<Integer, Product> pros = this
			.searchProductBySeller(username);

		products.addAll(pros.values());
		for (Product x : products) {
		    seller.Add_a_product(x);
		}

		sellers.put(username, seller);
		products = new ArrayList<Product>();
	    }

	    rs.close();
	    stat.close();
	    conn.close();
	    return sellers;

	} catch (SQLException e) {
	    e.printStackTrace();
	    return null;
	}

    }

}
