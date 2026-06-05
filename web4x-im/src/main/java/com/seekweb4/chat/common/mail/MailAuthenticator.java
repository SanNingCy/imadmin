package com.seekweb4.chat.common.mail;
/**   
 *  
 */

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;

public class MailAuthenticator extends Authenticator{   
    String userName=null;   
    String password=null;   
        
    public MailAuthenticator(){   
    }   
    public MailAuthenticator(String username, String password) {    
        this.userName = username;    
        this.password = password;    
    }    
    protected PasswordAuthentication getPasswordAuthentication(){   
        return new PasswordAuthentication(userName, password);   
    }   
}   
