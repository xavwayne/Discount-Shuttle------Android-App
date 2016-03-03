package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import db.DBHelper;
import model.Product;

/**
 * Servlet implementation class Seller_edit_product
 */
@WebServlet("/Seller_get_product")
public class Seller_get_product extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Seller_get_product() {
        super();

    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String username="default";
		String productname="default";
		String reg_price="default";
		String now_price="default";
		String intro="default";
		String image="default";

		
		boolean success = false;
		String Path = getServletContext().getRealPath("sql_command.properties");
		DBHelper dbhelper = new DBHelper(Path);
		PrintWriter out = response.getWriter();
		
		username=request.getParameter("username");
		productname=request.getParameter("product_name");
		
//		System.out.println(username);
//		System.out.println(productname);
		LinkedHashMap<Integer,Product> products=dbhelper.searchProductBySeller(username);
		for(Product x:products.values()){
			if(x.getName().equals(productname)){
				reg_price=String.format("%.2f", x.getRegular_price());
				now_price=String.format("%.2f", x.getPrice_now());
				intro=x.getIntroduction();
				image=x.getImage_url();
				success=true;
				break;
			}
		}
		
		if(success){
			String result="1"+"\t"+productname+"\t"+reg_price+"\t"+now_price+"\t"+intro+"\t"+image;
			out.append(result);
		}else{
			out.append("0\tNot Found!!");
		}
		
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
