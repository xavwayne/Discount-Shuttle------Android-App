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

@WebServlet("/Seller_discount_barn")
public class Seller_discount_barn extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String products_info;
	Seller seller;
      
    public Seller_discount_barn() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username;
        username = (String)request.getParameter("username");
        
	    String Path = getServletContext().getRealPath("sql_command.properties");
		DBHelper dbhelper = new DBHelper(Path);
		PrintWriter out = response.getWriter();
		
		LinkedHashMap<Integer,Product> products = dbhelper.searchProductBySeller(username);
		System.out.println("products size:" + products.size());
		seller = dbhelper.searchSellerByUsername(username);
		if(products.size()==0)products_info = "0\tno item";
		else assemble(products);
//		if(products_info == null)products_info = "0\tno item";
        out.append(products_info);
        System.out.println(products_info);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	private void assemble(LinkedHashMap<Integer, Product> products) {
		if (products.size() != 0) {
			products_info = Integer.toString(products.size()) + "\t";
			Set<Integer> keyset = products.keySet();
			for (int key : keyset) {
				Product product = products.get(key);
				products_info += product.getName() + "\t";
			}
			Set<Integer> keyset2 = products.keySet();
			for (int key : keyset2) {
				Product product = products.get(key);
				products_info += product.getImage_url() + "\t";
			}
			products_info = products_info.trim();
		}
	}

}
