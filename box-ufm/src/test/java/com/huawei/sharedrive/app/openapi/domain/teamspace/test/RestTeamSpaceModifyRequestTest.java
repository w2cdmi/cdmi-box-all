package com.huawei.sharedrive.app.openapi.domain.teamspace.test;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamSpaceModifyRequest;

public class RestTeamSpaceModifyRequestTest
{
    @Test
    public void checkParameterTest()
    {
        try
        {
            RestTeamSpaceModifyRequest rr = new RestTeamSpaceModifyRequest();
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
            RestTeamSpaceModifyRequest rr = new RestTeamSpaceModifyRequest();
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
        try
        {
            RestTeamSpaceModifyRequest rr = new RestTeamSpaceModifyRequest();
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
            rr.setSpaceQuota(0L);
            rr.checkParameter();
            rr.setSpaceQuota(123123L);
            rr.setSpaceQuota(123L);
            rr.checkParameter();
            rr.setSpaceQuota(1L);
            rr.setMaxMembers(0);
            rr.checkParameter();
            rr.setMaxMembers(123);
            rr.setMaxMembers(0);
            rr.checkParameter();
        }
        catch (InvalidParamException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void checkParameterTest3()
    {
        RestTeamSpaceModifyRequest rr = new RestTeamSpaceModifyRequest();
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
        catch (BaseRunException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
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
        rr.setSpaceQuota(0L);
        try
        {
            rr.checkParameter();
        }
        catch (BaseRunException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        rr.setSpaceQuota(123123L);
        rr.setSpaceQuota(123L);
        try
        {
            rr.checkParameter();
        }
        catch (BaseRunException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        rr.setSpaceQuota(1L);
        rr.setMaxMembers(0);
        try
        {
            rr.checkParameter();
        }
        catch (BaseRunException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        rr.setMaxMembers(123);
        rr.setMaxMembers(0);
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
