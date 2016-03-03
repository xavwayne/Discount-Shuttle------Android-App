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
import adapter.Read_customers;
import adapter.Read_sellers;
import db.DBHelper;
import model.Customer;
import model.Seller;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public Login() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username;
		String password;
		String user_type;
		String Path = getServletContext().getRealPath("sql_command.properties");
		DBHelper dbhelper = new DBHelper(Path);
		PrintWriter out = response.getWriter();
		
		username = (String)request.getParameter("username");
		password = (String)request.getParameter("password");
		user_type = (String)request.getParameter("usertype");
		
		if(username.equals("") || password.equals("")){
			out.append("Incorrect user type, username or password");
			return;
		}
		
		if(user_type.equals("Customer")){
			LinkedHashMap<String, Customer> Customers_Map = new LinkedHashMap<String, Customer>();
			if(dbhelper.synCustomer()!=null){
			    Customers_Map.putAll(dbhelper.synCustomer());
			}
			Build_customers customers = new Build_customers();
			customers.Create_customers_list(Path);
			customers.LoadHashMap(Customers_Map);
			Read_customers RC = new Build_customers();
			Customer temp = RC.Read_a_customer(username, password);
			if(temp != null){
				out.append("You have logged in successfully");
			}
			else{
				out.append("Incorrect user type, username or password");
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
            Read_sellers RS = new Build_sellers();
            Seller seller = RS.Read_a_Seller(username, password);
			if(seller!=null){
				out.append("You have logged in successfully");
			}
			else{
			    out.append("Incorrect user type, username or password");
			}
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
