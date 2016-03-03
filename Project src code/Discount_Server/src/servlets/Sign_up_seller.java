package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import adapter.Build_sellers;
import adapter.Create_delete_sellers;
import db.DBHelper;
import email.SendMail;
import exception.DiscountServerException;
import model.Seller;

/**
 * Servlet implementation class Sign_up_seller
 */
@WebServlet("/Sign_up_seller")
public class Sign_up_seller extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public Sign_up_seller() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username="default";
		String password="default";
		String email="default@default.com";
		String telephone="default";
		String store_name="default";
		String Audio_url="default";
		String store_icon_url="default";
		String introduction="default";
		String store_type="default";
		String latitude="default";
		String longitude="default";
		int Store_Type=6;
		String Path = getServletContext().getRealPath("sql_command.properties");
		DBHelper dbhelper = new DBHelper(Path);
		PrintWriter out = response.getWriter();
		
		store_type = (String)request.getParameter("shoptype");
		username = (String)request.getParameter("username");
		password = (String)request.getParameter("password");
		email = (String)request.getParameter("email");
		telephone = (String)request.getParameter("phonenum");
		store_name = (String)request.getParameter("shopname");
		Audio_url = (String)request.getParameter("audio");
		store_icon_url = (String)request.getParameter("image");
		introduction = (String)request.getParameter("introduction");
		latitude = (String)request.getParameter("latitude");
		longitude = (String)request.getParameter("longitude");
		System.out.println("store_type:"+store_type);
		System.out.println("username:"+username);
		System.out.println("password:"+password);
		System.out.println("email:"+email);
		System.out.println("telephone:"+telephone);
		System.out.println("store_name:"+store_name);
		System.out.println("Audio_url:"+Audio_url);
		System.out.println("store_icon_url:"+store_icon_url);
		System.out.println("introduction:"+introduction);
		System.out.println("latitude:"+latitude);
		System.out.println("longitude:"+longitude);
		
		switch(store_type){
		    case "Food":
		    	Store_Type =1;
		    	break;
		    case "Pharmacy":
		    	Store_Type =2;
		    	break;
		    case  "Cosmetics":
		    	Store_Type =3;
		    	break;
		    case  "Drink":
		    	Store_Type =4;
		    	break;
		    case  "Clothes":
		    	Store_Type =5;
		    	break;
		    default:
		    	Store_Type =6;
		}
		
		
			LinkedHashMap<String, Seller> Sellers_Map = new LinkedHashMap<String, Seller>();
			if (dbhelper.synSeller() != null) {
				Sellers_Map.putAll(dbhelper.synSeller());
			}
			Build_sellers Sellers = new Build_sellers();
			Sellers.Create_sellers_list(Path);
			Sellers.LoadHashMap(Sellers_Map);

			Create_delete_sellers CDS = new Build_sellers();
			try {
				CDS.Add_a_seller(username, password, email, telephone, store_name, store_icon_url, introduction,
						Audio_url, Store_Type, latitude, longitude);
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
