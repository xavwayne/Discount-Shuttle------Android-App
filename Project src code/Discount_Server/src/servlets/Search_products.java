package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import db.DBHelper;
import model.Product;
import model.Seller;

/**
 * Servlet implementation class Search_products
 */
@WebServlet("/Search_products")
public class Search_products extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String products_info;
	Seller seller;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Search_products() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username;
        username = (String)request.getParameter("pk");
        
	    String Path = getServletContext().getRealPath("sql_command.properties");
		DBHelper dbhelper = new DBHelper(Path);
		PrintWriter out = response.getWriter();
		
		LinkedHashMap<Integer,Product> products = dbhelper.searchProductBySeller(username);
		seller = dbhelper.searchSellerByUsername(username);
		assemble(products);
        out.append(products_info);
        System.out.println(products_info);
	}

	private void assemble(LinkedHashMap<Integer, Product> products) {
		products_info = Integer.toString(products.size())+"\t";
//		if(products.size()!=0)
		   products_info += seller.get_longitude()+";"+seller.get_latitude()+";"+seller.getAudio()+"\t";
		Set<Integer> keyset = products.keySet();
		for(int key : keyset){
			Product product = products.get(key);
		    products_info += product.getName() + ";";
		    products_info += product.getIntroduction() + ";";
		    products_info += product.getRegular_price() + ";";
		    products_info += product.getPrice_now() + ";";
		    products_info += product.getImage_url() + "\t";
		}
		products_info = products_info.trim();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
