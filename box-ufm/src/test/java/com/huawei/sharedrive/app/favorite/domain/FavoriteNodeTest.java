package com.huawei.sharedrive.app.favorite.domain;

import java.util.Date;

import org.junit.Test;

public class FavoriteNodeTest
{
    @Test
    public void favoriteNodeTest()
    {
        FavoriteNode fav = new FavoriteNode();
        fav.equals(null);
    }
    
    @Test
    public void favoriteNodeTest2()
    {
        FavoriteNode fav = new FavoriteNode();
        fav.getClass();
        fav.getCreatedAt();
        fav.getId();
        fav.getModifiedAt();
        fav.getName();
        fav.getNode();
        fav.getOwnedBy();
        fav.getType();
        fav.getParent();
        fav.getParams();
        fav.setCreatedAt(new Date());
        fav.setId(1l);
        fav.setModifiedAt(new Date());
        fav.setName("test");
        fav.setNode(null);
        fav.setOwnedBy(1l);
        fav.setParams("s");
        fav.setParent(1l);
        fav.setPreviewable(false);
        fav.setType(1);
    }
}
