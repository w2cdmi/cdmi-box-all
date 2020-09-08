package com.huawei.sharedrive.app.favorite.dao;

import java.util.List;

import com.huawei.sharedrive.app.favorite.domain.FavoriteNode;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

public interface FavoriteNodeDao
{
    
    int delete(FavoriteNode node);
    
    Long create(FavoriteNode node);
    
    int update(FavoriteNode node);
    
    FavoriteNode getFavoriteNodeBy(FavoriteNode favoriteNode);
    
    int getSubFavortieNodeCount(FavoriteNode node,String keyword);
    
    List<FavoriteNode> getFavoriteNodeFilterd(FavoriteNode filter, List<Order> orderList, Limit limit);
    
    List<FavoriteNode> getFavoriteNodeByParent(FavoriteNode filter, List<Order> orderList, Limit limit,String keyword);

    FavoriteNode getFavortieNode(Long id, Long ownedBy);
    
    long getMaxId(long ownedBy);
}
