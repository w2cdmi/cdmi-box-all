package com.huawei.sharedrive.app.dataserver.domain;

import org.junit.Test;

public class ResourceGroupTest
{
    @Test
    public void resourceGroupTest()
    {
        ResourceGroup res = new ResourceGroup();
        res.addNode(null);
    }
    
    @Test
    public void resourceGroupTest2()
    {
        ResourceGroup res = new ResourceGroup();
        res.equals(null);
        res.getClass();
    }
    
    @Test
    public void resourceGroupTest3()
    {
        ResourceGroup res = new ResourceGroup();
        res.getAccessKey();
    }
    
    @Test
    public void resourceGroupTest4()
    {
        ResourceGroup res = new ResourceGroup();
        res.getDcId();
        res.getDomainName();
        res.getGetProtocol();
        res.getId();
        res.getLastReportTime();
        res.getManageIp();
        res.getManagePort();
        res.getNodes();
        res.getPutProtocol();
        res.getRegionId();
        res.getRuntimeStatus();
        res.getServiceHttpPort();
        res.getServiceHttpsPort();
        res.getServicePath();
        res.getType();
        res.getStatus();
    }
    
    @Test
    public void resourceGroupTest5()
    {
        ResourceGroup res = new ResourceGroup();
        res.setAccessKey("abc123");
        res.setDcId(0);
        res.setDomainName("test");
        res.setGetProtocol("http");
        res.setId(1);
        res.setLastReportTime(1234456l);
        res.setManageIp("127.0.0.4");
        res.setManagePort(8080);
        res.setNodes(null);
        res.setPutProtocol("https");
        res.setRegionId(1);
        res.setServiceHttpPort(8081);
        res.setServiceHttpsPort(8443);
    }
}
