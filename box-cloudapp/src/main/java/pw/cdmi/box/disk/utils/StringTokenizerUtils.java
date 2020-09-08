package pw.cdmi.box.disk.utils;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public final class StringTokenizerUtils
{
    private StringTokenizerUtils()
    {
        
    }
    
    public static Set<String> wsString(String key)
    {
        HashSet<String> result = new HashSet<String>(BusinessConstants.INITIAL_CAPACITIES);
        
        StringTokenizer st = new StringTokenizer(key, PatternRegUtil.getSelfRule());
        String s;
        while (st.hasMoreTokens())
        {
            s = (String) st.nextElement();
            if (s.trim().length() > 0)
            {
                result.add(s.trim());
            }
        }
        return result;
    }
}
