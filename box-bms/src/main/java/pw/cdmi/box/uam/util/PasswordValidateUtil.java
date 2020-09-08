package pw.cdmi.box.uam.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public final class PasswordValidateUtil
{
    
    private final static String PWD_WITH_CHAR = "[a-zA-Z]+";
    
    private final static String PWD_WITH_NUMBER = "[0-9]+";
    
    private final static String PWD_WITH_SCHAR = "[-!@#$^&*+.]+";
    
    private final static List<String> PWD_CHARS = new ArrayList<String>(80);
    
    private PasswordValidateUtil()
    {
    }
    
    static
    {
        String charSerial = "0,1,2,3,4,5,6,7,8,9,a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z";
        String[] charSerials = charSerial.split(",");
        for (String tmpChar : charSerials)
        {
            PWD_CHARS.add(tmpChar);
        }
        PWD_CHARS.add("!");
        PWD_CHARS.add("@");
        PWD_CHARS.add("#");
        PWD_CHARS.add("$");
        PWD_CHARS.add("^");
        PWD_CHARS.add("&");
        PWD_CHARS.add("*");
        PWD_CHARS.add("-");
        PWD_CHARS.add("+");
        PWD_CHARS.add(".");
    }
    
    public static boolean isValidPassword(String password)
    {
        return isValidPassword(password, 20);
    }
    
    public static boolean isValidPassword(String password, int maxLength)
    {
        if (!StringUtils.isBlank(password) && password.length() >= 8 && password.length() <= maxLength)
        {
            Pattern pattern = Pattern.compile(PWD_WITH_CHAR);
            Matcher matcher = pattern.matcher(password);
            if (!matcher.find())
            {
                return false;
            }
            pattern = Pattern.compile(PWD_WITH_NUMBER);
            matcher = pattern.matcher(password);
            if (!matcher.find())
            {
                return false;
            }
            pattern = Pattern.compile(PWD_WITH_SCHAR);
            matcher = pattern.matcher(password);
            if (!matcher.find())
            {
                return false;
            }
            int length = password.length();
            for (int i = 0; i < length; i++)
            {
                if (!PWD_CHARS.contains(password.substring(i, i + 1)))
                {
                    return false;
                }
            }
            return true;
        }
        return false;
        
    }
    
}
