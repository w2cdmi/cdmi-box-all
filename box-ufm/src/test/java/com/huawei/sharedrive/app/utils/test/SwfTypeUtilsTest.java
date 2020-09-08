package com.huawei.sharedrive.app.utils.test;

import org.junit.Test;

import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.utils.SwfTypeUtils;

public class SwfTypeUtilsTest
{
    @Test
    public void getSwfTypeTest()
    {
        INode node = new INode();
        node.setType((byte) 0);
        SwfTypeUtils.getSwfType(node);
        node.setType((byte) 1);
        node.setName("testnode");
        SwfTypeUtils.getSwfType(node);
        node.setName("test.node.kk");
        SwfTypeUtils.getSwfType(node);
        node.setName("test.node.");
        SwfTypeUtils.getSwfType(node);
    }
}
