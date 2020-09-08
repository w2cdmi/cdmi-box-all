package pw.cdmi.box.disk.user.domain;

import java.io.Serializable;
import java.util.Comparator;

public class UserAdSorter implements Comparator<User>, Serializable
{
    
    /**
     * 
     */
    private static final long serialVersionUID = -8505528432675392225L;
    
    @Override
    public int compare(User o1, User o2)
    {
        return o1.getName().compareTo(o2.getName());
    }
    
}
