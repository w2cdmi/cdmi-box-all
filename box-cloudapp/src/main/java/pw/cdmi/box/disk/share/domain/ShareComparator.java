package pw.cdmi.box.disk.share.domain;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang.StringUtils;

public class ShareComparator implements Comparator<INodeShare>, Serializable
{
    
    /**
     * 
     */
    private static final long serialVersionUID = -1558167694387767029L;
    
    private String direct;
    
    private static final String DIRECT_ASC = "ASC";
    
    private static final String DIRECT_DESC = "DESC";
    
    @Override
    public int compare(INodeShare o1, INodeShare o2)
    {
        if (StringUtils.equals(DIRECT_DESC, direct))
        {
            return Byte.compare(o2.getSharedUserType(), o1.getSharedUserType());
        }
        else if (StringUtils.equals(DIRECT_ASC, direct))
        {
            return Byte.compare(o1.getSharedUserType(), o2.getSharedUserType());
        }
        return Byte.compare(o1.getSharedUserType(), o2.getSharedUserType());
    }
    
    public ShareComparator(String direct)
    {
        super();
        this.direct = direct;
    }
}
