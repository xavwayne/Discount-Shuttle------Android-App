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
import db.DBHelper;
import model.Customer;

/**
 * Servlet implementation class Read_a_customer
 */
@WebServlet("/Read_a_customer")
public class Read_a_customer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String customer_info;
    public Read_a_customer() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username;
		username = (String)request.getParameter("username");
		String Path = getServletContext().getRealPath("sql_command.properties");
		DBHelper dbhelper = new DBHelper(Path);
		PrintWriter out = response.getWriter();
		
		LinkedHashMap<String, Customer> Customers_Map = new LinkedHashMap<String, Customer>();
		if(dbhelper.synCustomer()!=null){
		    Customers_Map.putAll(dbhelper.synCustomer());
		}
		Build_customers customers = new Build_customers();
		customers.Create_customers_list(Path);
		customers.LoadHashMap(Customers_Map);
		
		Customer customer = customers.Read_a_customer(username);
		if(customer != null){
			assemble(customer);
			out.append(customer_info);
		}
		else{
			out.append("wrong username");
		}
		
	}

	private void assemble(Customer customer) {
		customer_info = customer.getName()+"\n";
		customer_info += customer.getGender()+"\n";
		customer_info += customer.getEmail()+"\n";
		customer_info += customer.getImage_url()+"\n"; 
		customer_info += customer.getTelephone();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
