package pw.cdmi.box.disk.share.domain;

import java.io.Serializable;
import java.util.Comparator;

public class INodeShareSharedNameSorterV2 implements Comparator<INodeShareV2>, Serializable
{
    
    /**
     * 
     */
    private static final long serialVersionUID = -2270429689987026196L;
    
    @Override
    public int compare(INodeShareV2 o1, INodeShareV2 o2)
    {
        return o1.getSharedUserName().compareTo(o2.getSharedUserName());
    }
    
}
