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
 * Servlet implementation class Delete_a_product
 */
@WebServlet("/Delete_a_product")
public class Delete_a_product extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public Delete_a_product() {
        super();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username;
		String name;
		String Path = getServletContext().getRealPath("sql_command.properties");
		DBHelper dbhelper = new DBHelper(Path);
		PrintWriter out = response.getWriter();
		
		username = (String)request.getParameter("username");
		name = (String)request.getParameter("productname");
		
		LinkedHashMap<String, Seller> Sellers_Map = new LinkedHashMap<String, Seller>();
		if (dbhelper.synSeller() != null) {
			Sellers_Map.putAll(dbhelper.synSeller());
		}
		Build_sellers Sellers = new Build_sellers();
		Sellers.Create_sellers_list(Path);
		Sellers.LoadHashMap(Sellers_Map);
		
		// Delete
		Update_sellers US = new Build_sellers();
		try {
			US.Delete_a_product(username, name);
			out.append("1\tThe product has been deleted");
		} catch (DiscountServerException e) {
			out.append("0\t" + e.getMsg());
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
