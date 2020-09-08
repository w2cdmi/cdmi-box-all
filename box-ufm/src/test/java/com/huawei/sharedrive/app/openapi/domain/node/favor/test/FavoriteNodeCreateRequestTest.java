package com.huawei.sharedrive.app.openapi.domain.node.favor.test;

import junit.framework.Assert;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.openapi.domain.node.NodeCreateRequest;
import com.huawei.sharedrive.app.openapi.domain.node.favor.FavoriteNodeCreateRequest;

public class FavoriteNodeCreateRequestTest
{
    @Test
    public void paramterTest()
    {
        FavoriteNodeCreateRequest rq = new FavoriteNodeCreateRequest();
        rq.getLinkCode();
        rq.getName();
        rq.getNode();
        rq.getParent();
        rq.getType();
        rq.setLinkCode("linkCode");
        rq.setName("nodeOne");
        rq.setNode(new NodeCreateRequest());
        rq.setParent(12314L);
        rq.setType("1");
        rq.builderFavoriteNode();
        Assert.assertEquals((Long) 0L, FavoriteNodeCreateRequest.TREE_ROOT_ID);
        Assert.assertEquals("myspace", FavoriteNodeCreateRequest.MYSPACE);
        Assert.assertEquals("teamspace", FavoriteNodeCreateRequest.TEAMSPACE);
        Assert.assertEquals("share", FavoriteNodeCreateRequest.SHARE);
        Assert.assertEquals("link", FavoriteNodeCreateRequest.LINK);
        Assert.assertEquals("containor", FavoriteNodeCreateRequest.CONTAINOR);
        FavoriteNodeCreateRequest.typeStringToInt(null);
        FavoriteNodeCreateRequest.typeStringToInt("containor");
        FavoriteNodeCreateRequest.typeStringToInt("myspace");
        FavoriteNodeCreateRequest.typeStringToInt("teamspace");
        FavoriteNodeCreateRequest.typeStringToInt("share");
        FavoriteNodeCreateRequest.typeStringToInt("link");
        try
        {
            rq.checkParamter();
        }
        catch (BaseRunException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
