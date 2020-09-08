package com.huawei.sharedrive.app.favorite.domain;

import org.junit.Test;

public class NodeTest
{
    @Test
    public void nodeTest()
    {
        Node node = new Node();
        node.equals(null);
    }
    
    @Test
    public void nodeTest2()
    {
        Node node = new Node();
        node.getClass();
    }
    
    @Test
    public void nodeTest3()
    {
        Node node = new Node();
        node.getId();
    }
    
    @Test
    public void nodeTest4()
    {
        Node node = new Node();
        node.getOwnedBy();
    }
    
    @Test
    public void nodeTest5()
    {
        Node node = new Node();
        node.getType();
    }
    
    @Test
    public void nodeTest6()
    {
        Node node = new Node();
        node.setId(1l);
    }
    
    @Test
    public void nodeTest7()
    {
        Node node = new Node();
        node.setOwnedBy(1l);
    }
    
    @Test
    public void nodeTest8()
    {
        Node node = new Node();
        node.setType(null);
    }
}
