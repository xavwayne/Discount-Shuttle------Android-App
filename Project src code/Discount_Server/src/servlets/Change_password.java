package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import adapter.Build_customers;
import adapter.Build_sellers;
import adapter.Update_customers;
import adapter.Update_sellers;
import db.DBHelper;
import email.SendMail;
import exception.DiscountServerException;
import model.Customer;
import model.Seller;

/**
 * Servlet implementation class Change_password
 */
@WebServlet("/Change_password")
public class Change_password extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public Change_password() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username;
		String old_password;
		String new_password;
		String user_type;
		
		PrintWriter out = response.getWriter();
		String Path = getServletContext().getRealPath("sql_command.properties");
		DBHelper dbhelper = new DBHelper(Path);
		
		username = (String)request.getParameter("username");
		old_password = (String)request.getParameter("oldpassword");
		new_password = (String)request.getParameter("newpassword");
		user_type = (String)request.getParameter("usertype");
		
		if(user_type.equals("Customer")){
			LinkedHashMap<String, Customer> Customers_Map = new LinkedHashMap<String, Customer>();
			if(dbhelper.synCustomer()!=null){
			    Customers_Map.putAll(dbhelper.synCustomer());
			}
			Build_customers customers = new Build_customers();
			customers.Create_customers_list(Path);
			customers.LoadHashMap(Customers_Map);
			Customer customer = customers.Read_a_customer(username);
			
			// Update
			if(customer.getPassword().equals(old_password)){
				Update_customers UC = new Build_customers();
				try {
					UC.Update_a_customer(username, new_password, customer.getName(), customer.getGender(), 
							                       customer.getEmail(), customer.getImage_url(), customer.getTelephone(), 1);
					SendMail Mail = new SendMail(customer.getEmail(),username, new_password,2);
		            Mail.start();
					out.append("Change Password Successful");
				} catch (DiscountServerException e) {
					out.append(e.getMsg());
				}
			}
			else{
				out.append("Your old password is incorrect");
			}
		}
	
		if(user_type.equals("Seller")){
			LinkedHashMap<String, Seller> Sellers_Map = new LinkedHashMap<String, Seller>();
			if (dbhelper.synSeller() != null) {
				Sellers_Map.putAll(dbhelper.synSeller());
			}
			Build_sellers Sellers = new Build_sellers();
			Sellers.Create_sellers_list(Path);
			Sellers.LoadHashMap(Sellers_Map);
			Seller seller = Sellers.Read_a_Seller(username);
			
			//Update
			if(seller.getPassword().equals(old_password)){
				Update_sellers US = new Build_sellers();
				try {
					US.Update_a_seller(username, new_password, seller.getEmail(), seller.getTelephone(),
							seller.getStore_name(), seller.getStore_icon_url(), seller.getIntroduction(),
							seller.getStore_type(), seller.get_latitude(), seller.get_longitude(), seller.getAudio());
					SendMail Mail = new SendMail(seller.getEmail(), username, new_password, 2);
					Mail.start();
					out.append("Change Password Successful");
				} catch (DiscountServerException e) {
					out.append(e.getMsg());
				}
			}
			else{
				out.append("Your old password is incorrect");
			}
		}
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
