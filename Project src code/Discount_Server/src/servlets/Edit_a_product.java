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
 * Servlet implementation class Edit_a_product
 */
@WebServlet("/Edit_a_product")
public class Edit_a_product extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public Edit_a_product() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username;
		String image_url;
		double regular_price;
		double price_now;
		String old_name;
		String new_name;
		String introduction;
		String Path = getServletContext().getRealPath("sql_command.properties");
		DBHelper dbhelper = new DBHelper(Path);
		PrintWriter out = response.getWriter();
		
		username = (String)request.getParameter("username");
		image_url = (String)request.getParameter("image_url");
		regular_price = Double.parseDouble((String)request.getParameter("regular_price"));
		price_now = Double.parseDouble((String)request.getParameter("now_price"));
		old_name = (String)request.getParameter("product_oldname");
		new_name = (String)request.getParameter("product_newname");
		introduction = (String)request.getParameter("introduction");
		
//		System.out.println("username");
//		System.out.println("image_url");
		
		// read from DB
		LinkedHashMap<String, Seller> Sellers_Map = new LinkedHashMap<String, Seller>();
		if (dbhelper.synSeller() != null) {
			Sellers_Map.putAll(dbhelper.synSeller());
		}
		Build_sellers Sellers = new Build_sellers();
		Sellers.Create_sellers_list(Path);
		Sellers.LoadHashMap(Sellers_Map);
		
		// Update
		// Add to DB
		Update_sellers US = new Build_sellers();
		try {
		    US.Update_a_product(username, old_name, image_url, regular_price, price_now, new_name, introduction);
			out.append("1\tYour setting has been saved");
		} catch (DiscountServerException e) {
			out.append("0\t"+e.getMsg());
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
