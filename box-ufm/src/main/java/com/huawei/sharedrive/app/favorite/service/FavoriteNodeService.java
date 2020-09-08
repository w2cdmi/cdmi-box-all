package com.huawei.sharedrive.app.favorite.service;

import java.util.List;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.favorite.domain.FavoriteNode;
import com.huawei.sharedrive.app.openapi.domain.node.favor.FavoriteNodeList;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

public interface FavoriteNodeService
{

    FavoriteNode createFavoriteNode(FavoriteNode favoriteNode,String linkCode) throws BaseRunException;

    FavoriteNodeList listNodesbyParent(FavoriteNode filter, List<Order> orderList, Limit limit,String keyword)
        throws BaseRunException;

    boolean deleteFavoriteNode(Long id,Long userId) throws BaseRunException;

    FavoriteNode getFavoriteNode(Long userId, Long id) throws BaseRunException;
    
}
