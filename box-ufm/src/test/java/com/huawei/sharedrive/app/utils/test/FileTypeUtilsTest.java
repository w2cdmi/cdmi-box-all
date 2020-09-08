package com.huawei.sharedrive.app.utils.test;

import org.junit.Test;

import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.utils.FileTypeUtils;

public class FileTypeUtilsTest
{
    @Test
    public void getFileIconTypeTest()
    {
        INode node = new INode();
        node.setType((byte)0);
        FileTypeUtils.getFileIconType(node);
    }
    
    @Test
    public void getFileIconTypeTest1()
    {
        INode node = new INode();
        node.setType((byte)1);
        node.setName("nodeName");
        FileTypeUtils.getFileIconType(node);
    }
    
    @Test
    public void getFileIconTypeTest2()
    {
        INode node = new INode();
        node.setType((byte)1);
        node.setName("nodeName.sdfdsf");
        FileTypeUtils.getFileIconType(node);
    }
}
