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
import adapter.Create_delete_customers;
import db.DBHelper;
import email.SendMail;
import exception.DiscountServerException;
import model.Customer;


/**
 * Servlet implementation class Sign_up
 */
@WebServlet("/Sign_up_customer")
public class Sign_up_customer extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

    public Sign_up_customer() {
        super();
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username="default";
		String password="default";
		String name="default";
		String email="default@default.com";
		String phone_number="default";
		String gender="default";
//		String user_type;
		String url="default";
		String Path = getServletContext().getRealPath("sql_command.properties");
		DBHelper dbhelper = new DBHelper(Path);
		
		username = (String)request.getParameter("username");
		password = (String)request.getParameter("password");
		name = (String)request.getParameter("name");
		email = (String)request.getParameter("email");if(email == null) email ="";if(email.contains("@")) email ="a@a.com";
		phone_number = (String)request.getParameter("phonenum");
		gender = (String)request.getParameter("gender");
		url = (String)request.getParameter("image");
		PrintWriter out = response.getWriter();
		
			LinkedHashMap<String, Customer> Customers_Map = new LinkedHashMap<String, Customer>();
			if(dbhelper.synCustomer()!=null){
			    Customers_Map.putAll(dbhelper.synCustomer());
			}
			Build_customers customers = new Build_customers();
			customers.Create_customers_list(Path);
			customers.LoadHashMap(Customers_Map);
			
			Create_delete_customers CDC = new Build_customers();
			try {
				CDC.Add_a_customer(username, name, password, gender, email, url, phone_number);
				out.append("Your account has been created successfully");
	            SendMail registrationMail = new SendMail(email,username,password,1);
	            registrationMail.start();
			} catch (DiscountServerException e) {
				out.append(e.getMsg());
			}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
