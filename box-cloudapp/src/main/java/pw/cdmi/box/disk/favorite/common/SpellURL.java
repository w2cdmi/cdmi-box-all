package pw.cdmi.box.disk.favorite.common;

import pw.cdmi.box.disk.favorite.domain.FavoriteNode;
import pw.cdmi.box.disk.favorite.domain.Node;

public final class SpellURL
{
    
    public static final String FILE_PAGE_ONE = "1";
    
    public static final String SEPARATOR = "/";
    
    public static final String OWENER = "/#file/";
    
    public static final String TEAMSPACE1 = "/teamspace/file/";
    
    public static final String TEAMSPACE2 = "#file/1/";
    
    public static final String SHARE = "/shared/list/";
    
    public static final String SHAREFILE = "/shared/";
    
    public static final String LINK = "/p/";
    
    private SpellURL()
    {
        
    }
    
    public static String getURL(String type, Long owerId, Long nodeId, String linkCode, String nodeType)
    {
        StringBuffer sb = new StringBuffer();
        if (type.equals(FavoriteNode.MYSPACE))
        {
            sb = getURLForFile(sb, nodeId);
        }
        else if (type.equals(FavoriteNode.TEAMSPACE))
        {
            sb = getURLForTeamSpace(sb, owerId, nodeId);
        }
        else if (type.equals(FavoriteNode.SHARE))
        {
            sb = getURLForShare(sb, owerId, nodeId, nodeType);
        }
        else if (type.equals(FavoriteNode.LINK))
        {
            sb = getURLForShareLink(sb, linkCode);
        }
        return sb.toString();
    }
    
    private static StringBuffer getURLForFile(StringBuffer sb, Long nodeId)
    {
        sb.append(OWENER).append(FILE_PAGE_ONE).append('/').append(nodeId);
        return sb;
    }
    
    private static StringBuffer getURLForShare(StringBuffer sb, Long owerId, Long nodeId, String nodeType)
    {
        if (nodeType.equals(Node.FOLDER))
        {
            sb.append(SHARE).append(owerId).append('/').append(nodeId);
        }
        else
        {
            sb.append(SHAREFILE);
        }
        
        return sb;
    }
    
    private static StringBuffer getURLForTeamSpace(StringBuffer sb, Long owerId, Long nodeId)
    {
        sb.append(TEAMSPACE1).append(owerId).append(TEAMSPACE2).append(nodeId);
        return sb;
    }
    
    private static StringBuffer getURLForShareLink(StringBuffer sb, String linkCode)
    {
        sb.append(LINK).append(linkCode);
        return sb;
    }
}
