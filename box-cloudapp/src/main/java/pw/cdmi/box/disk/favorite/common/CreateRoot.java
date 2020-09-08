package pw.cdmi.box.disk.favorite.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pw.cdmi.box.disk.favorite.domain.FavoriteNode;
import pw.cdmi.box.disk.favorite.domain.FavoriteNodeCreateRequest;
import pw.cdmi.box.disk.favorite.domain.FavoriteTreeNode;
import pw.cdmi.box.disk.utils.CustomUtils;

public final class CreateRoot
{
    private CreateRoot()
    {
        
    }
    
    public static List<FavoriteNodeCreateRequest> getFavoriteNodeCreateRequest(Long userid)
    {
        List<FavoriteNodeCreateRequest> list = new ArrayList<FavoriteNodeCreateRequest>(4);
        list.add(getRootNode(userid, typeToName(FavoriteNode.MYSPACE)));
        list.add(getRootNode(userid, typeToName(FavoriteNode.SHARE)));
        list.add(getRootNode(userid, typeToName(FavoriteNode.LINK)));
        list.add(getRootNode(userid, typeToName(FavoriteNode.TEAMSPACE)));
        return list;
    }
    
    public static FavoriteNodeCreateRequest getRootNode(Long userid, String name)
    {
        FavoriteNodeCreateRequest createRequest = new FavoriteNodeCreateRequest();
        createRequest.setType(FavoriteNode.CONTAINOR);
        createRequest.setName(name);
        createRequest.setParentId(FavoriteNode.TREE_ROOT_ID);
        return createRequest;
    }
    
    public static Long getId(List<FavoriteTreeNode> list, FavoriteNodeCreateRequest favoriteNode)
    {
        String key = typeToName(favoriteNode.getType());
        FavoriteTreeNode node = rootListCoventToMap(list).get(key);
        if (null != node)
        {
            return node.getId();
        }
        return null;
    }
    
    public static Map<String, FavoriteTreeNode> rootListCoventToMap(List<FavoriteTreeNode> list)
    {
        Map<String, FavoriteTreeNode> map = new HashMap<String, FavoriteTreeNode>(list.size());
        String name;
        for (FavoriteTreeNode node : list)
        {
            name = node.getName();
            if (name != null && !"".equals(name))
            {
                map.put(node.getName(), node);
            }
            else
            {
                map.put(node.getNodeName(), node);
            }
        }
        return map;
    }
    
    public static String typeToName(String type)
    {
        if (type.equals(FavoriteNode.MYSPACE))
        {
            return FavoriteType.MYFILE.getName();
        }
        if (type.equals(FavoriteNode.TEAMSPACE))
        {
            return FavoriteType.TEAMSPACE.getName();
        }
        if (type.equals(FavoriteNode.SHARE))
        {
            return FavoriteType.SHARE.getName();
        }
        if (type.equals(FavoriteNode.LINK))
        {
            if ("true".equals(CustomUtils.getValue("link.hidden")))
            {
                return FavoriteType.LINK_FOR_OTHER.getName();
            }
            return FavoriteType.LINK.getName();
        }
        throw new IllegalArgumentException("Con't find type ! type is " + type);
    }
    
}
