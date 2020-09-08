package com.huawei.sharedrive.app.openapi.domain.teamspace.test;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamSpaceCreateRequest;

public class RestTeamSpaceCreateRequestTest
{
    @Test
    public void checkParameterTest()
    {
        try
        {
            RestTeamSpaceCreateRequest rr = new RestTeamSpaceCreateRequest();
            rr.setName("123");
            rr.getName();
            rr.setRegionId((byte) 123);
            rr.getRegionId();
            rr.setDescription("qweqweqwe");
            rr.getDescription();
            rr.setMaxMembers(123213);
            rr.getMaxMembers();
            rr.setMaxVersions(234);
            rr.getMaxVersions();
            rr.setStatus(123);
            rr.getStatus();
            rr.setSpaceQuota(1234L);
            rr.getSpaceQuota();
            rr.checkParameter();
        }
        catch (InvalidParamException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void checkParameterTest1()
    {
        try
        {
            RestTeamSpaceCreateRequest rr = new RestTeamSpaceCreateRequest();
            rr.setRegionId((byte) 123);
            rr.getRegionId();
            rr.setDescription("qweqweqwe");
            rr.getDescription();
            rr.setMaxMembers(123213);
            rr.getMaxMembers();
            rr.setMaxVersions(234);
            rr.getMaxVersions();
            rr.setStatus(123);
            rr.getStatus();
            rr.setSpaceQuota(1234L);
            rr.getSpaceQuota();
            rr.checkParameter();
            rr.setName("");
            rr.checkParameter();
            rr.setName("234234");
            rr.setDescription("");
            rr.checkParameter();
            rr.setDescription("23234234");
            rr.checkParameter();
        }
        catch (InvalidParamException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void checkParameterTest2()
    {
        RestTeamSpaceCreateRequest rr = new RestTeamSpaceCreateRequest();
        rr.setRegionId((byte) 123);
        rr.getRegionId();
        rr.setDescription("qweqweqwe");
        rr.getDescription();
        rr.setMaxMembers(123213);
        rr.getMaxMembers();
        rr.setMaxVersions(234);
        rr.getMaxVersions();
        rr.setStatus(123);
        rr.getStatus();
        rr.setSpaceQuota(1234L);
        rr.getSpaceQuota();
        try
        {
            rr.checkParameter();
        }
        catch (BaseRunException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        rr.setName("");
        try
        {
            rr.checkParameter();
        }
        catch (BaseRunException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        rr.setName("234234");
        rr.setDescription("");
        try
        {
            rr.checkParameter();
        }
        catch (BaseRunException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        rr.setDescription("23234234");
        try
        {
            rr.checkParameter();
        }
        catch (BaseRunException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
