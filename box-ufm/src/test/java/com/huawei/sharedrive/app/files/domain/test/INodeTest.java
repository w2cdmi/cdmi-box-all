package com.huawei.sharedrive.app.files.domain.test;

import java.util.Date;

import org.junit.Test;

import com.huawei.sharedrive.app.core.domain.ThumbnailUrl;
import com.huawei.sharedrive.app.files.domain.INode;

public class INodeTest
{
    @Test
    public void inodeTest()
    {
        INode srcNode = new INode();
        INode valueOf = INode.valueOf(srcNode);
        System.out.println(valueOf);
    }
    
    @Test
    public void addThumbnailUrlTest()
    {
        ThumbnailUrl url = new ThumbnailUrl();
        INode srcNode = new INode();
        srcNode.addThumbnailUrl(url);
    }
    
    @Test
    public void getBlockMD5Test()
    {
        INode srcNode = new INode();
        String blockMD5 = srcNode.getBlockMD5();
        System.out.println(blockMD5);
    }
    
    @Test
    public void getContentCreatedAtTest()
    {
        INode srcNode = new INode();
        Date contentCreatedAt = srcNode.getContentCreatedAt();
        System.out.println(contentCreatedAt);
    }
    
    @Test
    public void getContentModifiedAtTest()
    {
        INode srcNode = new INode();
        Date contentCreatedAt = srcNode.getContentModifiedAt();
        System.out.println(contentCreatedAt);
    }
    
    @Test
    public void getCreatedAtTest()
    {
        INode srcNode = new INode();
        Date contentCreatedAt = srcNode.getCreatedAt();
        System.out.println(contentCreatedAt);
    }
    
    @Test
    public void getCreatedByTest()
    {
        INode srcNode = new INode();
        long createdBy = srcNode.getCreatedBy();
        System.out.println(createdBy);
    }
    
    @Test
    public void getModifiedAtTest()
    {
        INode srcNode = new INode();
        Date contentCreatedAt = srcNode.getModifiedAt();
        System.out.println(contentCreatedAt);
    }
    
    @Test
    public void setContentCreatedAtTest()
    {
        INode srcNode = new INode();
        srcNode.setContentCreatedAt(new Date());
    }
    
    @Test
    public void setContentCreatedAtTest2()
    {
        INode srcNode = new INode();
        srcNode.setContentCreatedAt(null);
    }
    
    @Test
    public void setContentModifiedAtTest()
    {
        INode srcNode = new INode();
        srcNode.setContentModifiedAt(new Date());
    }
    
    @Test
    public void setContentModifiedAtTest2()
    {
        INode srcNode = new INode();
        srcNode.setContentModifiedAt(null);
    }
    
    @Test
    public void setCreatedAtTest()
    {
        INode srcNode = new INode();
        srcNode.setCreatedAt(new Date());
    }
    
    @Test
    public void setCreatedAtTest2()
    {
        INode srcNode = new INode();
        srcNode.setCreatedAt(null);
    }
    
    @Test
    public void setModifiedAtTest()
    {
        INode srcNode = new INode();
        srcNode.setModifiedAt(new Date());
    }
    
    @Test
    public void setModifiedAtTest2()
    {
        INode srcNode = new INode();
        srcNode.setModifiedAt(null);
    }
    
    @Test
    public void copyFromTest2()
    {
        INode srcNode = new INode();
        srcNode.copyFrom(srcNode);
    }
}
