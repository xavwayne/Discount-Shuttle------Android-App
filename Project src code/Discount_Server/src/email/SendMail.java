package email;

import org.apache.commons.mail.*;  
  
public class SendMail extends Thread {  
	private String emailAddr;
	private String username;
	private String password;
	private int type;
    public SendMail(String emailAddr,String username, String password,int type) {  
    	this.emailAddr = emailAddr;
    	this.username = username;
    	this.password = password;
        this.type = type;
    }  
 
    public void run(){
       send();
    }
  
    protected void send() {  
        SimpleEmail email = new SimpleEmail();  
        email.setTLS(true);  
        email.setSSL(true);
        email.setHostName("smtp.gmail.com");
        email.setSmtpPort(465);
    
        
        email.setAuthentication("jerryshi3363296@gmail.com", "nelsondong"); // sender email addr and password  
        
        try {  
        	//according to the sign up user name's email, you should modify this part 
        	//by get email parameters for the ui's post request 
			if (type == 1) {
				email.addTo(emailAddr);
				email.setFrom("jerryshi3363296@gmail.com"); // from where
				email.setSubject("Confirmation Signup From Discount Shuttle"); // title
				// body. you can modify this part ,e.g. add user name.
				String body = "Congrantulations! Your account has been activated!\n";
				body = body + " Your account is " + username + ", and password is " + password + ".";
				email.setMsg(body);
				email.send();
			}
			if(type == 2){
				email.addTo(emailAddr);
				email.setFrom("jerryshi3363296@gmail.com"); // from where
				email.setSubject("Confirmation Signup From Discount Shuttle"); // title
				// body. you can modify this part ,e.g. add user name.
				String body = "The password of your account has been changed";
				body = body + " Your new password is " + password + ".";
				email.setMsg(body);
				email.send();
			}
        } catch (EmailException e) {  
            e.printStackTrace();  
        }  
    }  
} 
