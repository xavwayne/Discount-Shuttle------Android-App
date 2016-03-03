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
import adapter.Update_customers;
import db.DBHelper;
import exception.DiscountServerException;
import model.Customer;

/**
 * Servlet implementation class Edit_a_customer
 */
@WebServlet("/Edit_a_customer")
public class Edit_a_customer extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public Edit_a_customer() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username;
		String name;
		String email;
		String phone_number;
		String gender;
		String url;
		PrintWriter out = response.getWriter();
		String Path = getServletContext().getRealPath("sql_command.properties");
		DBHelper dbhelper = new DBHelper(Path);
		
		username = (String)request.getParameter("username");
		name = (String)request.getParameter("name");
		email = (String)request.getParameter("email");
		phone_number = (String)request.getParameter("phonenum");
		gender = (String)request.getParameter("gender");
		url = (String)request.getParameter("image");
		
		// Read data from DB
		LinkedHashMap<String, Customer> Customers_Map = new LinkedHashMap<String, Customer>();
		if(dbhelper.synCustomer()!=null){
		    Customers_Map.putAll(dbhelper.synCustomer());
		}
		Build_customers customers = new Build_customers();
		customers.Create_customers_list(Path);
		customers.LoadHashMap(Customers_Map);
		Customer customer = customers.Read_a_customer(username);
		
		// Update 
		Update_customers UC = new Build_customers();
		try {
			UC.Update_a_customer(username, customer.getPassword(), name, gender, email, url, phone_number, 1);
			out.append("Your setting has been saved");
		} catch (DiscountServerException e) {
			out.append(e.getMsg());
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
