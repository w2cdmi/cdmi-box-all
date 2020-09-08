package com.huawei.sharedrive.app.files.domain.test;

import org.junit.Test;

import com.huawei.sharedrive.app.files.domain.NodeType;

public class NodeTypeTest
{
    @Test
    public void getNodeTypeTest()
    {
        byte b = 'a';
        NodeType nodeType = NodeType.getNodeType(b);
        System.out.println(nodeType);
    }
    
    @Test
    public void getNodeTypeTest2()
    {
        NodeType nodeType = NodeType.getNodeType("abc");
        System.out.println(nodeType);
    }
    
    @Test
    public void getValueTest()
    {
        byte value = NodeType.getValue("file");
        System.out.println(value);
    }
}
