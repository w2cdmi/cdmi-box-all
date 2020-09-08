package pw.cdmi.box.disk.utils;

public final class LinkCodeCaculator
{
    private LinkCodeCaculator()
    {
        
    }
    
    private static final int MOD = 62;
    
    private static final byte OWNERID_LENGTH = 4;
    
    public static long getINodeId(String linkCode)
    {
        String ownerId62 = linkCode.substring(4);
        long value = 0;
        int size = ownerId62.length();
        for (int i = 0; i < size; i++)
        {
            value = value * MOD + convertToDigital(ownerId62.charAt(i));
        }
        return value;
    }
    
    public static String getLinkCode(long owenerId, long iNodeId)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(getStringLen4(getData62(owenerId)));
        builder.append(getData62(iNodeId));
        return builder.toString();
    }
    
    public static long getOwnerId(String linkCode)
    {
        String ownerId62 = linkCode.substring(0, 4);
        long value = 0;
        for (int i = 0; i < ownerId62.length(); i++)
        {
            value = value * MOD + convertToDigital(ownerId62.charAt(i));
        }
        return value;
    }
    
    private static char convertToChar(int data)
    {
        if (data < 10)
        {
            return (char) (data + 48);
        }
        if (data < 36)
        {
            return (char) (data + 55);
        }
        return (char) (data + 61);
    }
    
    private static int convertToDigital(char ch)
    {
        byte by = (byte) ch;
        if (by < 58)
        {
            return by - 48;
        }
        if (by < 91)
        {
            return (char) (by - 55);
        }
        return by - 61;
    }
    
    private static String getData62(long data)
    {
        StringBuilder builder = new StringBuilder();
        long temp = data;
        int tail = 0;
        while (temp != 0)
        {
            tail = (int) (temp % MOD);
            temp = temp / MOD;
            builder.append(convertToChar(tail));
        }
        return builder.reverse().toString();
    }
    
    private static String getStringLen4(String str)
    {
        StringBuilder builder = new StringBuilder(str);
        int length = builder.length();
        while (length < OWNERID_LENGTH)
        {
            builder.insert(0, '0');
            length++;
        }
        return builder.toString();
    }
}
