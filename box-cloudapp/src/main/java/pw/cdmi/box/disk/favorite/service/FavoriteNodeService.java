package pw.cdmi.box.disk.favorite.service;

import java.util.List;
import java.util.Locale;

import pw.cdmi.box.disk.favorite.domain.FavoriteNodeCreateRequest;
import pw.cdmi.box.disk.favorite.domain.FavoriteTreeNode;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.core.exception.RestException;

public interface FavoriteNodeService
{
    
    void create(FavoriteNodeCreateRequest favoriteNode, Long userid);
    
    List<FavoriteTreeNode> getFavoriteNodeList(Locale locale, Long id) throws RestException;
    
    boolean deleteTreeNode(Long id);
    
    String getParentUrl(UserToken user, FavoriteTreeNode node);
    
    String locationUrL(FavoriteTreeNode node);
    
}
