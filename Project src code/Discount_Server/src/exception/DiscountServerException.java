package exception;

/**
 * Created by Joe on 7/18/2015.
 */
@SuppressWarnings("serial")
/*
 * Exceptions:
 * No.1: "Username already exists" exception.
 * No.2: "Seller username not found" exception.
 * No.3: "Product already exists" exception
 * No.4: "product not Found" exception
 * No.5: "Customer not Found" exception
 * */
public class DiscountServerException extends Exception{
    private int number;
    private String msg;
    
    public DiscountServerException(int number) {
        super();
        this.number = number;
        switch(this.number){
           case 1:
        	   this.msg = "Username already exists";
        	  break;
           case 2:
        	   this.msg = "No such seller in Server";
        	   break;
           case 3:
        	   this.msg = "The product has already existed";
        	   break;
           case 4:
        	   this.msg = "product not Found";
        	   break;
           case 5:
        	   this.msg = "No such customer in Server";
        	   break;
           default:
        	   this.msg = "Unknown error";
        }
    }

    public int getNumber(){
        return number;
    }

    public String getMsg(){
        return msg;
    }

}
