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
import adapter.Update_sellers;
import db.DBHelper;
import exception.DiscountServerException;
import model.Seller;

/**
 * Servlet implementation class Edit_a_seller
 */
@WebServlet("/Edit_a_seller")
public class Edit_a_seller extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public Edit_a_seller() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username;
		String email;
		String telephone;
		String store_name;
		String store_icon_url;
		String introduction;
		String store_type;
		String latitude;
		String longitude;
		String Audio_url;
		int Store_Type;
		String Path = getServletContext().getRealPath("sql_command.properties");
		DBHelper dbhelper = new DBHelper(Path);
		PrintWriter out = response.getWriter();
		
		username = (String)request.getParameter("username");
		email = (String)request.getParameter("email");
		telephone = (String)request.getParameter("phonenum");
		store_name = (String)request.getParameter("shopname");
		store_icon_url = (String)request.getParameter("image");
		introduction = (String)request.getParameter("introduction");
		store_type = (String)request.getParameter("shoptype");
		latitude = (String)request.getParameter("latitude");
		longitude = (String)request.getParameter("longitude");
		Audio_url = (String)request.getParameter("audio");
		switch (store_type) {
		case "food":
			Store_Type = 1;
			break;
		case "Pharmacy":
			Store_Type = 2;
			break;
		case "Cosmetics":
			Store_Type = 3;
			break;
		case "Drink":
			Store_Type = 4;
			break;
		case "Clothes":
			Store_Type = 5;
			break;
		default:
			Store_Type = 1;
		}
		// read from DB
		LinkedHashMap<String, Seller> Sellers_Map = new LinkedHashMap<String, Seller>();
		if (dbhelper.synSeller() != null) {
			Sellers_Map.putAll(dbhelper.synSeller());
		}
		Build_sellers Sellers = new Build_sellers();
		Sellers.Create_sellers_list(Path);
		Sellers.LoadHashMap(Sellers_Map);
		
		System.out.println(Audio_url);
		// Update
		Update_sellers US = new Build_sellers();
		Seller seller = Sellers_Map.get(username);
		try {
			US.Update_a_seller(username, seller.getPassword(), email, telephone, store_name, store_icon_url, 
					                     introduction, Store_Type, latitude, longitude, Audio_url);
			out.append("Your setting has been saved");
		} catch (DiscountServerException e) {
			out.append("Error: "+e.getMsg());
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
