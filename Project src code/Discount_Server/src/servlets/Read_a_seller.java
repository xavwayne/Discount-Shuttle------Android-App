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
import db.DBHelper;
import model.Seller;

/**
 * Servlet implementation class Read_a_seller
 */
@WebServlet("/Read_a_seller")
public class Read_a_seller extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String sellers_info;
    public Read_a_seller() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username;
		username = (String)request.getParameter("username");
		String Path = getServletContext().getRealPath("sql_command.properties");
		DBHelper dbhelper = new DBHelper(Path);
		PrintWriter out = response.getWriter();
		
		LinkedHashMap<String, Seller> Sellers_Map = new LinkedHashMap<String, Seller>();
		if (dbhelper.synSeller() != null) {
			Sellers_Map.putAll(dbhelper.synSeller());
		}
		Build_sellers Sellers = new Build_sellers();
		Sellers.Create_sellers_list(Path);
		Sellers.LoadHashMap(Sellers_Map);
		Seller seller;
		
		seller = Sellers.Read_a_Seller(username);
		if(seller != null){
			assemble(seller);
			out.append(sellers_info);
		}
		else{
			out.append("There is a error in server");
		}
	}

	private void assemble(Seller seller) {
		sellers_info = seller.getStore_name()+"\n";
		sellers_info += seller.getIntroduction()+"\n";
		sellers_info += seller.getEmail()+"\n";
		sellers_info += seller.getTelephone()+"\n";
		sellers_info += seller.getStore_icon_url()+"\n";
        sellers_info += seller.getAudio() + "\n";
		sellers_info += seller.get_latitude()+"\n";
		sellers_info += seller.get_longitude()+"\n";
		sellers_info += seller.getStore_type()+"\n";
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
