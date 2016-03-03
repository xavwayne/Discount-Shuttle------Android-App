package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import db.DBHelper;
import model.Seller;

/**
 * Servlet implementation class Search
 */
@WebServlet("/Search_sellers")
public class Search_sellers extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String sellers_info;
	private final double EARTH_RADIUS = 6378.137;
	
    public Search_sellers() {
        super();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String store_type="default";
		String latitude = "0.0000";
		String longitude = "0.0000";
		int Store_Type=6;
		String Path = getServletContext().getRealPath("sql_command.properties");
		DBHelper dbhelper = new DBHelper(Path);
		PrintWriter out = response.getWriter();
		
		store_type = (String)request.getParameter("itemtype");
		latitude = (String)request.getParameter("latitude");
		longitude = (String)request.getParameter("longitude");
	
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
//		System.out.println(Store_Type);
		ArrayList<Seller> sellers = dbhelper.searchSellerByType(Store_Type);
//		System.out.println("latitude:" + latitude);
//		System.out.println("longitude:" + longitude);
        if(!latitude.equals("null") && !longitude.equals("null")){
			for (int i = 0; i < sellers.size(); i++) {
				if (sellers.get(i).get_latitude().equals("null") || sellers.get(i).get_longitude().equals("null")) {
					sellers.remove(i);
					i--;
				} else {
					double Distance = DistanceBetween(Double.parseDouble(latitude), Double.parseDouble(longitude),
							Double.parseDouble(sellers.get(i).get_latitude()),
							Double.parseDouble(sellers.get(i).get_longitude()));

					System.out.println("Distance:" + Distance);
					if (Distance > 4) {
						sellers.remove(i);
						i--;
					}
				}
        	}
		}
		assemble(sellers);
		out.append(sellers_info);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	protected void assemble(ArrayList<Seller> sellers){
		Integer number = sellers.size();
		sellers_info = number.toString()+"\t";
		for(int i=0;i<number;i++){
			sellers_info+=sellers.get(i).getStore_name() + ";";
			sellers_info+=sellers.get(i).getIntroduction() + ";";
			sellers_info+=sellers.get(i).getStore_icon_url() + ";";
			if(i!=(number-1))
			    sellers_info+=sellers.get(i).getUsername() + "\t";
			else
				sellers_info+=sellers.get(i).getUsername();
		}
		sellers_info = sellers_info.trim();
	}
	
	private static double rad(double D)
	{
	   return D * Math.PI / 180.0;
	}

	public double DistanceBetween(double lattitude1, double longitude1, double latitude2, double longitude2)
	{
	   double rad1 = rad(lattitude1);
	   double rad2 = rad(latitude2);
	   double a = rad1 - rad2;
	   double b = rad(longitude1) - rad(longitude2);
	   double distance = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2.0) + 
	    Math.cos(rad1)*Math.cos(rad2)*Math.pow(Math.sin(b/2),2)));
	   distance = distance * EARTH_RADIUS;
	   return distance;
	}
}
