package db;

/**
 * an empty class as an adapter
 * @author xiaoyuw
 *
 */
public class DBHelper extends ProxyDB implements Insert, Delete, Update, Search{
   public DBHelper(String path){
	   super(path);
   }
}
