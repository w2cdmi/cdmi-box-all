package pw.cdmi.box.disk.share.domain;

import java.io.Serializable;
import java.util.Comparator;

public class INodeShareSharedNameSorter implements Comparator<INodeShare>, Serializable
{
    
    /**
     * 
     */
    private static final long serialVersionUID = -3634423622870562215L;
    
    @Override
    public int compare(INodeShare o1, INodeShare o2)
    {
        return o1.getSharedUserName().compareTo(o2.getSharedUserName());
    }
    
}
