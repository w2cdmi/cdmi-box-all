package pw.cdmi.box.disk.share.domain;

public final class LinkAccessCodeMode
{
    public static final byte TYPE_STATIC_VALUE = 1;
    
    public static final byte TYPE_MAIL_VALUE = 2;
    
    public static final byte TYPE_PHONE_VALUE = 3;
    
    public static final String TYPE_STATIC_STRING = "static";
    
    public static final String TYPE_MAIL_STRING = "mail";
    
    public static final String TYPE_PHONE_STRING = "phone";
    
    private LinkAccessCodeMode()
    {
        
    }
    
    public static byte transTypeToValue(String input)
    {
        if (TYPE_MAIL_STRING.equals(input))
        {
            return TYPE_MAIL_VALUE;
        }
        
        if (TYPE_PHONE_STRING.equals(input))
        {
            return TYPE_PHONE_VALUE;
        }
        
        return TYPE_STATIC_VALUE;
    }
    
    public static String transTypeToString(byte input)
    {
        if (TYPE_MAIL_VALUE == input)
        {
            return TYPE_MAIL_STRING;
        }
        
        if (TYPE_PHONE_VALUE == input)
        {
            return TYPE_PHONE_STRING;
        }
        
        return TYPE_STATIC_STRING;
    }
    
    public static boolean contains(String input)
    {
        if (TYPE_STATIC_STRING.equals(input))
        {
            return true;
        }
        
        if (TYPE_MAIL_STRING.equals(input))
        {
            return true;
        }
        
        if (TYPE_PHONE_STRING.equals(input))
        {
            return true;
        }
        return false;
    }
}
