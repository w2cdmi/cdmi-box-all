package pw.cdmi.box.disk.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public final class PasswordValidateUtil
{
    private PasswordValidateUtil()
    {
        
    }
    
    private final static String PWDRULE_WITH_CHAR = "[a-zA-Z]+";
    
    private final static String PWDRULE_WITH_NUMBER = "[0-9]+";
    
    private final static String PWDRULE_WITH_SCHAR = "[-!@#$^&*+.]+";
    
    private final static List<String> PWDRULE_CHARS = new ArrayList<String>(80);
    
    static
    {
        String charSerial = "0,1,2,3,4,5,6,7,8,9,a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z";
        String[] charSerials = charSerial.split(",");
        for (String tmpChar : charSerials)
        {
            PWDRULE_CHARS.add(tmpChar);
        }
        PWDRULE_CHARS.add("!");
        PWDRULE_CHARS.add("@");
        PWDRULE_CHARS.add("#");
        PWDRULE_CHARS.add("$");
        PWDRULE_CHARS.add("^");
        PWDRULE_CHARS.add("&");
        PWDRULE_CHARS.add("*");
        PWDRULE_CHARS.add("-");
        PWDRULE_CHARS.add("+");
        PWDRULE_CHARS.add(".");
    }
    
    public static boolean isValidPassword(String password,int pwdLevel)
    {
    	if(pwdLevel == 3){
	        if (!StringUtils.isBlank(password) && password.length() >= 6 && password.length() <= 127)
	        {
	            return true;
	        }
    	}else if(pwdLevel == 2){
    		if (!StringUtils.isBlank(password) && password.length() >= 8 && password.length() <= 127)
	        {
	            Pattern pattern = Pattern.compile(PWDRULE_WITH_CHAR);
	            Matcher matcher = pattern.matcher(password);
	            int count = 0;
	            if (matcher.find())
	            {
	            	count++;
	            }
	            pattern = Pattern.compile(PWDRULE_WITH_NUMBER);
	            matcher = pattern.matcher(password);
	            if (matcher.find())
	            {
	            	count++;
	            }
	            pattern = Pattern.compile(PWDRULE_WITH_SCHAR);
	            matcher = pattern.matcher(password);
	            if (matcher.find())
	            {
	            	count++;
	            }
	            int length = password.length();
	            for (int i = 0; i < length; i++)
	            {
	                if (!PWDRULE_CHARS.contains(password.substring(i, i + 1)))
	                {
	                    return false;
	                }
	            }
	            if(count <= 1){
	            	return false;
	            }else{
	            	return true;
	            }
	        }
    	}else{
    		if (!StringUtils.isBlank(password) && password.length() >= 8 && password.length() <= 127)
	        {
	            Pattern pattern = Pattern.compile(PWDRULE_WITH_CHAR);
	            Matcher matcher = pattern.matcher(password);
	            if (!matcher.find())
	            {
	                return false;
	            }
	            pattern = Pattern.compile(PWDRULE_WITH_NUMBER);
	            matcher = pattern.matcher(password);
	            if (!matcher.find())
	            {
	                return false;
	            }
	            pattern = Pattern.compile(PWDRULE_WITH_SCHAR);
	            matcher = pattern.matcher(password);
	            if (!matcher.find())
	            {
	                return false;
	            }
	            int length = password.length();
	            for (int i = 0; i < length; i++)
	            {
	                if (!PWDRULE_CHARS.contains(password.substring(i, i + 1)))
	                {
	                    return false;
	                }
	            }
	            return true;
	        }
    	}
        return false;
        
    }
    
}
